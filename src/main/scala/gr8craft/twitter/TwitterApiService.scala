package gr8craft.twitter

import gr8craft.messages.{Done, Message}
import org.slf4s.Logging
import twitter4j._

import scala.collection.JavaConverters._
import scala.concurrent.{Future, Promise}

class TwitterApiService(twitter: AsyncTwitter) extends TwitterService with Logging {

  override def tweet(tweet: String): Future[Message] = {
    val promise: Promise[Message] = handleTweetResponse

    sendTweet(tweet)

    promise.future
  }

  private def sendTweet(tweet: String): Unit = {
    log.info(s"sending tweet to Twitter: $tweet")
    twitter.updateStatus(tweet)
  }

  private def handleTweetResponse: Promise[Message] = {
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

  override def getDirectMessagesAfter(lastFetched: Option[Long]): Future[Set[DirectMessage]] = {
    val promise: Promise[Set[DirectMessage]] = handleDirectMessageResponse(lastFetched)

    requestDirectMessages(lastFetched)

    promise.future
  }

  private def requestDirectMessages(lastFetched: Option[Long]): Unit = {
    log.info(s"reading direct Messages after id: $lastFetched")
    val paging = new Paging()
    paging.setSinceId(lastFetched.getOrElse(0))
    twitter.getDirectMessages()
  }

  private def handleDirectMessageResponse(lastFetched: Option[Long]): Promise[Set[DirectMessage]] = {
    val promise = Promise[Set[DirectMessage]]()

    twitter.addListener(new TwitterAdapter() {
      override def gotDirectMessages(messages: ResponseList[twitter4j.DirectMessage]): Unit = {
        log.info(s"successfully retrieved messages${lastFetched.map("after " + _).getOrElse("")}")

        val directMessages = messages.asScala
          .map(message =>
            new DirectMessage(message.getSenderScreenName, message.getText, message.getId))
          .toSet

        promise.success(directMessages)
      }

      override def onException(twitterException: TwitterException, method: TwitterMethod): Unit = {
        log.error(s"Error while fetching direct messages: ${twitterException.getErrorMessage}", twitterException)
        promise.failure(twitterException)
      }
    })

    promise
  }
}
