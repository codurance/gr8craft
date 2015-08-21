package gr8craft.scheduling

import java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.{ScheduledExecutorService, TimeUnit}

import org.slf4s.Logging


class ScheduledExecutor(timeUnit: TimeUnit, runnable: Runnable) extends Scheduler with Logging {
  def isShutDown = executor.isTerminated

  val executor: ScheduledExecutorService = newSingleThreadScheduledExecutor()

  def shutdown(): Unit = {
    log.info("shutting down execution...")

    executor.shutdown()
    executor.awaitTermination(10, SECONDS)

    log.info("shutdown of execution complete")
  }

  def schedule() {
    log.info("scheduling execution once per " + timeUnit)

    executor.scheduleAtFixedRate(runnable, 0, 1, timeUnit)
  }
}
