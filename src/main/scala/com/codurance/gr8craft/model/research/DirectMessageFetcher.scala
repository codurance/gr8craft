package com.codurance.gr8craft.model.research

trait DirectMessageFetcher {
  def fetchAfter(lastFetched: Option[DirectMessageId], successAction: (List[DirectMessage]) => Unit)
}
