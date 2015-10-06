package com.codurance.gr8craft.model.inspiration

import org.scalatest.{FunSuite, Matchers}

class SuggestionShould extends FunSuite with Matchers {
  test("parse a valid Suggestion to an inspiration") {
    val parsedInspiration = new Suggestion("inspiration: DDD | location: http://t.co/lqJDZlGcJE | contributor: @gr8contributor").parse

    parsedInspiration.get shouldEqual new Inspiration("DDD", "http://t.co/lqJDZlGcJE", Some("@gr8contributor"))
  }

  test("yield no Inspiration if not matched") {
    val parsedInspiration = new Suggestion("nonsense").parse

    parsedInspiration shouldEqual None
  }
}
