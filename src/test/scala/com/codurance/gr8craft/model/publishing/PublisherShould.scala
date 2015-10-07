package com.codurance.gr8craft.model.publishing

import akka.actor.Props
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.util.AkkaTest
import org.junit.runner.RunWith
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PublisherShould extends AkkaTest("PublisherShould") with ScalaFutures {
  private val inspiration = new Inspiration("topic", "location")

  private val tweetSender = new TweetSender {
    override def tweet(tweet: Tweet, successAction: () => Unit): Unit = {
      successAction.apply()
    }
  }

  private val publisher = system.actorOf(Props(new Publisher(tweetSender)))

  test("forward tweets to the TweetSender") {
    publisher ! GoAndTweet(inspiration)

    expectMsg(SuccessfullyTweeted(inspiration))
  }
}
