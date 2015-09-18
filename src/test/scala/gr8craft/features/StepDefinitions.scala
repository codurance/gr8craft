package gr8craft.features

import akka.actor.ActorSystem
import akka.testkit.TestKit
import akka.testkit.TestKit._
import cucumber.api.scala.{EN, ScalaDsl}
import gr8craft.ApplicationFactory.createApplication
import gr8craft.ApplicationRunner
import gr8craft.TwitterFactoryWithConfiguration.createTwitter
import gr8craft.inspiration.Inspiration
import gr8craft.twitter.TwitterApiService
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually
import twitter4j.{ResponseList, Status, TwitterAdapter}

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class StepDefinitions extends TestKit(ActorSystem("StepDefinitions")) with ScalaDsl with EN with Matchers with Eventually {

  val twitter = createTwitter()
  val twitterService = new TwitterApiService(twitter)
  var application: ApplicationRunner = null
  var newestTweet: Option[Status] = Option.empty
  var deleted = false

  Before() { _ =>
    deleteExistingTimeline()
    eventually(timeout(1000.seconds), interval(1.second)) {
      assert(deleted)
    }
  }

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
    eventually(timeout(1000.seconds), interval(1.second)) {
      requestNewestTweet()
      newestTweet.isDefined shouldBe true
    }
    newestTweet.map(_.getText).get shouldEqual expectedTweet
  }

  private def deleteExistingTimeline(): Unit = {
    twitter.addListener(new TwitterAdapter() {
      override def gotUserTimeline(statuses: ResponseList[Status]): Unit =
        statuses.asScala
          .foreach(status => twitter.destroyStatus(status.getId))

      deleted = true
    })
    twitter.getUserTimeline()
  }

  def requestNewestTweet(): Unit = {
    twitter.addListener(new TwitterAdapter() {
      override def gotUserTimeline(statuses: ResponseList[Status]): Unit =
        newestTweet = statuses.asScala.headOption
    })
    twitter.getUserTimeline()
  }
}
