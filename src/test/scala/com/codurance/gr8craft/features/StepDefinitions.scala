package com.codurance.gr8craft.features

import com.codurance.gr8craft.Gr8craft
import com.codurance.gr8craft.Gr8craftFactory.createApplication
import com.codurance.gr8craft.infrastructure.TwitterApiService
import com.codurance.gr8craft.infrastructure.TwitterFactoryWithConfiguration.createTwitter
import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.util.AkkaSteps
import twitter4j._

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class StepDefinitions extends AkkaSteps("StepDefinitions") {

  private var application: Gr8craft = null
  private val twitter = createTwitter()
  private val twitterService = new TwitterApiService(twitter)

  Before() { _ =>
    twitter.getUserTimeline
      .asScala
      .foreach(status => twitter.destroyStatus(status.getId))

    twitter.getDirectMessages
      .asScala
      .foreach(message => twitter.destroyDirectMessage(message.getId))
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
      getNewestTweet.map(_.getText).get shouldEqual expectedTweet
  }

  Given( """^"([^"]*)" sends a DM to gr8craft with the text "([^"]*)"$""") {
    (sender: String, directMessage: String) =>
      application = createApplication(system, twitterService, tweetInterval = 1.second)
      sendDirectMessage(createTwitter(sender), directMessage)
  }

  private def getNewestTweet: Option[Status] = {
    eventually(timeout(3.seconds), interval(1.second)) {
      val newestTweet = requestNewestTweet()
      newestTweet.isDefined shouldBe true
      newestTweet
    }
  }

  private def requestNewestTweet(): Option[Status] = {
    twitter.getUserTimeline
      .asScala
      .headOption
  }

  private def sendDirectMessage(sender: Twitter, directMessage: String): Unit = {
    eventually(timeout(3.seconds), interval(1.second)) {
      sender.sendDirectMessage(twitter.getId, directMessage)
    }

    eventually(timeout(3.seconds), interval(1.second)) {
      getNewestDM(twitter).isDefined shouldBe true
    }
  }

  private def getNewestDM(twitter: Twitter): Option[DirectMessage] = {
    twitter.getDirectMessages
      .asScala
      .headOption
  }
}
