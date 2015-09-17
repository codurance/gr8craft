package gr8craft

import akka.actor.ActorRef
import gr8craft.ApplicationFactory.createApplication
import gr8craft.messages.{Start, Stop}

class ApplicationRunner(scheduler: ActorRef) {
  def startTwitterBot() {
    scheduler ! Start
  }

  def stop() {
    scheduler ! Stop
  }
}

object ApplicationRunner {
  def main(args: Array[String]) {
    val application: ApplicationRunner = createApplication()

    application.startTwitterBot()
  }
}
