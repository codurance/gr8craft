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

  private var twitterRequestSuccessful: Boolean = true

  private val twitterService = new TwitterService {
    override def tweet(tweet: Tweet, successAction: () => Unit, failureAction: () => Unit): Unit = {
      if (twitterRequestSuccessful) successAction.apply() else failureAction.apply()
    }

    override def fetchDirectMessagesAfter(lastFetched: Option[DirectMessageId], successAction: (List[DirectMessage]) => Unit): Unit = {}
  }

  private val publisher = system.actorOf(Props(new Publisher(twitterService)))

  test("forward tweets to Twitter") {
    twitterRequestSuccessful = true

    publisher ! GoAndTweet(inspiration)

    expectMsg(SuccessfullyTweeted(inspiration))
  }

  test("informs of unsuccessful tweets") {
    twitterRequestSuccessful = false

    publisher ! GoAndTweet(inspiration)

    expectMsg(FailedToTweet(inspiration))
  }
}
