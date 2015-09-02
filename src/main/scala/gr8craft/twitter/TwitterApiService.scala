package gr8craft.twitter

import org.slf4s.Logging
import twitter4j.{Twitter, TwitterException}

class TwitterApiService(twitter: Twitter) extends TwitterService with Logging {
  var tweet: String = null

  override def tweet(tweet: String) {
    try {
      sendToTwitter(tweet)
    } catch {
      case twitterException: TwitterException =>
        logException(twitterException)
    }
  }

  private def sendToTwitter(tweet: String): Unit = {
    log.info(s"sending tweet to Twitter: $tweet")

    twitter.updateStatus(tweet)

    log.info(s"successfully tweeted $tweet")
  }

  private def logException(twitterException: TwitterException): Unit = {
    log.error(s"Error while tweeting: ${twitterException.getErrorMessage}", twitterException)
  }
}
