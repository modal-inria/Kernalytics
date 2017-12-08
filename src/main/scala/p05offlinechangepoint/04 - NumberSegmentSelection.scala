package p05offlinechangepoint

import breeze.linalg._
import p04various.TypeDef._
import p04various.Math

object NumberSegmentSelection {
  /**
   * Take the risk for every value of D, compute the penalized risk using a slope heuristic, then return an oracle
   * estimation of the optimal number of segments.
   */
  def optimalNumberSegments(cost: Array[Real], n: Index): Index = {
    val DMax = cost.size - 1
    val DMin: Index = (0.6 * DMax.toDouble).toInt // TODO: should use Real and Index types
    
    val funcs = Array[Index => Real](
        D => D.toDouble / n.toDouble,
        D => Math.logBinomial(n - 1, D - 1),
        D => 1.0)
        
    val x = DenseMatrix.tabulate[Real](DMax - DMin + 1, 3)((i, j) => {
      val D = i + DMin
      funcs(j)(D)
    })
    
    val y = DenseVector.tabulate[Real](DMax - DMin + 1)(i => {
      val D = i + DMin;
      cost(D)
    })
    
    val beta = Math.linearRegression(x, y)
    val C = DenseVector.tabulate[Real](3)(i => if (i < 2) -2.0 * beta(i) else 0.0)
    
    val penalizedCost = DenseVector.tabulate[Real](cost.size)(D => {
      val penalty = DenseVector(funcs.map(_(D))).dot(C)
      cost(D) + penalty
    })

    return argmin(penalizedCost) // TODO: replace with penalized cost
  }
}