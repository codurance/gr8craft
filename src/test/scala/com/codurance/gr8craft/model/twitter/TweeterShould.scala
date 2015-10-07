package com.codurance.gr8craft.model.twitter

import akka.actor.Props
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.util.AkkaTest
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TweeterShould extends AkkaTest("TweeterShould") with ScalaFutures {

  private val topic: String = "topic"
  private val location: String = "location"
  private val contributor: String = "contributor"

  private val anotherTopic: String = "anotherTopic"
  private val anotherLocation: String = "anotherLocation"
  private val anotherContributor: String = "anotherContributor"

  private val inspiration = new Inspiration(topic, location)

  private val directMessage: DirectMessage = DirectMessage("gr8craftmod", "inspiration: " + topic + " | location: " + location + " | contributor: " + contributor, DirectMessageId(1L))
  private val laterDirectMessage: DirectMessage = DirectMessage("gr8craftmod", "inspiration: " + anotherTopic + " | location: " + anotherLocation + " | contributor: " + anotherContributor, DirectMessageId(2L))
  private val foreignMessage: DirectMessage = DirectMessage("someone else", "inspiration: " + topic + " | location: " + location + " | contributor: " + contributor, DirectMessageId(3L))
  private val lastRequested = DirectMessageId(42L)

  private var twitterRequestSuccessful: Boolean = true
  private var directMessages: List[DirectMessage] = List()

  private val twitterService = new TwitterService {
    override def tweet(tweet: Tweet, successAction: () => Unit, failureAction: () => Unit): Unit = {
      if (twitterRequestSuccessful) successAction.apply() else failureAction.apply()
    }

    override def fetchDirectMessagesAfter(lastFetched: Option[DirectMessageId], successAction: (List[DirectMessage]) => Unit): Unit = {
      successAction.apply(directMessages)
    }
  }

  private val tweeter = system.actorOf(Props(new Tweeter(twitterService)))

  test("forward tweets to Twitter") {
    twitterRequestSuccessful = true

    tweeter ! GoAndTweet(inspiration)

    expectMsg(SuccessfullyTweeted(inspiration))
  }

  test("informs of unsuccessful tweets") {
    twitterRequestSuccessful = false

    tweeter ! GoAndTweet(inspiration)

    expectMsg(FailedToTweet(inspiration))
  }

  test("don't accept direct messages that do not come from the moderator") {
    directMessages = List(foreignMessage)

    tweeter ! FetchDirectMessages(Some(lastRequested))

    expectNoMsg()
  }

  test("fetch direct messages from moderator and forward them") {
    directMessages = List(directMessage, laterDirectMessage)

    tweeter ! FetchDirectMessages(Some(lastRequested))

    expectMsg(AddDirectMessage(directMessage))
    expectMsg(AddDirectMessage(laterDirectMessage))
  }
}
