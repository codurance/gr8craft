import gr8craft.article.{Article, Shelf}
import gr8craft.main.ApplicationRunner
import gr8craft.scheduling.Scheduler
import gr8craft.twitter.TwitterService
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfter, FunSuite}


class ApplicationRunnerShould extends FunSuite with MockFactory with BeforeAndAfter {
  val topic = "topic"
  val location = "location"

  val scheduler = mock[Scheduler]
  val shelf = mock[Shelf]
  val twitterService = stub[TwitterService]

  val applicationRunner = new ApplicationRunner(scheduler, twitterService, shelf)


  test("tweet the first article from the shelf if scheduled") {
    (scheduler.isTriggered _).expects().returning(true)
    (shelf.first _).expects().returns(new Article(topic, location))

    applicationRunner.startTwitterBot

    (twitterService.tweet _).verify("Your hourly recommended article about " + topic + ": " + location)
  }

  after {
    applicationRunner.stop
  }
}
