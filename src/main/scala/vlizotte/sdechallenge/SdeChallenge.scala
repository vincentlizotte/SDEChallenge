package vlizotte.sdechallenge

import scala.util.Random

object SdeChallenge {
  def main(args: Array[String]): Unit = {
    val windowSize = 10
    println(s"Creating a MovingAverage with Window Size of $windowSize")
    var movingAverage: MovingAverageable = MovingAverage(windowSize)
    for (i <- 1 to 100) {
      val toAdd = Random.nextInt(10)
      movingAverage = movingAverage.add(toAdd)
      println(s" - Added $toAdd. It now has ${movingAverage.elements.length} elements and its Moving Average is ${movingAverage.average}")
    }
  }
}
