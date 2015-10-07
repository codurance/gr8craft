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
  private val gr8craftTwitter = createTwitter()
  private val twitterService = new TwitterApiService(gr8craftTwitter)

  Before() { _ =>
    deletePreviousDirectMessages()
  }

  Given( """^the next inspiration on the shelf about "([^"]*)" can be found at "([^"]*)"$""") {
    (topic: String, location: String) =>
      application = createApplication(system, twitterService, Set(new Inspiration(topic, location)), 1.second)
  }

  When( """^the hour is reached$""") {
    deletePreviousTweets()
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

  private def deletePreviousDirectMessages(): Unit = {
    gr8craftTwitter.getDirectMessages
      .asScala
      .foreach(message => gr8craftTwitter.destroyDirectMessage(message.getId))
  }

  private def deletePreviousTweets(): Unit = {
    gr8craftTwitter.getUserTimeline
      .asScala
      .foreach(status => gr8craftTwitter.destroyStatus(status.getId))
  }

  private def getNewestTweet: Option[Status] = {
    eventually(timeout(10.seconds), interval(1.second)) {
      val newestTweet = requestNewestTweet()
      newestTweet.isDefined shouldBe true
      newestTweet
    }
  }

  private def requestNewestTweet(): Option[Status] = {
    gr8craftTwitter.getUserTimeline
      .asScala
      .headOption
  }

  private def sendDirectMessage(sender: Twitter, directMessage: String): Unit = {
    sender.sendDirectMessage(gr8craftTwitter.getId, directMessage)

    eventually(timeout(10.seconds), interval(1.second)) {
      requestNewestDirectMessage().isDefined shouldBe true
    }
  }

  private def requestNewestDirectMessage(): Option[DirectMessage] = {
    gr8craftTwitter.getDirectMessages
      .asScala
      .headOption
  }
}
