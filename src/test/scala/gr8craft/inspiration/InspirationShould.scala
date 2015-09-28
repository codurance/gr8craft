package gr8craft.inspiration

import org.scalatest.{FunSuite, Matchers}

class InspirationShould extends FunSuite with Matchers {

  test("have a useful output") {
    val inspiration = new Inspiration("myTopic", "myLocation")

    inspiration.toString shouldBe "Your hourly recommended inspiration about myTopic: myLocation"
  }

  test("have a useful output when there is a contributor") {
    val inspiration = new Inspiration("myTopic", "myLocation", Option.apply("@myContributor"))

    inspiration.toString shouldBe "Your hourly recommended inspiration about myTopic: myLocation (via @myContributor)"
  }

  test("be equal by it's values") {
    val inspiration = new Inspiration("myTopic", "myLocation")
    val equalInspiration = new Inspiration("myTopic", "myLocation")

    inspiration shouldEqual equalInspiration
  }

}
