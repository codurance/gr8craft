package com.codurance.gr8craft

import akka.actor.ActorRef
import com.codurance.gr8craft.Gr8craftFactory.createApplication
import com.codurance.gr8craft.messages.{Start, Stop}

class Gr8craft(scheduler: ActorRef) {
  def startTwitterBot() {
    scheduler ! Start
  }

  def stop() {
    scheduler ! Stop
  }
}

object Gr8craft {
  def main(args: Array[String]) {
    createApplication().startTwitterBot()
  }
}
