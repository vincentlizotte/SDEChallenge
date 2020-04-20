package vlizotte.sdechallenge

// Exposes a Moving Average view for an object, allowing the manipulation of a collection of elements and the actual calculation of that average
trait MovingAverageable {

  // Adds an element to the collection
  def add(element: BigDecimal): MovingAverageable

  // Returns the underlying list of all elements that this MovingAverageable instance operates on
  def elements: List[BigDecimal]

  // Computes the moving average of the underlying list of elements, with the exact details of that calculation,
  // such as the sliding window, to be determined by the implementing class
  def average: BigDecimal
}
