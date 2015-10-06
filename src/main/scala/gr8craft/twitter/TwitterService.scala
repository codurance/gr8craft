package gr8craft.twitter

import gr8craft.messages.Message

import scala.concurrent.Future

trait TwitterService {
  def tweet(tweet: String): Future[Message]

  def getDirectMessagesAfter(lastFetched: Option[Long]): Future[Set[DirectMessage]]
}
