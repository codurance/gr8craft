package gr8craft

import gr8craft.TwitterFactoryWithConfiguration.createTwitter
import gr8craft.article.{Article, InMemoryShelf}
import gr8craft.scheduling.{ScheduledExecutor, Scheduler}
import gr8craft.twitter.{TweetRunner, TwitterApiService}

import scala.concurrent.duration._

class ApplicationRunner(scheduler: Scheduler) {
  def startTwitterBot() {
    scheduler.schedule()
  }

  def stop() {
    scheduler.shutdown()
  }

}

object ApplicationRunner {

  def main(args: Array[String]) {
    val application: ApplicationRunner = assembleApplication

    application.startTwitterBot()
  }

  def assembleApplication: ApplicationRunner = {
    val articles: List[Article] = List(new Article("Interaction Driven Design", "http://www.ustream.tv/recorded/61480606"))
    val tweetRunner: TweetRunner = new TweetRunner(new TwitterApiService(createTwitter()), new InMemoryShelf(articles))
    new ApplicationRunner(new ScheduledExecutor(1.hour, tweetRunner))
  }
}
