package svm

import breeze.linalg._

import various.TypeDef._

/**
 * No heuristic version of the optimization algorithm, to single out the implementation error.
 */
object CoreNoHeuristic {
  val tolAlpha: Real = 1.0e-3 // tolerance in alpha space
  val tolObjective: Real = 1.0e-3 // tolerance in objective function value space (when kerEval is evaluated)

  /**
   * Call optimizeImpl with default initial values.
   */
  def optimize(nObs: Index, kerEval: (Index, Index) => Real, y: DenseVector[Real], C: Real): (DenseVector[Real], Real) = {
    val alpha = DenseVector.zeros[Real](nObs) // DenseVector are mutable, no need for a val
    val b0: Real = 0.0
    optimizeImpl(nObs, kerEval, y, C, alpha, b0)
  }

  var b: Real = 0.0 // global var are ugly, but necessary to keep the syntax close to the article
  val nSweep = 100

  /**
   * Debug version where the initial solution can be provided.
   */
  def optimizeImpl(nObs: Index, kerEval: (Index, Index) => Real, y: DenseVector[Real], C: Real, alpha: DenseVector[Real], b0: Real): (DenseVector[Real], Real) = {
    b = b0 // threshold

    val cached = DenseVector.zeros[Real](nObs)
    updateCache(cached, alpha, y, kerEval)
    checkSolution(kerEval, alpha, y, C)

    println(s"optimize, y: $y")

    for (k <- 0 to nSweep - 1) {
      println(s"k: $k")
      for (i1 <- 0 to nObs - 1) {
        for (i2 <- 0 to nObs - 1) {
          takeStep(i1, i2, alpha, y, C, cached, kerEval)
        }
      }
    }

    return (alpha, b)
  }

  /**
   * Update the error cache which contains u_i - y_i. This occurs any time a component of \alpha is modified.
   * u_i = w * x_i - b in the linear case
   * w = \sum_{i = 1}^N y_i \alpha_i x_i in the kernel case
   * u = \sum_{j = 1}^N y_j \alpha_j K(x_j, x)
   */
  def updateCache(cached: DenseVector[Real], alpha: DenseVector[Real], y: DenseVector[Real], kerEval: (Index, Index) => Real) = {
    val nObs = alpha.length
    for (i <- 0 to nObs - 1) {
      var currSum = -b

      for (j <- 0 to nObs - 1) {
        currSum += y(j) * alpha(j) * kerEval(j, i)
      }

      cached(i) = currSum - y(i)
    }

    println(s"updateCache: $cached")
  }

  def takeStep(i1: Index, i2: Index, alpha: DenseVector[Real], y: DenseVector[Real], C: Real, cached: DenseVector[Real], kerEval: (Index, Index) => Real): Index = {
    println(s"takeStep, i1: $i1, i2: $i2")
    if (i1 == i2) {
      println("i1 == i2")
      return 0
    }

    val alph1 = alpha(i1)
    val alph2 = alpha(i2)
    val y1 = y(i1)
    val y2 = y(i2)

    val E1 = cached(i1)
    val E2 = cached(i2)

    val s = y1 * y2

    println(s"takeStep: alph1: $alph1, alph2: $alph2")

    val (l, h) = if (s < 0.0) { // y1 != y2
      (max(0.0, alph2 - alph1), min(C, C + alph2 - alph1))
    } else { // y1 == y2
      (max(0.0, alph2 + alph1 - C), min(C, alph2 + alph1))
    }

    println(s"takeStep: l: $l, h: $h")

    if (math.abs(l - h) < tolAlpha) {
      println("l and h are too close")
      return 0
    }

    val k11 = kerEval(i1, i1)
    val k12 = kerEval(i1, i2)
    val k22 = kerEval(i2, i2)

    val eta = k11 + k22 - 2.0 * k12

    var a2: Real = 0.0 // use of a var to have a syntax similar to the article

    if (tolObjective < eta) {
      println("eta > 0.0")
      a2 = alph2 + y2 * (E1 - E2) / eta
      if (a2 < l) {
        a2 = l
      } else if (a2 > h) {
        a2 = h
      }
    } else {
      println("eta == 0.0")
      val f1 = y1 * (E1 + b) - alph1 * k11 - s * alph2 * k12
      val f2 = y2 * (E2 + b) - s * alph1 * k12 - alph2 * k22
      val L1 = alph1 + s * (alph2 - l)
      val H1 = alph1 + s * (alph2 - h)
      val psiL = L1 * f1 + l * f2 + 0.5 * L1 * L1 * k11 + 0.5 * l * l * k22 + s * l * L1 * k12
      val phiH = H1 * f1 + h * f2 + 0.5 * H1 * H1 * k11 + 0.5 * h * h * k22 + s * h * H1 * k12

      if (psiL < phiH - tolObjective) {
        a2 = l
      } else if (psiL > phiH + tolObjective) {
        a2 = h
      } else {
        a2 = alph2
      }
    }

    println(s"takeStep, a2: $a2")

    if (math.abs(a2 - alph2) < tolAlpha * (a2 + alph2 + tolObjective)) {
      println("a2 step too small")
      return 0
    }

    val a1 = alph1 + s * (alph2 - a2)

    val b1 = E1 + y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12 + b // update threshold to reflect change in Lagrange multiplier
    val b2 = E2 + y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2) * k22 + b

    if (tolAlpha < a1 && a1 < C - tolAlpha) {
      b = b1
    } else if (tolAlpha < a2 && a2 < C - tolAlpha) {
      b = b2
    } else {
      b = (b1 + b2) / 2.0
    }

    updateCache(cached, alpha, y, kerEval)
    checkSolution(kerEval, alpha, y, C)

    alpha(i1) = a1
    alpha(i2) = a2

    println("takeStep, alpha updated")
    return 1
  }

  /**
   * Output the objective function value to check convergence of the algorithm, and check the constraints.
   * Only useful in debug.
   */
  def checkSolution(kerEval: (Index, Index) => Real, alpha: DenseVector[Real], y: DenseVector[Real], C: Real) {
    val nObs = alpha.length
    var psi = 0.0

    for (i <- 0 to nObs - 1) {
      for (j <- 0 to nObs - 1) {
        psi += y(i) * y(j) * kerEval(i, j) * alpha(i) * alpha(j)
      }
    }

    psi /= 2.0
    psi -= sum(alpha)

    println(s"checkSolution, psi = $psi")

    for (i <- 0 to nObs - 1) {
      if (alpha(i) < 0.0) println(s"checkSolution error, alpha($i) < 0.0")
      if (C < alpha(i)) println(s"checkSolution error, C < alpha($i)")
    }

    if (tolAlpha < math.abs(y.dot(alpha))) println("checkSolution error, y.dot(alpha) != 0.0")
  }
}