package com.codurance.gr8craft.featuresmocked

import com.codurance.gr8craft.Gr8craftFactory._
import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.model.publishing.{DirectMessage, DirectMessageId, Tweet, TwitterService}
import com.codurance.gr8craft.util.AkkaSteps

import scala.concurrent.duration._

class MockedStepDefinitions extends AkkaSteps("MockedStepDefinitions") {
  private var initialInspirations: Set[Inspiration] = Set()

  private val twitterService = new TwitterService {
    var directMessages: List[DirectMessage] = List()
    var tweet: String = null

    override def fetchDirectMessagesAfter(lastFetched: Option[DirectMessageId], successAction: (List[DirectMessage]) => Unit): Unit = successAction(directMessages)

    override def tweet(tweet: Tweet, successAction: () => Unit, failureAction: () => Unit): Unit = {
      this.tweet = tweet.toString
      successAction.apply()
    }
  }

  Given( """^the next inspiration on the archivist about "([^"]*)" can be found at "([^"]*)"$""") {
    (topic: String, location: String) =>
      initialInspirations = Set(Inspiration(topic, location))
  }

  When( """^the hour is reached$""") {
    () =>
      deletePreviousTweets()
      createApplication(system, twitterService, initialInspirations, 1.millisecond).startTwitterBot()
  }

  Then( """^gr8craft tweets "([^"]*)"$""") {
    (expectedTweet: String) =>
      awaitNewestTweet().toString shouldEqual expectedTweet
  }

  Given( """^"([^"]*)" sends a DM to gr8craft with the text "([^"]*)"$""") {
    (sender: String, messageText: String) =>
      deletePreviousDirectMessages()
      sendDirectMessage(sender, messageText)
  }


  private def deletePreviousTweets(): Unit = {
    twitterService.tweet = null
  }

  private def awaitNewestTweet(): String = {
    awaitCond(twitterService.tweet != null, 4.second)
    twitterService.tweet
  }

  def deletePreviousDirectMessages() = {
    twitterService.directMessages = List()
  }

  private def sendDirectMessage(sender: String, messageText: String): Unit = {
    twitterService.directMessages = twitterService.directMessages :+ new DirectMessage(sender, messageText, DirectMessageId(42))
  }
}
