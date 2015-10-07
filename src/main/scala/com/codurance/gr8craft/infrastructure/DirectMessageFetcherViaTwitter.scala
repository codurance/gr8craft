package com.codurance.gr8craft.infrastructure

import com.codurance.gr8craft.model.publishing._
import org.slf4s.Logging
import twitter4j.{Paging, ResponseList, Status, Twitter}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class DirectMessageFetcherViaTwitter(twitter: Twitter) extends DirectMessageFetcher with Logging {
  private val DEFAULT_PAGING = new DirectMessageId(1)

  override def fetchAfter(lastFetched: Option[DirectMessageId], successAction: (List[DirectMessage]) => Unit) = {
    log.info(s"retrieving" + logRetrievingMessagesFrom(lastFetched))

    Future {
      twitter.getDirectMessages(createPaging(lastFetched))
    }.onComplete(completeFetchingDirectMessages(successAction, lastFetched))
  }

  private def createPaging(lastFetched: Option[DirectMessageId]): Paging = {
    val paging = new Paging()
    paging.setSinceId(lastFetched.getOrElse(DEFAULT_PAGING).id)
    paging
  }

  private def logRetrievingMessagesFrom(lastFetched: Option[DirectMessageId]): String = {
    s" direct messages ${lastFetched.map("after " + _.id).getOrElse("")}"
  }

  private def completeFetchingDirectMessages(successAction: (List[DirectMessage]) => Unit, lastFetched: Option[DirectMessageId]): (Try[ResponseList[twitter4j.DirectMessage]]) => Unit = {
    case Success(messages) =>
      log.info(s"successfully retrieved" + logRetrievingMessagesFrom(lastFetched))
      successAction(convertMessages(messages))
    case Failure(twitterException) =>
      log.error(s"Error while fetching direct messages: ${twitterException.getMessage}", twitterException)
  }

  private def convertMessages(messages: ResponseList[twitter4j.DirectMessage]): List[DirectMessage] = {
    messages.asScala
      .map(message => DirectMessage(message.getSenderScreenName, message.getText, DirectMessageId(message.getId)))
      .toList
  }
}
