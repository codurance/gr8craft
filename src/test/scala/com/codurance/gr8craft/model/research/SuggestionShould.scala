package com.codurance.gr8craft.model.research

import com.codurance.gr8craft.model.inspiration.Inspiration
import org.scalatest.{FunSuite, Matchers}

class SuggestionShould extends FunSuite with Matchers {
  test("parse a valid Suggestion to an inspiration") {
    val parsedInspiration = new Suggestion("inspiration: Technical Debt | location: https://t.co/p0l82zzEtw | contributor: @gr8contributor").parse

    parsedInspiration.get shouldEqual new Inspiration("Technical Debt", "https://t.co/p0l82zzEtw", Some("@gr8contributor"))
  }

  test("yield no Inspiration if not matched") {
    val parsedInspiration = new Suggestion("nonsense").parse

    parsedInspiration shouldEqual None
  }
}
