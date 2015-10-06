package com.codurance.gr8craft.infrastructure

import com.codurance.gr8craft.model.twitter.{DirectMessage, Tweet, TwitterService}
import org.slf4s.Logging
import twitter4j.{ResponseList, Status, Paging, Twitter}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class TwitterApiService(twitter: Twitter) extends TwitterService with Logging {
  private val DEFAULT_PAGING = 1

  override def tweet(tweet: Tweet, successAction: () => Unit, failureAction: () => Unit): Unit = {
    log.info(s"sending tweet to Twitter: $tweet")

    Future {
      twitter.updateStatus(tweet.toString)
    }.onComplete(completeTweeting(successAction, failureAction))
  }

  def fetchDirectMessagesAfter(lastFetched: Option[Long], successAction: (List[DirectMessage]) => Unit) = {
    log.info(s"retrieving" + logRetrievingMessagesFrom(lastFetched))

    val paging = new Paging()
    paging.setSinceId(lastFetched.getOrElse(DEFAULT_PAGING))

    Future {
      twitter.getDirectMessages
    }.onComplete(completeFetchingDirectMessages(successAction, lastFetched))
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

  private def logRetrievingMessagesFrom(lastFetched: Option[Long]): String = {
    s" direct messages ${lastFetched.map("after " + _).getOrElse("")}"
  }

  private def completeFetchingDirectMessages(successAction: (List[DirectMessage]) => Unit, lastFetched: Option[Long]): (Try[ResponseList[twitter4j.DirectMessage]]) => Unit = {
    case Success(messages) =>
      log.info(s"successfully retrieved" + logRetrievingMessagesFrom(lastFetched))
      successAction(convertMessages(messages))
    case Failure(twitterException) =>
      log.error(s"Error while fetching direct messages: ${twitterException.getMessage}", twitterException)

  }

  private def convertMessages(messages: ResponseList[twitter4j.DirectMessage]): List[DirectMessage] = {
    messages.asScala
      .map(message => DirectMessage(message.getSenderScreenName, message.getText, message.getId))
      .toList
  }
}