package com.codurance.gr8craft.featuresmocked

import com.codurance.gr8craft.Gr8craft
import com.codurance.gr8craft.Gr8craftFactory._
import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.model.twitter.{DirectMessageId, DirectMessage, Tweet, TwitterService}
import com.codurance.gr8craft.util.AkkaSteps

import scala.concurrent.duration._

class MockedStepDefinitions extends AkkaSteps("MockedStepDefinitions") {
  private var application: Gr8craft = null

  private var directMessages: List[DirectMessage] = List()

  private val twitterService = new TwitterService {
    var tweet: String = null

    def tweetSent: String = this.tweet

    override def fetchDirectMessagesAfter(lastFetched: Option[DirectMessageId], successAction: (List[DirectMessage]) => Unit): Unit = successAction(directMessages)

    override def tweet(tweet: Tweet, successAction: () => Unit, failureAction: () => Unit): Unit = {
      this.tweet = tweet.toString
      successAction.apply()
    }
  }

  Given( """^the next inspiration on the shelf about "([^"]*)" can be found at "([^"]*)"$""") {

    (topic: String, location: String) =>
      application = createApplication(system, twitterService, Set(new Inspiration(topic, location)), 1.millisecond)
  }

  When( """^the hour is reached$""") {
    () =>
      twitterService.tweet = null
      application.startTwitterBot()
  }

  Then( """^gr8craft tweets "([^"]*)"$""") {
    (expectedTweet: String) =>
      awaitCond(twitterService.tweetSent != null, 1.second)
      twitterService.tweetSent.toString shouldBe expectedTweet
  }

  Given( """^"([^"]*)" sends a DM to gr8craft with the text "([^"]*)"$""") {
    (sender: String, messageText: String) =>
      directMessages = directMessages :+ new DirectMessage(sender, messageText, DirectMessageId(42))
      application = createApplication(system, twitterService, tweetInterval = 1.second)
  }
}
