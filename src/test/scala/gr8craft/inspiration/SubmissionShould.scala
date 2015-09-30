package gr8craft.inspiration

import org.scalatest.{Matchers, FunSuite}

class SubmissionShould extends FunSuite with Matchers {
  test("be parsed from a string") {
    val parsedInspiration = new Submission("inspiration: DDD | location: http://t.co/lqJDZlGcJE | contributor: @gr8contributor").parse

    parsedInspiration.get shouldEqual new Inspiration("DDD", "http://t.co/lqJDZlGcJE", Option.apply("@gr8contributor"))
  }

  test("yield no Inspiration if not matched") {
    val parsedInspiration = new Submission("nonsense").parse

    parsedInspiration shouldEqual Option.empty
  }
}
