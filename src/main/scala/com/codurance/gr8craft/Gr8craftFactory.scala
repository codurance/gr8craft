package com.codurance.gr8craft

import akka.actor.{ActorSystem, Props}
import com.codurance.gr8craft.infrastructure.TwitterFactoryWithConfiguration.createTwitter
import com.codurance.gr8craft.infrastructure.{TwitterApiService, TwitterFactoryWithConfiguration}
import com.codurance.gr8craft.model.inspiration.{Inspiration, Shelf}
import com.codurance.gr8craft.model.scheduling.{RegularActions, ScheduledExecutor}
import com.codurance.gr8craft.model.twitter.{Tweeter, TwitterService}

import scala.concurrent.duration._

object Gr8craftFactory {
  def createApplication(system: ActorSystem = ActorSystem("Gr8craftSystem"), twitterService: TwitterService = new TwitterApiService(createTwitter()), initalInspirations: Set[Inspiration] = Set.empty, tweetInterval: Duration = 1.hour): Gr8craft = {
    val tweeter = system.actorOf(Props(new Tweeter(twitterService)))
    val shelf = system.actorOf(Props(new Shelf(initalInspirations)))
    val tweetRunner = system.actorOf(Props(new RegularActions(tweeter, shelf)))
    val scheduler = system.actorOf(Props(new ScheduledExecutor(tweetInterval, tweetRunner)))
    new Gr8craft(scheduler)
  }
}