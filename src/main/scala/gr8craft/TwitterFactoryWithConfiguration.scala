package gr8craft

import com.typesafe.config.ConfigFactory
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory}

object TwitterFactoryWithConfiguration {

  def createTwitter(): Twitter = {
    val configuration = ConfigFactory.load().getConfig("twitter4j")

    val twitterAuthConfiguration = new ConfigurationBuilder()
      .setDebugEnabled(true)
      .setOAuthConsumerKey(configuration.getString("consumerKey"))
      .setOAuthConsumerSecret(configuration.getString("consumerSecret"))
      .setOAuthAccessToken(configuration.getString("accessToken"))
      .setOAuthAccessTokenSecret(configuration.getString("accessTokenSecret"))
      .build()

    new TwitterFactory(twitterAuthConfiguration).getInstance()
  }
}
