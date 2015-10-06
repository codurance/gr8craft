package gr8craft.twitter

trait TwitterService {
  def tweet(tweet: Tweet, successAction: () => Unit, failureAction: () => Unit): Unit

  def fetchDirectMessagesAfter(lastFetched: Option[Long], successAction: (List[DirectMessage]) => Unit)
}
