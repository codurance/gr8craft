package gr8craft.twitter

import gr8craft.article.{Article, Shelf}
import org.junit.runner.RunWith
import org.scalamock.scalatest.MockFactory
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TweetRunnerShould extends FunSuite with MockFactory {
  val topic = "topic"
  val location = "location"

  val shelf = mock[Shelf]
  val twitterService = stub[TwitterService]

  val tweetRunner = new TweetRunner(twitterService, shelf)


  test("tweet the first article from the shelf") {
    (shelf.first _).expects().returns(new Article(topic, location))

    tweetRunner.run().apply()

    (twitterService.tweet _).verify("Your hourly recommended article about " + topic + ": " + location)
  }
}
