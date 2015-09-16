package gr8craft.twitter

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit._
import akka.testkit.{TestKit, TestProbe}
import gr8craft.inspiration.Inspiration
import gr8craft.messages._
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}

@RunWith(classOf[JUnitRunner])
class TweetRunnerShould extends TestKit(ActorSystem("TweetRunnerShould")) with FunSuiteLike with MockFactory with BeforeAndAfterAll {
  val topic = "topic"
  val location = "location"
  val anotherTopic = "anotherTopic"
  val anotherLocation = "anotherLocation"
  val inspiration = new Inspiration(topic, location)
  val laterInspiration = new Inspiration(anotherTopic, anotherLocation)

  val shelf = TestProbe()
  val tweeter = TestProbe()

  val tweetRunner = system.actorOf(Props(new TweetRunner(tweeter.ref, shelf.ref)))

  override def afterAll() {
    shutdownActorSystem(system)
  }

  test("receive trigger and ask shelf for next article") {
    tweetRunner ! Trigger

    shelf.expectMsg(Next)
  }

  test("receive a new inspiration for the shelf and use it") {
    tweetRunner ! Inspire(inspiration)

    tweeter.expectMsg(Tweet("Your hourly recommended inspiration about " + topic + ": " + location))
  }

  test("receive a new inspiration for the shelf and forward it") {
    tweetRunner ! AddInspiration(inspiration)

    shelf.expectMsg(AddInspiration(inspiration))
  }
  
}
