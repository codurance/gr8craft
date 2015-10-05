package gr8craft.twitter

import gr8craft.inspiration.Inspiration


class Tweet(inspiration: Inspiration) {
  val FORMATTING = s"Your hourly recommended inspiration about ${inspiration.topic}: ${inspiration.location}"

  override def toString: String = {
    FORMATTING + inspiration.optionalContributor.fold(ifEmpty = "")(contributor => s" (via $contributor)")
  }
}
