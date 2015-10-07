package com.codurance.gr8craft.infrastructure

import com.codurance.gr8craft.model.publishing.{DirectMessage, DirectMessageId, Tweet, TweetSender}
import org.slf4s.Logging
import twitter4j.{Paging, ResponseList, Status, Twitter}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class TweetSenderViaTwitter(twitter: Twitter) extends TweetSender with Logging {
  override def tweet(tweet: Tweet, successAction: () => Unit, failureAction: () => Unit): Unit = {
    log.info(s"sending tweet to Twitter: $tweet")

    Future {
      twitter.updateStatus(tweet.toString)
    }.onComplete(completeTweeting(successAction, failureAction))
  }

  private def completeTweeting(successAction: () => Unit, failureAction: () => Unit): (Try[Status]) => Unit = {
    val completeTweeting: (Try[Status]) => Unit = {
      case Success(tweetSend) =>
        log.info(s"successfully tweeted $tweetSend.getText()")
        successAction.apply()
      case Failure(twitterException) =>
        log.error(s"Error while tweeting: ${twitterException.getMessage}", twitterException)
        failureAction.apply()
    }
    completeTweeting
  }

  private def logRetrievingMessagesFrom(lastFetched: Option[DirectMessageId]): String = {
    s" direct messages ${lastFetched.map("after " + _.id).getOrElse("")}"
  }
}
