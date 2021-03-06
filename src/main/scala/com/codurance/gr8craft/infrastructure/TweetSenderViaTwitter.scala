package com.codurance.gr8craft.infrastructure

import com.codurance.gr8craft.model.publishing.{Tweet, TweetSender}
import org.slf4s.Logging
import twitter4j.{Status, Twitter}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class TweetSenderViaTwitter(twitter: Twitter) extends TweetSender with Logging {
  override def tweet(tweet: Tweet): Unit = {
    log.info(s"sending tweet to Twitter: $tweet")

    Future {
      twitter.updateStatus(tweet.toString)
    }.onComplete(completeTweeting())
  }

  private def completeTweeting(): (Try[Status]) => Unit = {
    val completeTweeting: (Try[Status]) => Unit = {
      case Success(tweetSend) =>
        log.info(s"successfully tweeted $tweetSend.getText()")
      case Failure(twitterException) =>
        log.error(s"Error while tweeting: ${twitterException.getMessage}", twitterException)
    }
    completeTweeting
  }
}
