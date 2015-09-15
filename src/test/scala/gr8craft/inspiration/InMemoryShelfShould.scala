package gr8craft.inspiration

import org.scalatest.{FunSuite, Matchers}

class InMemoryShelfShould extends FunSuite with Matchers {
  test("return inspirations on the shelf first in first out") {
    val inspiration = new Inspiration("topic", "location")
    val laterInspiration = new Inspiration("another topic", "another location")

    val shelf = new InMemoryShelf(List(inspiration, laterInspiration))

    shelf.next shouldBe inspiration
    shelf.next shouldBe laterInspiration
  }

  test("add new inspirations to the end of the shelf") {
    val inspiration = new Inspiration("topic", "location")
    val laterInspiration = new Inspiration("another topic", "another location")

    val shelf = new InMemoryShelf(List())
      .withInspiration(inspiration)
    shelf.next shouldBe inspiration

    val updatedShelf = shelf.withInspiration(laterInspiration)
    updatedShelf.next shouldBe laterInspiration
  }

  test("give default inspiration if it runs out of inspirations") {
    val shelf = new InMemoryShelf(List())

    val inspiration = shelf.next
   
    inspiration.topic shouldBe "Interaction Driven Design"
    inspiration.location shouldBe "http://www.ustream.tv/recorded/61480606"
  }

}
