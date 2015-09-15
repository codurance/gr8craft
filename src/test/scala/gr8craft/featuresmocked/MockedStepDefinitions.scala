package gr8craft.featuresmocked

import akka.actor.{ActorSystem, Props}
import cucumber.api.scala.{EN, ScalaDsl}
import gr8craft.ApplicationRunner
import gr8craft.inspiration.{InMemoryShelf, Inspiration, Shelf}
import gr8craft.scheduling.ScheduledExecutor
import gr8craft.twitter.{TweetRunner, TwitterService}
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually

import scala.concurrent.duration._

class MockedStepDefinitions extends ScalaDsl with EN with Matchers with Eventually {
  val twitterService = new TwitterService {
    var tweet: String = null

    override def tweet(tweet: String): Unit = this.tweet = tweet

    def tweetSent: String = this.tweet
  }
  var shelf: Shelf = null
  var application: ApplicationRunner = null

  val system = ActorSystem("MockedFeatureSpecifications")

  After() { _ =>
    application.stop()
  }

  Given( """^the next inspiration on the shelf about "([^"]*)" can be found at "([^"]*)"$""") { (topic: String, location: String) =>
    shelf = new Shelf {
      override def next: Inspiration = new Inspiration(topic, location)

      override def withInspiration(inspiration: Inspiration): Shelf = this
    }
  }

  When( """^the hour is reached$""") { () =>
    val tweetRunner = system.actorOf(Props(new TweetRunner(twitterService, shelf)))
    val scheduler = system.actorOf(Props(new ScheduledExecutor(1.second, tweetRunner)))
    this.application = new ApplicationRunner(scheduler)
    application.startTwitterBot()
  }

  Then( """^gr8craft tweets "([^"]*)"$""") { (expectedTweet: String) =>
    eventually(timeout(10.seconds), interval(1.second)) {
      twitterService.tweetSent should not be null
    }
    twitterService.tweetSent shouldBe expectedTweet
  }


}
