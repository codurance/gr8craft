package gr8craft.featuresmocked

import java.util.concurrent.TimeUnit

import cucumber.api.scala.{EN, ScalaDsl}
import gr8craft.ApplicationRunner
import gr8craft.TwitterFactoryWithConfiguration.createTwitter
import gr8craft.article.{Article, InMemoryShelf, Shelf}
import gr8craft.scheduling.ScheduledExecutor
import gr8craft.twitter.{TwitterService, TweetRunner, TwitterApiService}
import org.scalamock.scalatest.MockFactory
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import twitter4j.Status

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class MockedStepDefinitions extends ScalaDsl with EN with Matchers with Eventually {
  val twitterService = new TwitterService {
    var tweet: String = null

    override def tweet(tweet: String): Unit = this.tweet = tweet

    def tweetSent: String = this.tweet
  }
  var shelf: Shelf = null
  var application: ApplicationRunner = null

  After() { _ =>
    application.stop()
  }

  Given( """^the next article on the shelf about "([^"]*)" can be found at "([^"]*)"$""") { (topic: String, articleLocation: String) =>
    shelf = new Shelf {
      override def first: Article = new Article(topic, articleLocation)
    }
  }

  When( """^the hour is reached$""") { () =>
    val scheduler = new ScheduledExecutor(TimeUnit.SECONDS, new TweetRunner(twitterService, shelf));
    this.application = new ApplicationRunner(scheduler)
    application.startTwitterBot()
  }

  Then( """^gr8craft tweets "([^"]*)"$""") { (expectedTweet: String) =>
    twitterService.tweetSent shouldBe expectedTweet
  }


}
