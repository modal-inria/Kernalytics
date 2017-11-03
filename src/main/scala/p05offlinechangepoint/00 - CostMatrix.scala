package p05offlinechangepoint

import breeze.linalg._
import p04various.TypeDef._

object CostMatrix {
  /**
   *  The cost Matrix can be computed iteratively, one column after another. Auxiliary quantities have to be tracked
   *  to this effect.
   */
  case class ColumnCostMatrix (
      val c: DenseVector[Real],
      val d: DenseVector[Real],
      val a: DenseVector[Real],
      val nObs: Index,
      val tauP: Index)
  
  /** 
   *  Initialize the CostMatrix computation by generating the first column. Corresponds to $C_{\tau, \tau'}, for $\tau = 0$ and $\tau' = 1$.
   *  Note that only the first component of each vector c, d and a is non-zero.
   *  
   *  @param kerEval function that takes indices of observations and returns the corresponding kernel evaluation. kerEval could contain the Gram matrix in cache or recompute value each time it is called. This should depend on the sample size.
   */
  def firstColumn (nObs: Index, kerEval: (Index, Index) => Real): ColumnCostMatrix = {
    val tauP = 1
    
    val d = DenseVector.tabulate[Real](nObs)(tau => if (tau < tauP) kerEval(0, 0) else 0.0)
    val a = DenseVector.tabulate[Real](nObs)(tau => 0.0)
    val c = DenseVector.tabulate[Real](nObs)(tau => 0.0)
    
    return ColumnCostMatrix(c, d, a, nObs, tauP)
  }
  
  /**
   *  Compute $C_{\tau, \tau' + 1}$ from $C_{\tau, \tau'}$.
   */
  def nextColumn (currColumn: ColumnCostMatrix, kerEval: (Index, Index) => Real): ColumnCostMatrix = {
    val tauP = currColumn.tauP + 1
    
    val d = DenseVector.tabulate[Real](currColumn.nObs)(tau => if (tau < tauP) currColumn.d(tau) + kerEval(currColumn.tauP, currColumn.tauP) else 0.0)
    val a = DenseVector.tabulate[Real](currColumn.nObs)(tau => 0.0)
    val c = DenseVector.tabulate[Real](currColumn.nObs)(tau => 0.0)
    
    return ColumnCostMatrix(c, d, a, currColumn.nObs, tauP)
  }
}