package gr8craft.featuresmocked

import akka.actor.ActorSystem
import akka.testkit.TestKit
import akka.testkit.TestKit.shutdownActorSystem
import cucumber.api.scala.{EN, ScalaDsl}
import gr8craft.ApplicationFactory._
import gr8craft.ApplicationRunner
import gr8craft.inspiration.Inspiration
import gr8craft.twitter.TwitterService
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually

import scala.concurrent.duration._

class MockedStepDefinitions extends TestKit(ActorSystem("MockedStepDefinitions")) with ScalaDsl with EN with Matchers with Eventually {
  val twitterService = new TwitterService {
    var tweet: String = null

    override def tweet(tweet: String): Unit = this.tweet = tweet

    def tweetSent: String = this.tweet
  }

  var application: ApplicationRunner = null

  After() { _ =>
    shutdownActorSystem(system)
  }

  Given( """^the next inspiration on the shelf about "([^"]*)" can be found at "([^"]*)"$""") { (topic: String, location: String) =>
    application = createApplication(system, twitterService, Set(new Inspiration(topic, location)), 1.millisecond)
  }

  When( """^the hour is reached$""") { () =>
    application.startTwitterBot()
  }

  Then( """^gr8craft tweets "([^"]*)"$""") { (expectedTweet: String) =>
    awaitCond(twitterService.tweetSent != null, 1.second)
    twitterService.tweetSent shouldBe expectedTweet
  }

}
