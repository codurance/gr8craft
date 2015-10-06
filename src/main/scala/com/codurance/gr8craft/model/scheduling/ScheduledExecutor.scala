package com.codurance.gr8craft.model.scheduling

import java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit.SECONDS

import akka.actor.{Actor, ActorRef}
import com.codurance.gr8craft.messages.{IsTerminated, Start, Stop, Trigger}
import org.slf4s.Logging

import scala.concurrent.duration.Duration


class ScheduledExecutor(duration: Duration, toBeScheduled: ActorRef) extends Actor with Logging {
  val executor: ScheduledExecutorService = newSingleThreadScheduledExecutor()

  override def receive: Receive = {
    case Start => schedule()
    case Stop => shutdown()
    case IsTerminated => sender() ! isTerminated
  }

  private def schedule() {
    log.info(s"Scheduling every $duration.")
    executor.scheduleAtFixedRate(new Runnable {
      def run() = toBeScheduled ! Trigger
    }, 0, duration.length, duration.unit)
  }

  private def shutdown(): Unit = {
    log.info("shutting down execution...")

    executor.shutdown()
    executor.awaitTermination(10, SECONDS)

    log.info("shutdown of execution complete")
  }

  private def isTerminated = executor.isTerminated
}
