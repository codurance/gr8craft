package gr8craft.twitter

import org.slf4s.Logging
import twitter4j._

class TwitterApiService(twitter: AsyncTwitter) extends TwitterService with Logging {
  var tweet: String = null

  override def tweet(tweet: String) {
    handleResponse()

    sendTweet(tweet)
  }

  private def sendTweet(tweet: String): Unit = {
    log.info(s"sending tweet to Twitter: $tweet")
    twitter.updateStatus(tweet)
  }

  private def handleResponse(): Unit = {
    twitter.addListener(new TwitterAdapter() {
      override def updatedStatus(status: Status) {
        log.info(s"successfully tweeted $status.getText()")
      }

      override def onException(twitterException: TwitterException, method: TwitterMethod): Unit = {
        log.error(s"Error while tweeting: ${twitterException.getErrorMessage}", twitterException)
      }
    })
  }
}
