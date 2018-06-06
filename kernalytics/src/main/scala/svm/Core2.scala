package svm

import breeze.linalg._

import rkhs.KerEval
import various.TypeDef._

/**
 * Second implementation of Core, more modular, for easier unit testing.
 */
object Core2 {
  /**
   * Equivalent to takeStep in the article.
   * Optimize the problem for two given values of alpha.
   *
   * @return If optimization takes place, return (alpha1, alpha2, b), otherwise return None
   */

  class binaryData(alpha: Real, y: Real, e: Real)

  def binaryOptimization(i1: Index, i2: Index, alpha: DenseVector[Real], y: DenseVector[Real], cache: DenseVector[Real], kerEval: KerEval, C: Real): Option[(Real, Real, Real)] = {
    if (i1 == i2) return None

    val alph1 = alpha(i1)
    val alph2 = alpha(i2)
    val y1 = y(i1)
    val y2 = y(i2)
    val e1 = cache(i1)
    val E2 = cache(i2)

    val s = y1 * y2

    val (l, h) = // compute L, H, via equations (13) and (14)
      if (s < 0.0) { // y1 != y2
        (max(0.0, alph2 - alph1), min(C, C + alph2 - alph1))
      } else { // y1 == y2
        (max(0.0, alph2 + alph1 - C), min(C, alph2 + alph1))
      }

    if (l == h) return None // TODO: use epsilon for floating point number comparison

    val k11 = kerEval.k(i1, i1)
    val k12 = kerEval.k(i1, i2)
    val k22 = kerEval.k(i2, i2)

    val eta = k11 + k22 - 2.0 * k12

    val a2: Real =
      if (0.0 < eta) { // TODO: use epsilon for floating point number comparison
        ???
      } else {
        ???
      }

    ???
  }
}