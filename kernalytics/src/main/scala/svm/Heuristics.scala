package svm

import breeze.linalg._

import rkhs.KerEval
import various.TypeDef._

/**
 * This package contains the heuristics used to selects the pairs to be optimized.
 */
object Heuristics {
  /**
   * Loop over all the possible pairs, for a given number of iterations. Used as a first approach, for debugging.
   *
   * A mutable array is used to store alpha, as the algorithm updates a pair at a time.
   */
  def naive(kerEval: KerEval, y: DenseVector[Real], C: Real, nLoop: Index): (DenseVector[Real], Real) = {
    val alpha = DenseVector.zeros[Real](kerEval.nObsLearn) // parameters are mutable, to simplify the outer algorithm
    var b: Real = 0
    var cache = Core2.computeCache(alpha, b, y, kerEval) // will be reassigned by future cache computation

    for (n <- 0 to nLoop - 1) {
      for (i1 <- 0 to kerEval.nObsLearn - 1) {
        for (i2 <- 0 to kerEval.nObsLearn - 1) {
          val res = Core2.binaryOptimization(i1, i2, alpha, b, y, cache, kerEval, C)

          res match {
            case Some((a1, a2, bNew)) => {
              alpha(i1) = a1
              alpha(i2) = a2
              b = bNew

              cache = Core2.computeCache(alpha, b, y, kerEval)
            }
            case None => {}
          }
        }
      }
    }

    return (alpha, b)
  }
}