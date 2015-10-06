package gr8craft.featuresmocked

import gr8craft.ApplicationFactory._
import gr8craft.inspiration.Inspiration
import gr8craft.messages.{Done, Message}
import gr8craft.twitter.{Tweet, DirectMessage, TwitterService}
import gr8craft.{AkkaSteps, ApplicationRunner}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
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

  var application: ApplicationRunner = null

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
