package gr8craft.twitter

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import gr8craft.article.{Article, Shelf}
import gr8craft.messages.Trigger
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuiteLike
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TweetRunnerShould extends TestKit(ActorSystem("TweetRunnerShould")) with FunSuiteLike with MockFactory {
  val topic = "topic"
  val location = "location"

  val shelf = mock[Shelf]
  val twitterService = stub[TwitterService]

  val tweetRunner = TestActorRef(Props(new TweetRunner(twitterService, shelf)))


  test("tweet the first article from the shelf") {
    (shelf.first _).expects().returns(new Article(topic, location))

    tweetRunner ! Trigger

    (twitterService.tweet _).verify("Your hourly recommended article about " + topic + ": " + location)
  }
}
