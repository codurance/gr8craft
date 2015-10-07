package com.codurance.gr8craft.model.publishing

trait TwitterService {
  def tweet(tweet: Tweet, successAction: () => Unit, failureAction: () => Unit): Unit

  def fetchDirectMessagesAfter(lastFetched: Option[DirectMessageId], successAction: (List[DirectMessage]) => Unit)
}
