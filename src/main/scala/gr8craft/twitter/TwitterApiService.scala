package gr8craft.twitter

import org.slf4s.Logging
import twitter4j.{TwitterException, Twitter}

class TwitterApiService(twitter: Twitter) extends TwitterService with Logging {
  var tweet: String = null

  override def tweet(tweet: String) {
    try {
      sendToTwitter(tweet)
    }
    catch {
      case twitterException: TwitterException => log.error(twitterException.getErrorMessage)
    }
  }

  def sendToTwitter(tweet: String): Unit = {
    log.info("sending tweet to Twitter: " + tweet)

    twitter.updateStatus(tweet)

    log.info("successfully tweeted" + tweet)
  }
}
