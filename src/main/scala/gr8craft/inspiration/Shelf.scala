package gr8craft.inspiration

trait Shelf {
  def add(inspiration: Inspiration)

  def next: Inspiration
}
