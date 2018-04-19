package svm

import breeze.linalg._

import various.TypeDef._

/**
 * Core implementation of the algorithm. Note that it is not (for now) written in the functional style, as it is a direct implementation of the paper:
 * Sequential Minimal Optimization, John C. Platt, 1998
 *
 * TODO: for debug, export the cost value at each iteration, to ensure that it decreases properly
 *
 * Algorithm ends when no Lagrange multipliers have been modified.
 */
object Core {
  /**
   * wrapper for mutable input, so that the syntax be similar to the original article, which was not written in a functional style.
   */
  class Mutable[A](var value: A) 

  val tol: Real = 1.0e-3 // tolerance in alpha space
  val eps: Real = 1.0e-3 // tolerance in objective function value space
  val epsLH: Real = 1e-12

  def optimize(nObs: Index, kerEval: (Index, Index) => Real, y: DenseVector[Real], C: Real): (DenseVector[Real], Real) = {
    val alpha = DenseVector.zeros[Real](nObs) // DenseVector are mutable, no need for a val
    val b0: Real = 0.0
    optimizeImpl(nObs, kerEval, y, C, alpha, b0)
  }
  
  /**
   * Debug version where the initial solution can be provided.
   */
  def optimizeImpl(nObs: Index, kerEval: (Index, Index) => Real, y: DenseVector[Real], C: Real, alpha: DenseVector[Real], b0: Real): (DenseVector[Real], Real) = {
    var b: Real = 0 // threshold
    var cached = updateCache(alpha, b, y, kerEval) // cached reference is modified by updateCache, hence the var
    
    println(s"optimize, cached: $cached")
    println(s"optimize, y: $y")

    var numChanged: Index = 0
    var examineAll = true

    while (numChanged > 0 || examineAll) { // as long as some coefficients where changed in the last iteration, or that a complete examination is required
      if (numChanged > 0) println("optimize, numchanged > 0")
      if (examineAll) println("optimize, examineAll")
      
      numChanged = 0
      if (examineAll) {
        for (i <- 0 to nObs - 1) {
          numChanged += examineExample(i, alpha, y, new Mutable(b), C, cached, kerEval)
        }
      } else { // Only examine the bound observations (observation for which a constraint is active)
        for (i <- 0 to nObs - 1) {
          if (tol < alpha(i) || alpha(i) < C - tol) {
            numChanged += examineExample(i, alpha, y, new Mutable(b), C, cached, kerEval)
          }
        }
      }

      if (examineAll == true) {
        examineAll = false
      } else if (numChanged == 0) { // if none were changed, that could mean that the heuristic could not find good candidates anymore, hence it is necessary to perform an exhaustive examination
        examineAll = true
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
  def updateCache(alpha: DenseVector[Real], b: Real, y: DenseVector[Real], kerEval: (Index, Index) => Real): DenseVector[Real] = {
    val nObs = alpha.length
    var e = DenseVector.zeros[Real](nObs)
    for (i <- 0 to nObs - 1) {
      var sum = - b
      for (j <- 0 to nObs - 1) {
        sum += y(j) * alpha(j) * kerEval(j, i)
      }
      e(i) = sum - y(i) // u_i - y_i
    }

    println(s"updateCache, e: $e")
    return e
  }

  /**
   * Use the KKT violation as a criterium for modification.
   *
   * @param i index of the Lagrange multiplier to optimize
   * @return 1 if at least one Lagrange multiplier has been modified
   */
  def examineExample(i2: Index, alpha: DenseVector[Real], y: DenseVector[Real], b: Mutable[Real], C: Real, cached: DenseVector[Real], kerEval: (Index, Index) => Real): Index = {
    println(s"examineExample, i2: $i2")
    val nObs = alpha.length

    val y2 = y(i2)
    val alph2 = alpha(i2)
    val E2 = cached(i2)
    val r2 = E2 * y2

    if ((r2 < -tol && alph2 < C) || (r2 > tol && alph2 > 0)) { // KKT violated for a non-bound observation
      println("examineExample, KKT violated")
      val randomIndices = randomNonBoundIndices(alpha, C)

      if (randomIndices._1.length > 1) {
        println("examineExample, first heuristic")
        val i1 = secondChoiceHeuristic(E2, cached)
        if (takeStep(i1, i2, alpha, y, b, C, cached, kerEval) == 1) return 1 // if takeStep returns 0, switch to next heuristic
      }

      for (i1 <- 0 to randomIndices._1.length - 1) { // loop over all non-zero and non-C alpha, starting at random point
        println("examineExample, second heuristic")
        if (takeStep(i1, i2, alpha, y, b, C, cached, kerEval) == 1) return 1
      }

      for (i1 <- 0 to nObs - 1) { // loop over all non-zero and non-C alpha, starting at random point TODO: there is no need to loop over the previously discarded non bound cases
        println("examineExample, third heuristic")
        if (takeStep(i1, i2, alpha, y, b, C, cached, kerEval) == 1) return 1
      }
    }

    return 0 // no second Lagrange multiplier was enough candidate for the heuristic
  }

  /**
   * Step size used in take step is approximated by |E 1 - E2|. The heuristic selects the second candidate for which the step size is maximal.
   */
  def secondChoiceHeuristic(E2: Real, cached: DenseVector[Real]): Index = {
    val nObs = cached.length
    val dis = cached.map(E1 => math.abs(E2 - E1))

    return argmax(dis)
  }

  /**
   * Get the indices of non-bound Lagrange multiplier, in a random order to avoid bias in the algorithm.
   */
  def randomNonBoundIndices(alpha: DenseVector[Real], C: Real): (IndexedSeq[Index], IndexedSeq[Index]) = {
    val nObs = alpha.length
    val all = util.Random.shuffle(0 to nObs - 1)
    return (all.filter(i => tol > alpha(i) || alpha(i) < C - tol), all)
  }

  def takeStep(i1: Index, i2: Index, alpha: DenseVector[Real], y: DenseVector[Real], b: Mutable[Real], C: Real, cached: DenseVector[Real], kerEval: (Index, Index) => Real): Index = {
    println(s"takeStep, i1")
    if (i1 == i2) return 0
    
    val alph1 = alpha(i1)
    val alph2 = alpha(i2)
    val y1 = y(i1)
    val y2 = y(i2)

    val E1 = cached(i1)
    val E2 = cached(i2)

    val s = y1 * y2

    println(s"takeStep: alph1: $alph1, alph2: $alph2")
    
    val (l, h) = if (y1 != y2) { // uppercases for l and h forbidden by Scala syntax: https://stackoverflow.com/questions/12636972/how-to-pattern-match-into-an-uppercase-variable
      (max(0.0, alph2 - alph1), min(C, C + alph2 - alph1))
    } else {
      (max(0.0, alph2 + alph1 - C), min(C, alph2 + alph1))
    }

    println(s"takeStep: l: $l, h: $h")
    
    if (math.abs(l - h) < epsLH) {
      println("l and h are too close")
      return 0
    }

    val k11 = kerEval(i1, i1)
    val k12 = kerEval(i1, i2)
    val k22 = kerEval(i2, i2)

    val eta = k11 + k22 - 2 * k12

    var a2: Real = 0.0 // use of a var to have a syntax similar to the article

    if (eta > 0.0) {
      a2 = alph2 + y2 * (E1 - E2) / eta
      if (a2 < l) {
        a2 = l
      } else if (a2 > h) {
        a2 = h
      }
    } else {
      val f1 = y1 * (E1 + b.value) - alph1 * k11 - s * alph2 * k12
      val f2 = y2 * (E2 + b.value) - s * alph1 * k12 - alph2 * k22
      val L1 = alph1 + s * (alph2 - l)
      val H1 = alph1 + s * (alph2 - h)
      val psiL = L1 * f1 + l * f2 + 0.5 * L1 * L1 * k11 + 0.5 * l * l * k22 + s * l * L1 * k12
      val phiH = H1 * f1 + h * f2 + 0.5 * H1 * H1 * k11 + 0.5 * h * h * k22 + s * h * H1 * k12

      if (psiL < phiH - eps) {
        a2 = l
      } else if (psiL > phiH + eps) {
        a2 = h
      } else {
        a2 = alph2
      }
    }
    
    println(s"takeStep, a2: $a2")

    if (math.abs(a2 - alph2) < eps * (a2 + alph2 + eps)) {
      return 0
    }

    val a1 = alph1 + s * (alph2 - a2)

    val b1 = E1 + y1 * (a1 - alph1) * k11 + y2 * (a2 - alph2) * k12 + b.value // update threshold to reflect change in Lagrange multiplier
    val b2 = E2 + y1 * (a1 - alph1) * k12 + y2 * (a2 - alph2) * k22 + b.value
    b.value = (b1 + b2) / 2.0
    
    updateCache(alpha, b.value, y, kerEval)

    alpha(i1) = a1
    alpha(i2) = a2
    
    println("takeStep, alpha updated")
    return 1
  }
}