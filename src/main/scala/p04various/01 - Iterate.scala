package p04various

import scala.annotation.tailrec

object Iterate {
  /**
   * @param a state
   * @param f function applied to state at each iteration
   * @param condition condition(a) is true the computation is over before applying f to a
   */
  @tailrec
  def iterate[A](
      a: A,
      f: A => A,
      condition: A => Boolean)
  : A = if (condition(a)) a else iterate(f(a), f, condition)
}