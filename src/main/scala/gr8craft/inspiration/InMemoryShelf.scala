package gr8craft.inspiration


case class InMemoryShelf(inspirations: Seq[Inspiration]) extends Shelf {
  val defaultInspiration = new Inspiration("Interaction Driven Design", "http://www.ustream.tv/recorded/61480606")

  var index = 0

  override def next: Inspiration = {
    if (inspirations.size <= index) return defaultInspiration

    val next = inspirations(index)
    index = index + 1
    next
  }

  override def withInspiration(inspiration: Inspiration): Shelf = new InMemoryShelf(inspirations.drop(index) :+ inspiration)
}
