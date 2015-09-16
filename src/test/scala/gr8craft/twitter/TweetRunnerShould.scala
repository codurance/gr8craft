package gr8craft.twitter

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit._
import akka.testkit.{TestProbe, TestKit}
import gr8craft.inspiration.{Inspiration, Shelf}
import gr8craft.messages.{Tweet, AddInspiration, Trigger}
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TweetRunnerShould extends TestKit(ActorSystem("TweetRunnerShould")) with FunSuiteLike with MockFactory with BeforeAndAfterAll {
  val topic = "topic"
  val location = "location"
  val inspiration = new Inspiration(topic, location)

  val shelf = mock[Shelf]
  val tweeter = TestProbe()

  val tweetRunner = system.actorOf(Props(new TweetRunner(tweeter.ref, shelf)))

  override def afterAll() {
    shutdownActorSystem(system)
  }

  test("tweet the first inspiration from the shelf") {
    (shelf.next _).expects().returns(inspiration)

    tweetRunner ! Trigger

    tweeter.expectMsg(Tweet("Your hourly recommended inspiration about " + topic + ": " + location))
  }

  test("receive a new inspiration for the shelf and use it") {
    val newShelf = mock[Shelf]
    (shelf.withInspiration _).expects(inspiration).returns(newShelf)
    (newShelf.next _).expects().returns(inspiration)

    tweetRunner ! AddInspiration(inspiration)

    tweetRunner ! Trigger
    tweeter.expectMsg(Tweet("Your hourly recommended inspiration about " + topic + ": " + location))
  }
}
