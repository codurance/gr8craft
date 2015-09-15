package gr8craft.twitter

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import gr8craft.inspiration.{Inspiration, Shelf}
import gr8craft.messages.{AddInspiration, Trigger}
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuiteLike
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TweetRunnerShould extends TestKit(ActorSystem("TweetRunnerShould")) with FunSuiteLike with MockFactory {
  val topic = "topic"
  val location = "location"
  val inspiration = new Inspiration(topic, location)

  val shelf = mock[Shelf]
  val twitterService = mock[TwitterService]

  val tweetRunner = TestActorRef(Props(new TweetRunner(twitterService, shelf)))


  test("tweet the first inspiration from the shelf") {
    (shelf.next _).expects().returns(inspiration)
    (twitterService.tweet _).expects("Your hourly recommended inspiration about " + topic + ": " + location)

    tweetRunner ! Trigger
  }


  test("receive a new inspiration for the shelf") {
    (shelf.withInspiration _).expects(inspiration)

    tweetRunner ! AddInspiration(inspiration)
  }
}
