package gr8craft.features

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import akka.testkit.TestKit._
import cucumber.api.scala.{EN, ScalaDsl}
import gr8craft.ApplicationRunner
import gr8craft.TwitterFactoryWithConfiguration.createTwitter
import gr8craft.inspiration.{InMemoryShelf, Inspiration, Shelf}
import gr8craft.scheduling.ScheduledExecutor
import gr8craft.twitter.{TweetRunner, TwitterApiService}
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import twitter4j.Status

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class StepDefinitions extends TestKit(ActorSystem("StepDefinitions")) with ScalaDsl with EN with Matchers with Eventually {

  val twitter = createTwitter("4testing")
  val twitterService = new TwitterApiService(twitter)
  var shelf: Shelf = null
  var application: ApplicationRunner = null

  Before() { _ =>
    twitter.getUserTimeline.asScala.foreach(status => twitter.destroyStatus(status.getId))
  }

  After() { _ =>
    shutdownActorSystem(system)
  }

  Given( """^the next inspiration on the shelf about "([^"]*)" can be found at "([^"]*)"$""") { (topic: String, location: String) =>
    shelf = InMemoryShelf(Seq(new Inspiration(topic, location)))
  }

  When( """^the hour is reached$""") { () =>
    val tweetRunner = system.actorOf(Props(new TweetRunner(twitterService, shelf)))
    val scheduler = system.actorOf(Props(new ScheduledExecutor(1.second, tweetRunner)))
    this.application = new ApplicationRunner(scheduler)
    application.startTwitterBot()
  }

  Then( """^gr8craft tweets "([^"]*)"$""") { (expectedTweet: String) =>
    val newestTweet = eventually(timeout(1000.seconds), interval(1.second)) {
      val newestTweet: Option[Status] = twitter.getUserTimeline.asScala.headOption
      newestTweet.isDefined shouldBe true
      newestTweet.map(_.getText)
    }
    newestTweet.get shouldEqual expectedTweet
  }

}
