package gr8craft.twitter

import akka.actor.{ActorSystem, Props}
import akka.testkit.{TestActorRef, TestKit}
import gr8craft.article.{Article, Shelf}
import gr8craft.messages.{AddInspiration, Trigger}
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuiteLike
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TweetRunnerShould extends TestKit(ActorSystem("TweetRunnerShould")) with FunSuiteLike with MockFactory {
  val topic = "topic"
  val location = "location"
  val inspiration = new Article(topic, location)

  val shelf = mock[Shelf]
  val twitterService = mock[TwitterService]

  val tweetRunner = TestActorRef(Props(new TweetRunner(twitterService, shelf)))


  test("tweet the first article from the shelf") {
    (shelf.first _).expects().returns(inspiration)
    (twitterService.tweet _).expects("Your hourly recommended article about " + topic + ": " + location)

    tweetRunner ! Trigger
  }


  test("receive a new inspiration for the shelf") {
    (shelf.add _).expects(inspiration)

    tweetRunner ! AddInspiration(inspiration)
  }
}
