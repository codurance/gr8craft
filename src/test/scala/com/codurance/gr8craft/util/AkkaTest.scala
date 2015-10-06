package com.codurance.gr8craft.util

import akka.actor.ActorSystem
import akka.testkit.TestKit._
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfter, FunSuiteLike, Matchers, OneInstancePerTest}

abstract class AkkaTest(name: String) extends TestKit(ActorSystem(name)) with FunSuiteLike with BeforeAndAfter with OneInstancePerTest with ImplicitSender with Matchers {
  after {
    shutdownActorSystem(system)
  }
}
