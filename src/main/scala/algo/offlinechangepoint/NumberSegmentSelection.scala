package algo.offlinechangepoint

import breeze.linalg._
import breeze.plot._
import various.TypeDef._
import various.{ Math }

object NumberSegmentSelection {
  class optimalNumberSegmentsReturn(
    val segPoints: Array[Index],
    val rawCost: Array[Real],
    val regCost: DenseVector[Real],
    val penCost: DenseVector[Real])

  /**
   * Take the risk for every value of D, compute the penalized risk using a slope heuristic, then return an oracle
   * estimate of the optimal number of segments.
   *
   * @param cost the unpenalized cost of each segment
   * @param nObs number of observations
   */
  def optimalNumberSegments(
    resFromSegmentation: Segmentation.Accumulator,
    nObs: Index): optimalNumberSegmentsReturn = {
    val cost = resFromSegmentation.L.map(_.head.cost)

    val DMax = cost.size - 1
    val DMin: Index = (0.6 * DMax.toReal).toIndex

    val funcs = // functions that will be evaluated for various values of D (from DMin to DMax)
      Array[Index => Real](
        D => D.toReal / nObs.toReal,
        D => Math.logBinomial(nObs - 1, D - 1) / nObs.toReal,
        D => 1.0) // for constant term

    val x = // design matrix constructed from evaluations of funcs
      DenseMatrix.tabulate[Real](DMax - DMin + 1, 3)((i, j) => {
        val D = i + DMin // because tabulate will start evaluation at 0, this is an offset
        funcs(j)(D)
      })

    val y =
      DenseVector.tabulate[Real](DMax - DMin + 1)(i => {
        val D = i + DMin;
        cost(D)
      })

    val beta = Math.linearRegression(x, y) // least-square estimation
    val C = DenseVector.tabulate[Real](3)(i => if (i < 2) -2.0 * beta(i) else 0.0) // non constant terms are multiplied by -2.0, constant term is fixed at 0

    val penalizedCost =
      DenseVector.tabulate[Real](cost.size)(D => {
        val penalty = DenseVector(funcs.map(_(D))).dot(C) // each function is evaluated at D, and the C coefficients are applied. TODO: this computation is partially redundant with the one used to get the value of x
        cost(D) + penalty
      })

    val regressedCost =
      DenseVector.tabulate[Real](cost.size)(D => {
        DenseVector(funcs.map(_(D))).dot(beta)
      })

    val regCost = regressedCost(1 to DMax)
    val penCost = penalizedCost(1 to DMax)
    val rawCost = cost.slice(1, DMax + 1)

    val bestD = argmin(penalizedCost)

    val segPoints = resFromSegmentation
      .L
      .map(_.head)
      .apply(bestD)
      .seg
      .reverse
      .toArray

    return new optimalNumberSegmentsReturn(segPoints, rawCost, regCost, penCost)
  }
}