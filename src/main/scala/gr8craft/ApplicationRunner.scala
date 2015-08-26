package gr8craft

import java.util.concurrent.TimeUnit.HOURS

import gr8craft.TwitterFactoryWithConfiguration.createTwitter
import gr8craft.article.{Article, InMemoryShelf}
import gr8craft.scheduling.{ScheduledExecutor, Scheduler}
import gr8craft.twitter.{TweetRunner, TwitterApiService}
import twitter4j.{Twitter, TwitterFactory}
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
    val articles: List[Article] = List(new Article("Interaction Driven Design", "http://www.ustream.tv/recorded/61480606"))
    val tweetRunner: TweetRunner = new TweetRunner(new TwitterApiService(createTwitter()), new InMemoryShelf(articles))
    new ApplicationRunner(new ScheduledExecutor(HOURS, tweetRunner))
  }


}

object TwitterFactoryWithConfiguration {
  def createTwitter(suffix: String = ""): Twitter = {
    val configuration = new ConfigurationBuilder()
      .setDebugEnabled(true)
      .setOAuthConsumerKey(readEnvironmentVariable(suffix, "twitter4jconsumerKey"))
      .setOAuthConsumerSecret(readEnvironmentVariable(suffix, "twitter4jconsumerSecret"))
      .setOAuthAccessToken(readEnvironmentVariable(suffix, "twitter4jaccessToken"))
      .setOAuthAccessTokenSecret(readEnvironmentVariable(suffix, "twitter4jaccessTokenSecret"))
      .build()

    new TwitterFactory(configuration).getInstance()
  }

  def readEnvironmentVariable(suffix: String, key: String): String = {
    sys.env(key + suffix)
  }
}
