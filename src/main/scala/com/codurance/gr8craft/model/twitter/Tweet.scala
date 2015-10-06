package com.codurance.gr8craft.model.twitter

import com.codurance.gr8craft.model.inspiration.Inspiration

class Tweet(inspiration: Inspiration) {
  val FORMATTING = s"Your hourly recommended inspiration about ${inspiration.topic}: ${inspiration.location}"

  override def toString: String = {
    FORMATTING + inspiration.optionalContributor.fold(ifEmpty = "")(contributor => s" (via $contributor)")
  }
}