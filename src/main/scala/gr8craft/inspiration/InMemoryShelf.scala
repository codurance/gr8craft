package gr8craft.inspiration


case class InMemoryShelf(_inspirations: Seq[Inspiration]) extends Shelf {
  var index = 0
  var inspirations = _inspirations

  override def next: Inspiration = {
    val next = inspirations(index)
    index = index + 1
    next
  }

  override def add(inspiration: Inspiration): Unit = inspirations = inspirations :+ inspiration
}
