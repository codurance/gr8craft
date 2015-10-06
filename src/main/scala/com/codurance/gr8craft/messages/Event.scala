package com.codurance.gr8craft.messages

import com.codurance.gr8craft.model.inspiration.Inspiration
import com.codurance.gr8craft.model.twitter.DirectMessage

sealed trait Event

case class Triggered(lastFetched: Option[Long]) extends Event

case class Tweeted(inspiration: Inspiration) extends Event

case class GotDirectMessage(message: DirectMessage) extends Event