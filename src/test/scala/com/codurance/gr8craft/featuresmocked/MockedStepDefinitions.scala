package com.codurance.gr8craft.featuresmocked

import com.codurance.gr8craft.Gr8craftFactory._
import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.model.twitter.{DirectMessage, Tweet, TwitterService}
import com.codurance.gr8craft.util.AkkaSteps
import com.codurance.gr8craft.{Gr8craftFactory, Gr8craft}

import scala.concurrent.duration._

class MockedStepDefinitions extends AkkaSteps("MockedStepDefinitions") {

  var directMessages: List[DirectMessage] = List()
  val twitterService = new TwitterService {
    var tweet: String = null

    def tweetSent: String = this.tweet

    override def fetchDirectMessagesAfter(lastFetched: Option[Long], successAction: (List[DirectMessage]) => Unit): Unit = successAction.apply(directMessages)

    override def tweet(tweet: Tweet, successAction: () => Unit, failureAction: () => Unit): Unit = {
      this.tweet = tweet.toString
      successAction.apply()
    }
  }

  var application: Gr8craft = null

  Given( """^the next inspiration on the shelf about "([^"]*)" can be found at "([^"]*)"$""") {
    (topic: String, location: String) =>
      application = createApplication(system, twitterService, Set(new Inspiration(topic, location)), 1.millisecond)
  }

  When( """^the hour is reached$""") {
    () =>
      application.startTwitterBot()
  }

  Then( """^gr8craft tweets "([^"]*)"$""") {
    (expectedTweet: String) =>
      awaitCond(twitterService.tweetSent != null, 1.second)
      twitterService.tweetSent.toString shouldBe expectedTweet
  }

  Given( """^"([^"]*)" sends a DM to gr8craft with the text "([^"]*)"$""") {
    (sender: String, messageText: String) =>
      directMessages = directMessages :+ new DirectMessage(sender, messageText, 42L)
      application = createApplication(system, twitterService, tweetInterval = 1.second)
  }
}
