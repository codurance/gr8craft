package com.codurance.gr8craft.model.supervision

import akka.actor.{ActorRef, Kill, Props}
import akka.testkit.TestProbe
import com.codurance.gr8craft.messages._
import com.codurance.gr8craft.model.inspiration.{Inspiration, Suggestion}
import com.codurance.gr8craft.model.publishing.{DirectMessage, DirectMessageId}
import com.codurance.gr8craft.util.AkkaTest
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class SupervisorShould extends AkkaTest("SupervisorShould") with MockFactory {
  private val inspiration = Inspiration("topic", "location")
  private val lastId = DirectMessageId(42L)

  private val textOfDirectMessage = "inspiration: DDD | location: http://t.co/lqJDZlGcJE | contributor: @gr8contributor"
  private val directMessage = DirectMessage("sender", textOfDirectMessage, lastId)
  private val textOfLaterDirectMessage = "inspiration: Another | location: url | contributor: @anotherContributor"
  private val laterDirectMessage = DirectMessage("sender", textOfLaterDirectMessage, lastId)

  private val shelf = TestProbe()
  private val tweeter = TestProbe()

  private var curator = createCurator()


  test("receive a trigger and ask the shelf for the next inspiration") {
    curator ! Trigger

    shelf.expectMsg(InspireMe)
  }

  test("receive a trigger and ask the publisher for new DMs since last asked") {
    curator ! Trigger

    tweeter.expectMsg(FetchDirectMessages(None))

    curator ! AddDirectMessage(directMessage)
    curator ! Trigger

    tweeter.expectMsg(FetchDirectMessages(Some(lastId)))
  }

  test("receive a new inspiration and use it") {
    curator ! Inspire(inspiration)

    tweeter.expectMsg(GoAndTweet(inspiration))
  }

  test("receive a new inspiration for the shelf and forward it") {
    curator ! AddDirectMessage(directMessage)

    expectInspirationAddedFrom(textOfDirectMessage)
  }

  test("recover trigger by skipping text to shelf") {
    curator ! Trigger
    shelf.expectMsg(InspireMe)

    recoverFromShutdown()

    shelf.expectMsg(Skip)
  }

  test("recover trigger by not interacting with Twitter, but continue from last id asked afterwards") {
    curator ! Trigger
    tweeter.expectMsg(FetchDirectMessages(None))
    curator ! AddDirectMessage(directMessage)

    recoverFromShutdown()

    tweeter.expectNoMsg()

    curator ! Trigger
    tweeter.expectMsg(FetchDirectMessages(Some(lastId)))
  }

  test("recover getting DirectMessages") {
    curator ! AddDirectMessage(directMessage)
    expectInspirationAddedFrom(textOfDirectMessage)
    curator ! AddDirectMessage(laterDirectMessage)
    expectInspirationAddedFrom(textOfLaterDirectMessage)

    recoverFromShutdown()

    expectInspirationAddedFrom(textOfDirectMessage)
    expectInspirationAddedFrom(textOfLaterDirectMessage)
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
    system.actorOf(Props(new Supervisor(tweeter.ref, shelf.ref)))
  }

  private def expectInspirationAddedFrom(text: String): AddInspiration = {
    shelf.expectMsg(AddInspiration(new Suggestion(text).parse.get))
  }
}
