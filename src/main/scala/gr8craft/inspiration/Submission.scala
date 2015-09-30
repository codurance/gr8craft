package gr8craft.inspiration

class Submission(toParse: String) {
  val PatternForNewInspirations = """inspiration: (\S+) \| location: (\S+) \| contributor: (\S+)""".r

  def parse: Option[Inspiration] =
    toParse match {
      case PatternForNewInspirations(topic, location, contributor) =>
        Option.apply(new Inspiration(topic, location, Option.apply(contributor)))
      case _ => Option.empty
    }
}
