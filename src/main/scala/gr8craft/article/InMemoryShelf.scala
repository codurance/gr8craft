package gr8craft.article


case class InMemoryShelf(articles: Seq[Article]) extends Shelf {
  override def first: Article = articles.head
}
