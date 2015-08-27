package gr8craft.scheduling

import java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit.SECONDS

import org.slf4s.Logging

import scala.concurrent.duration.Duration


class ScheduledExecutor(duration: Duration, runnable: Runnable) extends Scheduler with Logging {
  def isShutDown = executor.isTerminated

  val executor: ScheduledExecutorService = newSingleThreadScheduledExecutor()

  def shutdown(): Unit = {
    log.info("shutting down execution...")

    executor.shutdown()
    executor.awaitTermination(10, SECONDS)

    log.info("shutdown of execution complete")
  }

  def schedule() {
    log.info(s"Scheduling every $duration.")
    executor.scheduleAtFixedRate(runnable, 0, duration.length, duration.unit)
  }
}
