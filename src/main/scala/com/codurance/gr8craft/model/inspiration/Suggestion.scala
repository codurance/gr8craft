package com.codurance.gr8craft.model.inspiration

class Suggestion(toParse: String) {
  private val PATTERN_FOR_SUGGESTIONS = """inspiration: (\S+) \| location: (\S+) \| contributor: (\S+)""".r

  def parse: Option[Inspiration] =
    toParse match {
      case PATTERN_FOR_SUGGESTIONS(topic, location, contributor) =>
        Some(new Inspiration(topic, location, Some(contributor)))
      case _ => None
    }
}
