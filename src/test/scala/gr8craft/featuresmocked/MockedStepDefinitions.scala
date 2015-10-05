package gr8craft.featuresmocked

import java.time.LocalDateTime

import gr8craft.ApplicationFactory._
import gr8craft.inspiration.Inspiration
import gr8craft.messages.{DirectMessage, Done, Message}
import gr8craft.twitter.{Tweet, TwitterService}
import gr8craft.{AkkaSteps, ApplicationRunner}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class MockedStepDefinitions extends AkkaSteps("MockedStepDefinitions") {
  var directMessage: String = null
  var sender: String = null
  val twitterService = new TwitterService {
    var tweet: String = null

    def tweetSent: String = this.tweet

    override def tweet(tweet: String): Future[Message] = {
      Future {
        this.tweet = tweet
        Done
      }
    }

    override def getDirectMessagesFrom(startingTime: LocalDateTime): Future[Set[DirectMessage]] = {
      Future {
        Set(new DirectMessage(sender, directMessage))
      }
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

  Given( """^gr8craft receives a mention from "([^"]*)" with the recommendation "([^"]*)"$""") {
    (contributor: String, recommendation: String) =>

  }

  Then( """^"([^"]*)" receives a DM from gr8craft saying "([^"]*)"$""") {
    (recipient: String, message: String) =>

  }

  Given( """^"([^"]*)" sends a DM to gr8craft with the text "([^"]*)"$""") {
    (sender: String, directMessage: String) =>
      application = createApplication(system, twitterService, tweetInterval = 1.second)
      this.directMessage = directMessage
      this.sender = sender
  }
}
