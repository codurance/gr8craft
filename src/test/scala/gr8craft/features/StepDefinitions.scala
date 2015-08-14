package gr8craft.features

import cucumber.api.scala.{EN, ScalaDsl}
import gr8craft.article.{Article, Shelf}
import gr8craft.main.ApplicationRunner
import gr8craft.scheduling.FakeScheduler
import gr8craft.twitter.TwitterApiService
import org.scalatest.Matchers

class StepDefinitions extends ScalaDsl with EN with Matchers {
  val scheduler = new FakeScheduler
  val twitterService = new TwitterApiService

  var application: ApplicationRunner = null

  Given( """^the clock shows (\d+):(\d+)$""") { (hour: Int, minute: Int) =>
    scheduler.setTime(hour, minute)
  }

  Given( """^the next article on the shelf about "([^"]*)" can be found at "([^"]*)"$""") { (topic: String, articleLocation: String) =>
    val shelf = Shelf(Seq(new Article(topic, articleLocation)))
    this.application = new ApplicationRunner(scheduler, twitterService, shelf)
  }

  When( """^the clock reaches (\d+):(\d+)$""") { (hour: Int, minute: Int) =>
    scheduler.setTime(hour, minute)
    application.startTwitterBot
  }

  Then( """^gr8craft tweets "([^"]*)"$""") { (expectedTweet: String) =>
    twitterService.getNewestTweet shouldEqual expectedTweet
  }

  After() { _ => application.stop }

}
