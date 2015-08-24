package gr8craft

import java.util.concurrent.TimeUnit.HOURS

import gr8craft.article.{Article, InMemoryShelf}
import gr8craft.scheduling.{ScheduledExecutor, Scheduler}
import gr8craft.twitter.{TweetRunner, TwitterApiService}
import twitter4j.TwitterFactory
import twitter4j.TwitterFactory.getSingleton
import twitter4j.conf.ConfigurationBuilder

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
    val articles: List[Article] = List(new Article("dummy topic", "dummy URL"))
    val tweetRunner: TweetRunner = new TweetRunner(createTwitterApiService, new InMemoryShelf(articles))
    new ApplicationRunner(new ScheduledExecutor(HOURS, tweetRunner))
  }

  def createTwitterApiService: TwitterApiService = {
    val configuration = new ConfigurationBuilder()
      .setDebugEnabled(true)
      .setOAuthConsumerKey(sys.env("twitter4jconsumerKey"))
      .setOAuthConsumerSecret(sys.env("twitter4jconsumerSecret"))
      .setOAuthAccessToken(sys.env("twitter4jaccessToken"))
      .setOAuthAccessTokenSecret(sys.env("twitter4jaccessTokenSecret"))
      .build()

    new TwitterApiService(new TwitterFactory(configuration).getInstance())
  }
}
