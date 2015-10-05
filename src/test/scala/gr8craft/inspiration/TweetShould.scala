package gr8craft.inspiration

import gr8craft.twitter.Tweet
import org.scalatest.{FunSuite, Matchers}

class TweetShould extends FunSuite with Matchers {

  test("have a useful output") {
    val inspiration = new Inspiration("myTopic", "myLocation")

    new Tweet(inspiration).toString shouldBe "Your hourly recommended inspiration about myTopic: myLocation"
  }

  test("have a useful output when there is a contributor") {
    val inspiration = new Inspiration("myTopic", "myLocation", Option.apply("@myContributor"))

    new Tweet(inspiration).toString shouldBe "Your hourly recommended inspiration about myTopic: myLocation (via @myContributor)"
  }
}
