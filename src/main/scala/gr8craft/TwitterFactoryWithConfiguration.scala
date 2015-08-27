package gr8craft

import twitter4j.conf.ConfigurationBuilder
import twitter4j.{Twitter, TwitterFactory}

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
