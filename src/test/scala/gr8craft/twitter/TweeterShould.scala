package gr8craft.twitter

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, TestKit}
import gr8craft.inspiration.{Inspiration, Shelf}
import gr8craft.messages.{Tweet, AddInspiration, Trigger}
import gr8craft.scheduling.ScheduledExecutor
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuiteLike
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TweeterShould extends TestKit(ActorSystem("TweeterShould")) with FunSuiteLike with MockFactory {
  val twitterService = mock[TwitterService]
  val tweet = "my tweet"
  val tweeter = TestActorRef(Props(new Tweeter(twitterService)))

  test("forward tweets to Twitter") {
    (twitterService.tweet _).expects(tweet)

    tweeter ! Tweet(tweet)
  }
}
