package gr8craft.twitter

import gr8craft.messages.{DirectMessage, Message}

import scala.concurrent.Future

trait TwitterService {
  def tweet(tweet: String): Future[Message]

  def getDirectMessages: Future[DirectMessage]
}
