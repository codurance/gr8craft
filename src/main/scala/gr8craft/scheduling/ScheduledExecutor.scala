package gr8craft.scheduling

import java.util.concurrent.Executors.newSingleThreadScheduledExecutor
import java.util.concurrent.{ScheduledExecutorService, TimeUnit}

class ScheduledExecutor(timeUnit: TimeUnit, runnable: Runnable) extends Scheduler {
  def isShutDown = executor.isTerminated

  val executor: ScheduledExecutorService = newSingleThreadScheduledExecutor()

  def shutdown(): Unit = {
    executor.shutdown()
    executor.awaitTermination(1, TimeUnit.SECONDS)
  }

  def schedule() {
    executor.scheduleAtFixedRate(runnable, 0, 1, timeUnit)
  }
}
