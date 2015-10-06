package gr8craft.features

import gr8craft.ApplicationFactory.createApplication
import gr8craft.TwitterFactoryWithConfiguration.createTwitter
import gr8craft.inspiration.Inspiration
import gr8craft.twitter.{Tweet, TwitterApiService}
import gr8craft.{AkkaSteps, ApplicationRunner}
import twitter4j._

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class StepDefinitions extends AkkaSteps("StepDefinitions") {

  val twitter = createTwitter()
  val twitterService = new TwitterApiService(twitter)
  var application: ApplicationRunner = null

  Before() { _ =>
    twitter.getUserTimeline
      .asScala
      .foreach(status => twitter.destroyStatus(status.getId))
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
        val newestTweet = requestNewestTweet()
        newestTweet.isDefined shouldBe true
        newestTweet
      }.map(_.getText).get shouldEqual expectedTweet
  }

  Given( """^"([^"]*)" sends a DM to gr8craft with the text "([^"]*)"$""") {
    (sender: String, directMessage: String) =>
      application = createApplication(system, twitterService, tweetInterval = 1.second)
      clearDirectMessagesAndSend(createTwitter(sender), directMessage)
  }


  private def requestNewestTweet(): Option[Status] = {
    twitter.getUserTimeline
      .asScala
      .headOption
  }

  private def clearDirectMessagesAndSend(sender: Twitter, directMessage: String): Unit = {
    deleteExistingPrivateMessages(sender)

    sender.sendDirectMessage(twitter.getId, directMessage)

    eventually(timeout(100.seconds), interval(1.second)) {
      getNewestDM(twitter).getText shouldBe directMessage
    }
  }

  private def deleteExistingPrivateMessages(sender: Twitter): Unit = {
    sender.getSentDirectMessages
      .asScala
      .foreach(message => twitter.destroyDirectMessage(message.getId))
  }

  private def getNewestDM(twitter: Twitter): DirectMessage = {
    twitter.getDirectMessages
      .asScala
      .head
  }
}
