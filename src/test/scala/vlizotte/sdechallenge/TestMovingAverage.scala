package vlizotte.sdechallenge

import org.scalatest.FunSuite

class TestMovingAverage extends FunSuite {
  test("MovingAverage adds elements to the front of its list") {
    val movingAverage = MovingAverage(5)
    val added = movingAverage.add(12.3).add(9).add(82983.31)
    assert(added.elements == List[BigDecimal](82983.31, 9, 12.3))
  }

  test("MovingAverage of an empty list throws error") {
    val movingAverage = MovingAverage(5)
    intercept[NoSuchElementException] {
      movingAverage.average
    }
  }

  test("MovingAverage computes accurate average below, at and above window size") {
    val movingAverage = MovingAverage(5)
    val belowWindowSize = movingAverage.add(3).add(7).add(5)
    assert(belowWindowSize.average == (3 + 7 + 5) / 3)

    val atWindowSize = belowWindowSize.add(10).add(5.2)
    assert(atWindowSize.average == (3 + 7 + 5 + 10 + 5.2) / 5)

    val aboveWindowSize = atWindowSize.add(-12.5).add(38921.3)
    assert(aboveWindowSize.average == (5 + 10 + 5.2 - 12.5 + 38921.3) / 5)
  }

  test("MovingAverage with window <= 0 throws error") {
    intercept[IllegalArgumentException] {
      MovingAverage(0)
    }
    intercept[IllegalArgumentException] {
      MovingAverage(-5)
    }
  }
}
