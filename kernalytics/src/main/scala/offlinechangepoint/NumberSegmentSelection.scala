package offlinechangepoint

import breeze.linalg._
import breeze.plot._
import java.io.File
import various.TypeDef._
import various.{ Def, Math }

object NumberSegmentSelection {
  /**
   * Take the risk for every value of D, compute the penalized risk using a slope heuristic, then return an oracle
   * estimate of the optimal number of segments.
   *
   * @param cost the unpenalized cost of each segment
   * @param nObs number of observations
   */
  def optimalNumberSegments(
    resFromSegmentation: Segmentation.Accumulator,
    nObs: Index,
    visualOutput: Option[String]): Array[Index] = {
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

    visualOutput.map(baseDir => { // Option.map is only executed if visualOutput is of subtype Some
      val regressedCost =
        DenseVector.tabulate[Real](cost.size)(D => {
          DenseVector(funcs.map(_(D))).dot(beta)
        })

      val f = Figure()
      f.visible = false
      val p = f.subplot(0)

      p += plot(
        (1 to DMax).map(_.toReal),
        penalizedCost(1 to DMax),
        name = "Penalized Cost")

      p += plot(
        (1 to DMax).map(_.toReal),
        cost.slice(1, DMax + 1),
        name = "Cost")

      p += plot(
        (1 to DMax).map(_.toReal),
        regressedCost(1 to DMax),
        name = "Regressed Cost")

      p.title = "Penalized Cost"
      p.xlabel = "D"
      p.ylabel = "Cost"
      f.saveas(baseDir + "/lines.png")
    })

    val bestD = argmin(penalizedCost)

    return resFromSegmentation
      .L
      .map(_.head)
      .apply(bestD)
      .seg
      .reverse
      .toArray
  }
}