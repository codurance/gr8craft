package gr8craft.inspiration

import org.scalatest.{Matchers, FunSuite}

class InspirationShould extends FunSuite with Matchers {
  test("have a pretty output") {
    val inspiration = new Inspiration("myTopic", "myLocation")

    inspiration.toString shouldBe "Inspiration: topic: myTopic location: myLocation"
  }

  test("be equal by it's values") {
    val inspiration = new Inspiration("myTopic", "myLocation")
    val equalInspiration = new Inspiration("myTopic", "myLocation")

    inspiration shouldEqual equalInspiration
  }

}
