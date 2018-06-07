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
    var cache = Core.computeCache(alpha, b, y, kerEval) // will be reassigned by future cache computation

    for (n <- 0 to nLoop - 1) {
      for (i1 <- 0 to kerEval.nObsLearn - 1) {
        for (i2 <- 0 to kerEval.nObsLearn - 1) {
          val res = Core.binaryOptimization(i1, i2, alpha, b, y, cache, kerEval, C)

          res match {
            case Some((a1, a2, bNew)) => {
              alpha(i1) = a1
              alpha(i2) = a2
              b = bNew

              cache = Core.computeCache(alpha, b, y, kerEval)
            }
            case None => {}
          }
        }
      }
    }

    return (alpha, b)
  }

  /**
   * KKT-based heuristic, as presented under the name "main routine" in the SMO article.
   */
  def kkt(kerEval: KerEval, y: DenseVector[Real], C: Real): (DenseVector[Real], Real) = {
    val alpha = DenseVector.zeros[Real](kerEval.nObsLearn) // parameters are mutable, to simplify the outer algorithm
    var b: Real = 0
    var cache = Core.computeCache(alpha, b, y, kerEval) // will be reassigned by future cache computation

    var numChanged: Index = 0
    var examineAll = true

    while (numChanged > 0 || examineAll) { // as long as some coefficients where changed in the last iteration, or that a complete examination is required
      numChanged = 0
      val indices = randomIndices(alpha, C, examineAll)

      for (i2 <- indices) {
        selectI1(i2, alpha, kerEval, y, C, cache) match {
          case Some((i1, (a1, a2, newB))) => {
            alpha(i1) = a1
            alpha(i2) = a2
            b = newB
            numChanged += 1

            cache = Core.computeCache(alpha, b, y, kerEval)
          }
          case None => {}
        }
      }
    }

    return (alpha, b)
  }

  /**
   * Get the indices of non-bound Lagrange multiplier, in a random order to avoid bias in the algorithm.
   */
  def randomIndices(alpha: DenseVector[Real], C: Real, examineAll: Boolean): IndexedSeq[Index] = {
    val nObs = alpha.length
    val all = util.Random.shuffle(0 to nObs - 1)
    return if (examineAll) all else all.filter(i => 0.0 < alpha(i) || alpha(i) < C)
  }

  /**
   * Use the KKT violation as a criterium for modification. Corresponds to examineExample in the article.
   *
   * @return what Core.binaryOptimization returns
   */
  def selectI1(i2: Index, alpha: DenseVector[Real], kerEval: KerEval, y: DenseVector[Real], C: Real, cache: DenseVector[Real]): Option[(Index, (Real, Real, Real))] = {
    //    println(s"examineExample, i2: $i2")
    //    val nObs = alpha.length
    //
    //    val y2 = y(i2)
    //    val alph2 = alpha(i2)
    //    val E2 = cached(i2)
    //    val r2 = E2 * y2
    //
    //    if ((r2 < -tol && alph2 < C) || (r2 > tol && alph2 > 0)) { // KKT violated AND non-bound observation
    //      println("examineExample, KKT violated")
    //      val randomIndices = randomNonBoundIndices(alpha, C)
    //
    //      if (randomIndices._1.length > 1) {
    //        println("examineExample, first heuristic")
    //        val i1 = secondChoiceHeuristic(E2, cached)
    //        if (takeStep(i1, i2, alpha, y, C, cached, kerEval) == 1) return 1 // if takeStep returns 0, switch to next heuristic
    //      }
    //
    //      for (i1 <- 0 to randomIndices._1.length - 1) { // loop over all non-zero and non-C alpha, starting at random point
    //        println("examineExample, second heuristic")
    //        if (takeStep(i1, i2, alpha, y, C, cached, kerEval) == 1) return 1
    //      }
    //
    //      for (i1 <- 0 to nObs - 1) { // loop over all non-zero and non-C alpha, starting at random point TODO: there is no need to loop over the previously discarded non bound cases
    //        println("examineExample, third heuristic")
    //        if (takeStep(i1, i2, alpha, y, C, cached, kerEval) == 1) return 1
    //      }
    //    }
    //
    //    return 0 // no second Lagrange multiplier was a good enough candidate for the heuristic

    ???
  }

  /**
   * Step size used in takeStep is approximated by |E1 - E2|. The heuristic selects the second candidate for which the step size is maximal.
   */
  def secondChoiceHeuristic(E2: Real, cached: DenseVector[Real]): Index = {
    val nObs = cached.length
    val dis = cached.map(E1 => math.abs(E2 - E1))

    return argmax(dis)
  }

}