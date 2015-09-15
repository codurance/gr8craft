package gr8craft.article


case class InMemoryShelf(articles: Seq[Article]) extends Shelf {
  var index = 0
  var _articles = articles

  override def next: Article = {
    val next = _articles(index)
    index = index + 1
    next
  }

  override def add(article: Article): Unit = _articles = _articles :+ article
}
