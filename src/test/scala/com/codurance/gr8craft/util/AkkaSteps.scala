package com.codurance.gr8craft.util

import akka.actor.ActorSystem
import akka.testkit.TestKit
import akka.testkit.TestKit._
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import org.scalatest.concurrent.Eventually

class AkkaSteps(systemName: String) extends TestKit(ActorSystem(systemName)) with ScalaDsl with EN with Matchers with Eventually {
  private val numberOfScenarios = 2

  private var testCount = 0;

  After() { _ =>
    testCount = testCount + 1
    if (testCount >= numberOfScenarios)
      shutdownActorSystem(system)
  }
}
