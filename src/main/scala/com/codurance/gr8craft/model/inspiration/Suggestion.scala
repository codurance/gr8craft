package com.codurance.gr8craft.model.inspiration

class Suggestion(toParse: String) {
  val PatternForNewInspirations = """inspiration: (\S+) \| location: (\S+) \| contributor: (\S+)""".r

  def parse: Option[Inspiration] =
    toParse match {
      case PatternForNewInspirations(topic, location, contributor) =>
        Some(new Inspiration(topic, location, Some(contributor)))
      case _ => None
    }
}
