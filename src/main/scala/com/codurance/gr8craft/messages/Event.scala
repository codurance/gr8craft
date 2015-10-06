package com.codurance.gr8craft.messages

import com.codurance.gr8craft.model.inspiration.Inspiration

sealed trait Event

case class Triggered(lastFetched: Option[Long]) extends Event

case class Tweeted(inspiration: Inspiration) extends Event

case class Added(inspiration: Inspiration) extends Event

case class ReadDirectMessage(id: Long) extends Event
