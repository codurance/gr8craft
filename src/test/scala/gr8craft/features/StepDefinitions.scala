package gr8craft.features

import cucumber.api.scala.{EN, ScalaDsl}
import gr8craft.ApplicationRunner
import gr8craft.article.{Article, InMemoryShelf}
import gr8craft.scheduling.FakeScheduler
import gr8craft.twitter.TwitterApiService
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import twitter4j.{Status, TwitterFactory}
import scala.concurrent.duration._

import scala.collection.JavaConverters._

class StepDefinitions extends ScalaDsl with EN with Matchers with Eventually {
  val scheduler = new FakeScheduler
  private val twitter = TwitterFactory.getSingleton
  val twitterService = new TwitterApiService(twitter)

  var application: ApplicationRunner = null

  Before() { _ =>
    twitter.getUserTimeline.asScala.foreach(status => twitter.destroyStatus(status.getId))
  }

  After() { _ =>
    application.stop()
  }


  Given( """^the clock shows (\d+):(\d+)$""") { (hour: Int, minute: Int) =>
    scheduler.setTime(hour, minute)
  }

  Given( """^the next article on the shelf about "([^"]*)" can be found at "([^"]*)"$""") { (topic: String, articleLocation: String) =>
    val shelf = InMemoryShelf(Seq(new Article(topic, articleLocation)))
    this.application = new ApplicationRunner(scheduler, twitterService, shelf)
  }

  When( """^the clock reaches (\d+):(\d+)$""") { (hour: Int, minute: Int) =>
    scheduler.setTime(hour, minute)
    application.startTwitterBot()
  }

  Then( """^gr8craft tweets "([^"]*)"$""") { (expectedTweet: String) =>
    val newestTweet = eventually(timeout(5.seconds), interval(1.second)) {
      val newestTweet: Option[Status] = twitter.getUserTimeline.asScala.headOption
      newestTweet.map(_.getText)
    }
    newestTweet.get shouldEqual expectedTweet
  }
}
