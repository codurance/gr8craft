package gr8craft.twitter

import java.time.LocalDateTime

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

  val directMessage: DirectMessage = DirectMessage("gr8craftmod", "inspiration: " + topic + " | location: " + location + " | contributor: " + contributor)
  val laterDirectMessage: DirectMessage = DirectMessage("gr8craftmod", "inspiration: " + anotherTopic + " | location: " + anotherLocation + " | contributor: " + anotherContributor)
  val foreignMessage: DirectMessage = DirectMessage("someone else", "inspiration: " + topic + " | location: " + location + " | contributor: " + contributor)
  val lastRequested = LocalDateTime.now

  val twitterService = mock[TwitterService]
  val tweeter = system.actorOf(Props(new Tweeter(twitterService)))

  test("forward tweets to Twitter") {
    (twitterService.tweet _)
      .expects(inspiration.toString)
      .returns(successful(Done))

    tweeter ! GoAndTweet(inspiration)

    expectMsg(SuccessfullyTweeted(inspiration))
  }

  test("informs of unsuccessful tweets") {
    (twitterService.tweet _).expects(inspiration.toString)
      .returns(failed(new RuntimeException()))

    tweeter ! GoAndTweet(inspiration)

    expectMsg(FailedToTweet(inspiration))
  }

  test("don't accept direct messages that do not come from the moderator") {
    (twitterService.getDirectMessagesFrom _)
      .expects(lastRequested)
      .returns(successful(Set(foreignMessage)))

    tweeter ! FetchDirectMessages(lastRequested)

    expectNoMsg()
  }

  test("fetch direct messages from moderator and recommend authors from it") {
    (twitterService.getDirectMessagesFrom _)
      .expects(lastRequested)
      .returns(successful(Set(directMessage, laterDirectMessage)))

    tweeter ! FetchDirectMessages(lastRequested)

    expectMsg(AddInspiration(new Inspiration(topic, location, Option.apply(contributor))))
    expectMsg(AddInspiration(new Inspiration(anotherTopic, anotherLocation, Option.apply(anotherContributor))))
  }
}
