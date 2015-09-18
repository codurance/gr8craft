package gr8craft.twitter

import akka.actor.Props
import gr8craft.AkkaTest
import gr8craft.inspiration.Inspiration
import gr8craft.messages.{Done, FailedToTweet, SuccessfullyTweeted, Tweet}
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.junit.JUnitRunner

import scala.concurrent.Future.{failed, successful}


@RunWith(classOf[JUnitRunner])
class TweeterShould extends AkkaTest("TweeterShould") with MockFactory with ScalaFutures {
  val twitterService = mock[TwitterService]

  val topic: String = "topic"
  val location: String = "location"
  val inspiration = new Inspiration(topic, location)

  val tweeter = system.actorOf(Props(new Tweeter(twitterService)))

  test("forward tweets to Twitter") {
    val future = successful(Done)
    (twitterService.tweet _).expects(s"Your hourly recommended inspiration about $topic: $location")
      .returns(future)

    tweeter ! Tweet(inspiration)

    expectMsg(SuccessfullyTweeted(inspiration))

  }

  test("informs of unsuccessful tweets") {
    (twitterService.tweet _).expects(s"Your hourly recommended inspiration about $topic: $location")
      .returns(failed(new RuntimeException()))

    tweeter ! Tweet(inspiration)

    expectMsg(FailedToTweet(inspiration))

  }
}
