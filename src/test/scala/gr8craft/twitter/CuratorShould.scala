package gr8craft.twitter

import akka.actor.{ActorRef, Kill, Props}
import akka.testkit.TestProbe
import gr8craft.AkkaTest
import gr8craft.inspiration.Inspiration
import gr8craft.messages._
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CuratorShould extends AkkaTest("CuratorShould") with MockFactory {
  val inspiration = new Inspiration("topic", "location")
  val laterInspiration = new Inspiration("anotherTopic", "anotherLocation")
  val lastId = 42L
  val directMessage = new DirectMessage("sender", "text", lastId)

  val shelf = TestProbe()
  val tweeter = TestProbe()

  var curator = createCurator()


  test("receive a trigger and ask the shelf for the next inspiration") {
    curator ! Trigger

    shelf.expectMsg(InspireMe)
  }

  test("receive a trigger and ask the tweeter for new DMs since last asked") {
    curator ! Trigger

    tweeter.expectMsg(FetchDirectMessages(0L))

    curator ! GotDirectMessage(directMessage)
    curator ! Trigger

    tweeter.expectMsg(FetchDirectMessages(lastId))
  }

  test("receive a new inspiration and use it") {
    curator ! Inspire(inspiration)

    tweeter.expectMsg(GoAndTweet(inspiration))
  }

  test("receive a new inspiration for the shelf and forward it") {
    curator ! AddInspiration(inspiration)

    shelf.expectMsg(AddInspiration(inspiration))
  }

  test("recover trigger by skipping text to shelf") {
    curator ! Trigger
    shelf.expectMsg(InspireMe)

    recoverFromShutdown()

    shelf.expectMsg(Skip)
  }

  test("recover trigger by  not interacting with Twitter, but continue from last id asked afterwards") {
    curator ! Trigger
    tweeter.expectMsg(FetchDirectMessages(0L))
    curator ! GotDirectMessage(directMessage)

    recoverFromShutdown()

    tweeter.expectNoMsg()

    curator ! Trigger
    tweeter.expectMsg(FetchDirectMessages(lastId))
  }

  test("recover AddInspiration by replaying it to shelf") {
    curator ! AddInspiration(inspiration)
    shelf.expectMsg(AddInspiration(inspiration))
    curator ! AddInspiration(laterInspiration)
    shelf.expectMsg(AddInspiration(laterInspiration))

    recoverFromShutdown()

    shelf.expectMsg(AddInspiration(inspiration))
    shelf.expectMsg(AddInspiration(laterInspiration))
  }

  test("recover Inspire by doing nothing") {
    curator ! Inspire(inspiration)
    tweeter.expectMsg(GoAndTweet(inspiration))

    recoverFromShutdown()

    shelf.expectNoMsg()
    tweeter.expectNoMsg()
  }

  private def recoverFromShutdown(): Unit = {
    curator ! Kill
    curator = createCurator()
  }

  private def createCurator(): ActorRef = {
    system.actorOf(Props(new Curator(tweeter.ref, shelf.ref)))
  }
}
