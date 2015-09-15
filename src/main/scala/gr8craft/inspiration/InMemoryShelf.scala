package gr8craft.inspiration


case class InMemoryShelf(inspirations: Seq[Inspiration]) extends Shelf {
  var index = 0

  override def next: Inspiration = {
    val next = inspirations(index)
    index = index + 1
    next
  }

  override def withInspiration(inspiration: Inspiration): Shelf = new InMemoryShelf(inspirations.drop(index) :+ inspiration)
}
