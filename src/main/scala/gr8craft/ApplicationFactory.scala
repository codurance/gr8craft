package gr8craft

import akka.actor.{ActorSystem, Props}
import gr8craft.TwitterFactoryWithConfiguration.createTwitter
import gr8craft.inspiration.{Inspiration, Shelf}
import gr8craft.scheduling.ScheduledExecutor
import gr8craft.twitter.{TwitterApiService, Curator, Tweeter, TwitterService}

import scala.concurrent.duration._

object ApplicationFactory {
  def createApplication(system: ActorSystem = ActorSystem("Gr8craftSystem"), twitterService: TwitterService = new TwitterApiService(createTwitter()), initalInspirations: Set[Inspiration] = Set.empty, tweetInterval: Duration = 1.hour): ApplicationRunner = {
    val tweeter = system.actorOf(Props(new Tweeter(twitterService)))
    val shelf = system.actorOf(Props(new Shelf(initalInspirations)))
    val tweetRunner = system.actorOf(Props(new Curator(tweeter, shelf)))
    val scheduler = system.actorOf(Props(new ScheduledExecutor(tweetInterval, tweetRunner)))

    new ApplicationRunner(scheduler)
  }
}
