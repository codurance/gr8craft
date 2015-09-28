package gr8craft.twitter

import java.time.LocalDateTime

import gr8craft.messages
import gr8craft.messages.{Done, Message}
import org.slf4s.Logging
import twitter4j._

import scala.concurrent.{Future, Promise}

class TwitterApiService(twitter: AsyncTwitter) extends TwitterService with Logging {

  override def tweet(tweet: String): Future[Message] = {
    val promise: Promise[Message] = handleResponse

    sendTweet(tweet)

    promise.future
  }

  private def sendTweet(tweet: String): Unit = {
    log.info(s"sending tweet to Twitter: $tweet")
    twitter.updateStatus(tweet)
  }

  private def handleResponse: Promise[Message] = {
    val promise = Promise[Message]()

    twitter.addListener(new TwitterAdapter() {
      override def updatedStatus(status: Status) {
        log.info(s"successfully tweeted $status.getText()")
        promise.success(Done)
      }

      override def onException(twitterException: TwitterException, method: TwitterMethod): Unit = {
        log.error(s"Error while tweeting: ${twitterException.getErrorMessage}", twitterException)
        promise.failure(twitterException)
      }
    })

    promise
  }

  override def getDirectMessagesFrom(startingTime: LocalDateTime): Future[Set[messages.DirectMessage]] = ???
}
