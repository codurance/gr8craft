package gr8craft.inspiration

import org.scalatest.{Matchers, FunSuite}

class InMemoryShelfShould extends FunSuite with Matchers {
  test("return inspirations on the shelf first in first out") {
    val inspiration = new Inspiration("topic", "location")
    val laterInspiration = new Inspiration("another topic", "another location")

    val shelf = new InMemoryShelf(List(inspiration, laterInspiration))

    shelf.next shouldBe inspiration
    shelf.next shouldBe laterInspiration
  }

  test("withInspiration new inspirations withInspiration the end of the shelf") {
    val inspiration = new Inspiration("topic", "location")
    val laterInspiration = new Inspiration("another topic", "another location")

    val shelf = new InMemoryShelf(List())
      .withInspiration(inspiration)
      .withInspiration(laterInspiration)

    shelf.next shouldBe inspiration
    shelf.next shouldBe laterInspiration
  }
}
