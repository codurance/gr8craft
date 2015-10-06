package gr8craft.twitter

import akka.actor.Props
import gr8craft.AkkaTest
import gr8craft.inspiration.Inspiration
import gr8craft.messages._
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner

import scala.concurrent.Future.{failed, successful}

@RunWith(classOf[JUnitRunner])
class TweeterShould extends AkkaTest("TweeterShould") with MockFactory with ScalaFutures {

  val topic: String = "topic"
  val location: String = "location"
  val contributor: String = "contributor"

  val anotherTopic: String = "anotherTopic"
  val anotherLocation: String = "anotherLocation"
  val anotherContributor: String = "anotherContributor"

  val inspiration = new Inspiration(topic, location)

  val directMessage: DirectMessage = DirectMessage("gr8craftmod", "inspiration: " + topic + " | location: " + location + " | contributor: " + contributor, 1L)
  val laterDirectMessage: DirectMessage = DirectMessage("gr8craftmod", "inspiration: " + anotherTopic + " | location: " + anotherLocation + " | contributor: " + anotherContributor, 2L)
  val foreignMessage: DirectMessage = DirectMessage("someone else", "inspiration: " + topic + " | location: " + location + " | contributor: " + contributor, 3L)
  val lastRequested = 42L

  var twitterRequestSuccessful: Boolean = true
  var directMessages: List[DirectMessage] = List()

  val twitterService = new TwitterService {
    override def tweet(tweet: Tweet, successAction: () => Unit, failureAction: () => Unit): Unit = {
      if (twitterRequestSuccessful) successAction.apply() else failureAction.apply()
    }

    override def fetchDirectMessagesAfter(lastFetched: Option[Long], successAction: (List[DirectMessage]) => Unit): Unit = {
      successAction.apply(directMessages)
    }
  }

  val tweeter = system.actorOf(Props(new Tweeter(twitterService)))

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

    expectMsg(GotDirectMessage(directMessage))
    expectMsg(GotDirectMessage(laterDirectMessage))
  }
}
