package gr8craft.features

import gr8craft.ApplicationFactory.createApplication
import gr8craft.TwitterFactoryWithConfiguration.createTwitter
import gr8craft.inspiration.Inspiration
import gr8craft.twitter.TwitterApiService
import gr8craft.{AkkaSteps, ApplicationRunner}
import twitter4j._

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class StepDefinitions extends AkkaSteps("StepDefinitions") {

  val twitter = createTwitter()
  val twitterService = new TwitterApiService(twitter)
  var application: ApplicationRunner = null
  var newestTweet: Option[Status] = Option.empty
  var newestMessage: Option[DirectMessage] = Option.empty
  var dmSent = false
  var deleted = false

  Before() {
    _ =>
      deleteExistingTimeline()
      eventually(timeout(2.seconds), interval(1.second)) {
        assert(deleted)
      }
  }

  Given( """^the next inspiration on the shelf about "([^"]*)" can be found at "([^"]*)"$""") {
    (topic: String, location: String) =>
      application = createApplication(system, twitterService, Set(new Inspiration(topic, location)), 1.second)
  }

  When( """^the hour is reached$""") {
    () =>
      application.startTwitterBot()
  }

  Then( """^gr8craft tweets "([^"]*)"$""") {
    (expectedTweet: String) =>
      eventually(timeout(100.seconds), interval(1.second)) {
        requestNewestTweet()
        newestTweet.isDefined shouldBe true
      }
      newestTweet.map(_.getText).get shouldEqual expectedTweet
  }

  Given( """^gr8craft receives a mention from "([^"]*)" with the recommendation "([^"]*)"$""") {
    (contributor: String, recommendation: String) =>
      application = createApplication(system, twitterService, tweetInterval = 1.second)
      val modTwitter = createTwitter(contributor)
      val twitterServiceMod = new TwitterApiService(modTwitter)
      twitterServiceMod.tweet("@gr8crafttest " + recommendation)
  }

  Then( """^"([^"]*)" receives a DM from gr8craft saying "([^"]*)"$""") {
    (recipient: String, message: String) =>
      val recipientTwitter = createTwitter(recipient)

      eventually(timeout(100.seconds), interval(1.second)) {
        requestNewestDM(recipientTwitter)
        newestMessage.isDefined shouldBe true
      }
      newestMessage.map(_.getText).get shouldEqual message
  }

  Given( """^"([^"]*)" sends a DM to gr8craft with the text "([^"]*)"$""") {
    (sender: String, directMessage: String) =>
      application = createApplication(system, twitterService, tweetInterval = 1.second)
      val senderTwitter = createTwitter(sender)
      twitter.addListener(new TwitterAdapter() {
        dmSent = true
      })

      senderTwitter.sendDirectMessage(twitter.getId, directMessage)

      eventually(timeout(100.seconds), interval(1.second)) {
        assert(dmSent)
      }
  }

  private def deleteExistingTimeline(): Unit = {
    twitter.addListener(new TwitterAdapter() {
      override def gotUserTimeline(statuses: ResponseList[Status]): Unit =
        statuses.asScala
          .foreach(status => twitter.destroyStatus(status.getId))

      deleted = true
    })
    twitter.getUserTimeline()
  }

  private def requestNewestTweet(): Unit = {
    twitter.addListener(new TwitterAdapter() {
      override def gotUserTimeline(statuses: ResponseList[Status]): Unit =
        newestTweet = statuses.asScala.headOption
    })
    twitter.getUserTimeline()
  }

  private def requestNewestDM(modTwitter: AsyncTwitter): Unit = {
    modTwitter.addListener(new TwitterAdapter() {
      override def gotDirectMessages(messages: ResponseList[DirectMessage]): Unit =
        newestMessage = messages.asScala.headOption
    })
    modTwitter.getDirectMessages()
  }
}
