package vlizotte.sdechallenge

import java.util.NoSuchElementException

class MovingAverage(val windowSize: Int, val elements: List[BigDecimal]) extends MovingAverageable {
  require(windowSize > 0, "windowSize must be > 0")

  // Prepend an element to the list
  override def add(element: BigDecimal): MovingAverageable = MovingAverage(windowSize, element :: elements)

  // Computes the moving average, using the instance's window size. Implemented as lazy val to cache result for this instance.
  override lazy val average: BigDecimal = {
    val sublist = elements.take(windowSize)
    if (sublist.isEmpty)
      throw new NoSuchElementException("Attempted to calculate average of an empty list")
    else
      sublist.sum / sublist.length
  }
}

object MovingAverage {
  def apply(windowSize: Int, elements: List[BigDecimal] = Nil) = new MovingAverage(windowSize, elements)
}