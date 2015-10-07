package com.codurance.gr8craft.model.publishing

trait DirectMessageFetcher {
  def fetchAfter(lastFetched: Option[DirectMessageId], successAction: (List[DirectMessage]) => Unit)
}
