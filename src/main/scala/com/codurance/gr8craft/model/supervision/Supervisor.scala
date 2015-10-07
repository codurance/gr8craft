package com.codurance.gr8craft.model.supervision

import java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.{ScheduledExecutorService, ScheduledFuture}

import akka.actor.{Actor, ActorRef}
import com.codurance.gr8craft.messages.{IsTerminated, Start, Stop, Trigger}
import org.slf4s.Logging

import scala.concurrent.duration.Duration

class Supervisor(duration: Duration, collaborators: List[ActorRef]) extends Actor with Logging {
  private val executor: ScheduledExecutorService = newSingleThreadScheduledExecutor()

  override def receive: Receive = {
    case Start => schedule()
    case Stop => shutdown()
    case IsTerminated => sender() ! isTerminated
  }

  private def schedule() {
    log.info(s"Scheduling every $duration.")

    collaborators.foreach(collaborator =>
      schedule(collaborator))
  }

  private def schedule(collaborator: ActorRef): ScheduledFuture[_] = {
    executor.scheduleAtFixedRate(createRunnable(collaborator), 0, duration.length, duration.unit)
  }

  private def createRunnable(collaborator: ActorRef): Runnable with Object {def run(): Unit} = {
    new Runnable {
      def run() = collaborator ! Trigger
    }
  }

  private def shutdown(): Unit = {
    log.info("shutting down execution...")

    executor.shutdown()
    executor.awaitTermination(10, SECONDS)

    log.info("shutdown of execution complete")
  }

  private def isTerminated = executor.isTerminated
}
