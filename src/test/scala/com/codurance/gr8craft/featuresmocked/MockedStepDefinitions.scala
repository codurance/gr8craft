package com.codurance.gr8craft.featuresmocked

import com.codurance.gr8craft.Gr8craft.createApplication
import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.model.publishing._
import com.codurance.gr8craft.model.research.{DirectMessage, DirectMessageFetcher, DirectMessageId}
import com.codurance.gr8craft.util.AkkaSteps

import scala.concurrent.duration._

class MockedStepDefinitions extends AkkaSteps("MockedStepDefinitions") {
  private var initialInspirations: Set[Inspiration] = Set()

  private val tweetSender = new TweetSender {
    var tweet: String = null

    override def tweet(tweet: Tweet): Unit = {
      this.tweet = tweet.toString
    }
  }

  private val directMessageFetcher = new DirectMessageFetcher {
    var directMessages: List[DirectMessage] = List()

    override def fetchAfter(lastFetched: Option[DirectMessageId], successAction: (List[DirectMessage]) => Unit): Unit = successAction(directMessages)
  }

  Given( """^the next inspiration on the shelf about "([^"]*)" can be found at "([^"]*)"$""") {
    (topic: String, location: String) =>
      initialInspirations = Set(Inspiration(topic, location))
  }

  When( """^the hour is reached$""") {
    () =>
      deletePreviousTweets()
      createApplication(system, tweetSender, directMessageFetcher, initialInspirations, 1.millisecond).startTwitterBot()
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
    tweetSender.tweet = null
  }

  private def awaitNewestTweet(): String = {
    awaitCond(tweetSender.tweet != null, 4.second)
    tweetSender.tweet
  }

  def deletePreviousDirectMessages() = {
    directMessageFetcher.directMessages = List()
  }

  private def sendDirectMessage(sender: String, messageText: String): Unit = {
    directMessageFetcher.directMessages = directMessageFetcher.directMessages :+ new DirectMessage(sender, messageText, DirectMessageId(42))
  }
}
