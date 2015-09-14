package gr8craft.article

trait Shelf {
  def add(article: Article)

  def next: Article
}
