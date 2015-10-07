package com.codurance.gr8craft.model.publishing

import com.codurance.gr8craft.model.inspiration.Inspiration
import org.scalatest.{FunSuite, Matchers}

class TweetShould extends FunSuite with Matchers {

  test("have a useful output") {
    val inspiration = new InspirationBuilder().withTopic("myTopic").withLocation("myLocation").build()

    new Tweet(inspiration).toString shouldBe "Your hourly recommended inspiration about myTopic: myLocation"
  }

  test("have a useful output when there is a contributor") {
    val inspiration = new InspirationBuilder().withTopic("myTopic").withLocation("myLocation").withContributor("@myContributor").build()

    new Tweet(inspiration).toString shouldBe "Your hourly recommended inspiration about myTopic: myLocation (via @myContributor)"
  }
}
