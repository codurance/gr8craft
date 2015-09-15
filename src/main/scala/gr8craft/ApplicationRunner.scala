package gr8craft

import akka.actor.{ActorRef, ActorSystem, Props}
import gr8craft.TwitterFactoryWithConfiguration.createTwitter
import gr8craft.article.{Article, InMemoryShelf}
import gr8craft.messages.{Start, Stop}
import gr8craft.scheduling._
import gr8craft.twitter.{TweetRunner, TwitterApiService}

import scala.concurrent.duration._

class ApplicationRunner(scheduler: ActorRef) {
  def startTwitterBot() {
    scheduler ! Start
  }

  def stop() {
    scheduler ! Stop
  }
}

object ApplicationRunner {
  def main(args: Array[String]) {
    val application: ApplicationRunner = assembleApplication

    application.startTwitterBot()
  }

  def assembleApplication: ApplicationRunner = {
    val articles = List(new Article("Interaction Driven Design", "http://www.ustream.tv/recorded/61480606"))
    val twitterService = new TwitterApiService(createTwitter())
    val system = ActorSystem("Gr8craftSystem")
    val tweetRunner = system.actorOf(Props(new TweetRunner(twitterService, new InMemoryShelf(articles))))
    val scheduler = system.actorOf(Props(new ScheduledExecutor(1.hour, tweetRunner)))
    new ApplicationRunner(scheduler)
  }
}
