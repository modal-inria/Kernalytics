package p05offlinechangepoint

import breeze.linalg.{DenseVector, DenseMatrix, sum, trace}
import p04various.TypeDef._
import p04various.Iterate

object CostMatrix {
  /**
   * The cost Matrix can be computed iteratively, one column after another. Auxiliary quantities have to be tracked
   * to this effect.
   * The i-th element of the vector c contains the cost of the segment [i, tauP - 1], which corresponds in the article notation to the element $C_{i, \tau'}$
   * 
   * @param c value of the cost
   * @param d value of intermediate quantity
   * @param a value of intermediate quantity
   * @param nObs total number of observations
   * @param tauP start index of the segment AFTER the one for which the cost matrix provides the cost.
   */
  case class ColumnCostMatrix(
      val c: DenseVector[Real],
      val d: DenseVector[Real],
      val a: DenseVector[Real],
      val nObs: Index,
      val tauP: Index)
  
  /** 
   * Initialize the CostMatrix computation by generating the first column. Corresponds to $C_{\tau, \tau'}, for $\tau = 0$ and $\tau' = 1$.
   * This also corresponds to the cost of a segment composed only of the first observation.
   * Note that only the first component of each vector c, d and a is non-zero.
   *  
   * @param kerEval function that takes indices of observations and returns the corresponding kernel evaluation. kerEval could contain the Gram matrix in cache or recompute value each time it is called. This should depend on the sample size.
   */  
  def firstColumn(nObs: Index, kerEval: (Index, Index) => Real): ColumnCostMatrix = {
    val tauP = 0 // index  of the last element included in the subsegment
    val k00 = kerEval(0, 0) // this is note the cost of the one observation segment, it is only used in the auxiliary quantities c, d and a
    
    val d = DenseVector.tabulate[Real](nObs)(tau => if (tau == 0) k00 else 0.0)
    val a = DenseVector.tabulate[Real](nObs)(i => if (i == 0) k00 else 0.0) // for the first column, the submatrix associated to the cost only contains one element, that is why d = -a (for the only non zero element)
    val c = DenseVector.tabulate[Real](nObs)(tau => 0.0) // the cost of the one-observation segment is 0
    
    return ColumnCostMatrix(c, d, a, nObs, tauP)
  }
  
  /**
   * Compute $C_{\tau, \tau' + 1}$ from $C_{\tau, \tau'}$, for all $\tau$.
   */
  def nextColumn(currColumn: ColumnCostMatrix, kerEval: (Index, Index) => Real): ColumnCostMatrix = {
    val tauP = currColumn.tauP + 1 // last element included in the segments [tau, tauP] corresponding to the current column
    
    val d = DenseVector.tabulate[Real](currColumn.nObs)(tau => if (tau <= tauP) currColumn.d(tau) + kerEval(tauP, tauP) else 0.0) 
    val a = DenseVector.tabulate[Real](currColumn.nObs)(i => i match {
      case _ if i < tauP  => currColumn.a(i) + 2.0 * kerEval(i, tauP)
      case _ if i == tauP => kerEval(tauP, tauP)
      case _ => 0.0
    })
    
    val sumA = DenseVector.zeros[Real](currColumn.nObs)
    val sumAb = DenseVector.zeros[Real](currColumn.nObs)
    for (tau <- (tauP to 0 by -1)) { // TODO: could be computed functionaly using a scan, on the reversed data...
      sumAb(tau) = tau match {
        case _ if tau == tauP => a(tau)
        case _ if tau < tauP  => a(tau) + sumAb(tau + 1)
        case _ => 0.0
      }
      
      sumA(tau) = - 1.0 / (tauP - tau + 1) * sumAb(tau)
    }

    val c = d + sumA
    
    return ColumnCostMatrix(c, d, a, currColumn.nObs, tauP)
  }
  
  /**
   * Direct computation of the cost matrix, for debugging purposes on small cases.
   */
  def completeCostMatrix[Data](observations: DenseVector[Data], kernel: (Data, Data) => Real): DenseMatrix[Real] = {
    val nObs = observations.size
    val gram = p00rkhs.Gram.generate(observations, kernel)
    
    val CostMatrix = DenseMatrix.tabulate[Real](nObs, nObs)((tauFirst: Index, tauLast: Index) => {
      val subMat = gram(tauFirst to tauLast, tauFirst to tauLast)
    		trace(subMat) - 1.0 / (tauLast - tauFirst + 1) * sum(subMat)
    })
    
    return CostMatrix
  }
  
  /**
   * Computation of the cost matrix using the iterative method, for debugging purposes on small cases
   */
  def completeMatrixViaColumn(nObs: Index, kerEval: (Index, Index) => Real): DenseMatrix[Real] = {
    val fc = firstColumn(nObs, kerEval)
    val initialCostVector = Vector[ColumnCostMatrix](fc)
    
    val costVector = Iterate.iterate(
        initialCostVector,
        (v: Vector[ColumnCostMatrix]) => v :+ nextColumn(v.last, kerEval),
        (v: Vector[ColumnCostMatrix]) => v.last.tauP == nObs - 1)
        
    val costMatrix = DenseMatrix.tabulate(nObs, nObs)((i, j) => costVector(j).c(i))
    
    return costMatrix
  }
}