package com.codurance.gr8craft.model.inspiration

class Suggestion(toParse: String) {
  val PatternForNewInspirations = """inspiration: ([\s\S]+) \| location: ([\s\S]+) \| contributor: ([\s\S]+)""".r

  def parse: Option[Inspiration] =
    toParse match {
      case PatternForNewInspirations(topic, location, contributor) =>
        Option.apply(new Inspiration(topic, location, Option.apply(contributor)))
      case _ => Option.empty
    }
}
