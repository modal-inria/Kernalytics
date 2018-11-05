package algo.offlinechangepoint

import breeze.linalg.{ DenseVector, DenseMatrix }
import scala.annotation.tailrec
import various.TypeDef._
import offlinechangepoint.CostMatrix

/**
 * The index of the first observation is 0 and not 1 as in the article, because arrays in Scala are 0-based.
 * 
 * This implementation uses mutable states and loops, to be more readable and improve performances.
 */
object SegmentationMutable {
  /**
   * Cost associated with a particular segmentation. Note that the segmentation is kept as a List which has to be reversed to be exploited.
   * 
   * @param cost the cost the best partition which end at the current point
   * @param seg the starting point of each segment in the partition, reversed, because it is faster to prepend to a list
   */
  class SegCost(val cost: Real, val seg: List[Index])
  
  /**
   * Cost of the ignoredCost segmentation is so high that any other non degenerate alternative will be choosed instead.
   */
  val ignoredCost = new SegCost(Real.PositiveInfinity, Nil)
  
  /**
   * Loops as presented in Algorithm 3.
   */
  def loopOverTauP(nObs: Index, kerEval: (Index, Index) => Real, DMax: Index): DenseMatrix[SegCost] = {
    val L = new DenseMatrix[SegCost](DMax + 1, nObs) // same notation as in article, DenseMatrix is a mutable array hence the val
    
    for (D <- 0 to DMax) { // initialization of first column, for the segment [0, 0], as a var which will be reassigned to the result of various CostMatrix methods
      L(D, 0) = D match {
        case 0 => ignoredCost // partition is undefined for D = 0
        case 1 => new SegCost(0.0, List(0)) // D = 1, one element partition, cost of the segment [0, 0]
        case _ => ignoredCost // more segments than elements in the segment, impossible
      }
    }
    
    var C = CostMatrix.firstColumn(nObs, kerEval) // i-th coefficient contains the cost of the segment [i, tauP], initialized with the costs [i, 0], for which the only relevant element is the first, set at 0
    
    for (tauP <- 1 to nObs - 1) { // in current iteration, total cost of segment [0, tauP] are computed for various values of D
      C = CostMatrix.nextColumn(C, kerEval) // C is updated to contains the costs [i, tauP] for current tauP
      
      L(0, tauP) = ignoredCost // this will never be used, hence the Real.PositiveInfinity to ensure it is never selected as an optimal solution, this is similar to what is found in the initialAccumulator
      L(1, tauP) = new SegCost(C.c(0), List(0)) // one segment containing all the observations, directly compute the cost of the segment [0, tauP], using ColumnCostMatrix, for tau (the first element in the segment) = 0
      
      for (D <- 2 to math.min(DMax, tauP + 1)) { // in the segment [0, tauP], there can not be more than tauP + 1 segments
        L(D, tauP) = { // for all D - 1 partitions, compute the cost of the best new partition of what remains
          var bestCost = Real.PositiveInfinity
          var bestDPartition = -1          
          for (tau <- D to tauP) { // tau is the starting point of the new D-th segment, the case D = tauP occurs when the new segment only contains the last element, for example
            val totalCost = L(D - 1, tau - 1).cost + C.c(tau)
            if (totalCost < bestCost) {
              bestCost = totalCost
              bestDPartition = tau
            }
          }
          
          new SegCost(bestCost, bestDPartition :: L(D - 1, bestDPartition).seg)
        }
      }
    }
    
    return L
  }
}
