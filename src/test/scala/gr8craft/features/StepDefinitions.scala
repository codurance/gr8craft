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
import twitter4j._

import scala.collection.JavaConverters._
import scala.concurrent.duration._

class StepDefinitions extends TestKit(ActorSystem("StepDefinitions")) with ScalaDsl with EN with Matchers with Eventually {

  val twitter = createTwitter()
  val twitterService = new TwitterApiService(twitter)
  var application: ApplicationRunner = null
  var newestTweet: Option[Status] = Option.empty
  var newestMessage: Option[DirectMessage] = Option.empty
  var dmSent = false
  var deleted = false

  Before() { _ =>
    deleteExistingTimeline()
    eventually(timeout(100.seconds), interval(1.second)) {
      assert(deleted)
    }
  }

  After() { _ =>
    shutdownActorSystem(system)
  }

  Given( """^the next inspiration on the shelf about "([^"]*)" can be found at "([^"]*)"$""") { (topic: String, location: String) =>
    application = createApplication(system, twitterService, Set(new Inspiration(topic, location)), 1.second)
  }

  When( """^the hour is reached$""") { () =>
    application.startTwitterBot()
  }

  Then( """^gr8craft tweets "([^"]*)"$""") { (expectedTweet: String) =>
    eventually(timeout(100.seconds), interval(1.second)) {
      requestNewestTweet()
      newestTweet.isDefined shouldBe true
    }
    newestTweet.map(_.getText).get shouldEqual expectedTweet
  }

  Given( """^The Bot receives a mention from "([^"]*)" with the recommendation "([^"]*)"$""") { (contributor: String, recommendation: String) =>
    application = createApplication(system, twitterService, tweetInterval = 1.second)
    val modTwitter = createTwitter(contributor)
    val twitterServiceMod = new TwitterApiService(modTwitter)
    twitterServiceMod.tweet("@gr8crafttest " + recommendation)
  }

  Then( """^a moderator receives a DM from gr8craft saying "([^"]*)"$""") { (recommendation: String) =>
    val modTwitter = createTwitter("gr8craftmod")

    eventually(timeout(100.seconds), interval(1.second)) {
      requestNewestDM(modTwitter)
      newestMessage.isDefined shouldBe true
    }
    newestMessage.map(_.getText).get shouldEqual recommendation
  }

  When( """^a moderator sends a DM to "([^"]*)" "([^"]*)"$""") { (recipient: String, directMessage: String) =>
    application = createApplication(system, twitterService, tweetInterval = 1.second)
    val modTwitter = createTwitter("gr8craftmod")
    twitter.addListener(new TwitterAdapter() {
      dmSent = true
    })

    modTwitter.sendDirectMessage(recipient, directMessage)

    eventually(timeout(100.seconds), interval(1.second)) {
      assert(dmSent)
    }
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

  private def requestNewestTweet(): Unit = {
    twitter.addListener(new TwitterAdapter() {
      override def gotUserTimeline(statuses: ResponseList[Status]): Unit =
        newestTweet = statuses.asScala.headOption
    })
    twitter.getUserTimeline()
  }

  private def requestNewestDM(modTwitter: AsyncTwitter): Unit = {
    modTwitter.addListener(new TwitterAdapter() {
      override def gotDirectMessages(messages: ResponseList[DirectMessage]): Unit =
        newestMessage = messages.asScala.headOption
    })
    modTwitter.getDirectMessages()
  }
}
