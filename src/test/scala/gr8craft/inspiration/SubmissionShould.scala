package gr8craft.inspiration

import org.scalatest.{FunSuite, Matchers}

class SubmissionShould extends FunSuite with Matchers {
  test("parse a valid Submission to an inspiration") {
    val parsedInspiration = new Submission("inspiration: DDD | location: http://t.co/lqJDZlGcJE | contributor: @gr8contributor").parse

    parsedInspiration.get shouldEqual new Inspiration("DDD", "http://t.co/lqJDZlGcJE", Option.apply("@gr8contributor"))
  }

  test("yield no Inspiration if not matched") {
    val parsedInspiration = new Submission("nonsense").parse

    parsedInspiration shouldEqual Option.empty
  }
}
