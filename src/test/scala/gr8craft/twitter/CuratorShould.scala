package gr8craft.twitter

import akka.actor.{Kill, Props}
import akka.testkit.TestProbe
import gr8craft.AkkaTest
import gr8craft.inspiration.Inspiration
import gr8craft.messages._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CuratorShould extends AkkaTest("CuratorShould") {
  val inspiration = new Inspiration("topic", "location")
  val laterInspiration = new Inspiration("anotherTopic", "anotherLocation")

  val shelf = TestProbe()
  val tweeter = TestProbe()

  val tweetRunner = system.actorOf(Props(new Curator(tweeter.ref, shelf.ref)))


  test("receive a trigger and ask the shelf for the next inspiration") {
    tweetRunner ! Trigger

    shelf.expectMsg(InspireMe)
  }

  test("receive a new inspiration and use it") {
    tweetRunner ! Inspire(inspiration)

    tweeter.expectMsg(Tweet(inspiration))
  }

  test("receive a new inspiration for the shelf and forward it") {
    tweetRunner ! AddInspiration(inspiration)

    shelf.expectMsg(AddInspiration(inspiration))
  }

  test("recover trigger by skipping message to shelf") {
    tweetRunner ! Trigger
    shelf.expectMsg(InspireMe)

    recoverFromShutdown()

    shelf.expectMsg(Skip)
    tweeter.expectNoMsg()
  }

  test("recover AddInspiration by replaying it to shelf") {
    tweetRunner ! AddInspiration(inspiration)
    shelf.expectMsg(AddInspiration(inspiration))
    tweetRunner ! AddInspiration(laterInspiration)
    shelf.expectMsg(AddInspiration(laterInspiration))

    recoverFromShutdown()

    shelf.expectMsg(AddInspiration(inspiration))
    shelf.expectMsg(AddInspiration(laterInspiration))
    tweeter.expectNoMsg()
  }

  test("recover Inspire by doing nothing") {
    tweetRunner ! Inspire(inspiration)
    tweeter.expectMsg(Tweet(inspiration))

    recoverFromShutdown()

    shelf.expectNoMsg()
    tweeter.expectNoMsg()
  }

  private def recoverFromShutdown(): Unit = {
    tweetRunner ! Kill
    system.actorOf(Props(new Curator(tweeter.ref, shelf.ref)))
  }
}
