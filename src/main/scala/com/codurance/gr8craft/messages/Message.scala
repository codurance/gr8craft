package com.codurance.gr8craft.messages

import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.model.research.{DirectMessage, DirectMessageId}

sealed trait Message

case object IsTerminated extends Message

case object Start extends Message

case object Stop extends Message

case object Trigger extends Message

case object InspireMe extends Message

case object Skip extends Message


case class AddInspiration(inspiration: Inspiration) extends Message

case class Inspire(inspiration: Inspiration) extends Message

case class Publish(tweet: Inspiration) extends Message

case class AddDirectMessage(directMessage: DirectMessage) extends Message

case class FetchDirectMessages(lastFetched: Option[DirectMessageId]) extends Message

