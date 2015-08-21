package gr8craft

import java.util.concurrent.TimeUnit.HOURS

import gr8craft.article.{Article, InMemoryShelf}
import gr8craft.scheduling.{ScheduledExecutor, Scheduler}
import gr8craft.twitter.{TweetRunner, TwitterApiService}
import twitter4j.TwitterFactory.getSingleton

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
    val articles: List[Article] = List(new Article("dummy topic", "dummy URL"))
    val tweetRunner: TweetRunner = new TweetRunner(new TwitterApiService(getSingleton), new InMemoryShelf(articles))
    val application = new ApplicationRunner(new ScheduledExecutor(HOURS, tweetRunner))

    application.startTwitterBot()
  }
}
