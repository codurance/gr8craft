package gr8craft.twitter

import java.time.LocalDateTime

import gr8craft.messages.{DirectMessage, Message}

import scala.concurrent.Future

trait TwitterService {
  def tweet(tweet: String): Future[Message]

  def getDirectMessagesFrom(startingTime: LocalDateTime): Future[Set[DirectMessage]]
}
