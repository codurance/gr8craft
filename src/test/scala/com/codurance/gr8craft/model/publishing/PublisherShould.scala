package com.codurance.gr8craft.model.publishing

import akka.actor.Props
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.util.AkkaTest
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PublisherShould extends AkkaTest("PublisherShould") with ScalaFutures {
  private val inspiration = new InspirationBuilder().build()

  private val tweetSender = new TweetSender {
    var sentTweet: Tweet = null

    override def tweet(tweet: Tweet): Unit = sentTweet = tweet
  }

  private val publisher = system.actorOf(Props(new Publisher(tweetSender)))

  test("forward tweets to the TweetSender") {
    publisher ! Publish(inspiration)

    awaitCond(tweetSender.sentTweet != null)

    tweetSender.sentTweet shouldEqual new Tweet(inspiration)
  }
}
