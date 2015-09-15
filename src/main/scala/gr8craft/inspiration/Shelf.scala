package gr8craft.inspiration

trait Shelf {
  def withInspiration(inspiration: Inspiration): Shelf

  def next: Inspiration
}
