package gr8craft.article

import org.scalatest.{Matchers, FunSuite}

class InMemoryShelfShould extends FunSuite with Matchers {

  test("add new articles add the end of the shelf and return them first in first out") {
    val article = new Article("topic", "location")
    val laterArticle = new Article("another topic", "another location")
    val shelf = new InMemoryShelf

    shelf.add(article)
    shelf.add(laterArticle)

    shelf.next shouldBe article
    shelf.next shouldBe laterArticle
  }
}
