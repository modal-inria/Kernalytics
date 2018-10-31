package algo.offlinechangepoint

import breeze.linalg.{ DenseVector, DenseMatrix }
import scala.annotation.tailrec
import various.TypeDef._
import offlinechangepoint.CostMatrix

/**
 * The index of the first observation is 0 and not 1 as in the article.
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
  val ignoredCost = new SegCost(Real.PositiveInfinity, Nil)
  
  /**
   * Loops as presented in Algorithm 3.
   */
  def loopOverTauP(nObs: Index, kerEval: (Index, Index) => Real, DMax: Index): DenseMatrix[SegCost] = {
    val L = new DenseMatrix[SegCost](DMax + 1, nObs) // same notation as in article, DenseMatrix is mutable hence the val
    
    for (D <- 0 to DMax) {
      L(D, 0) = D match {
        case 0 => ignoredCost
        case 1 => new SegCost(0.0, List(0)) // D = 1, cost of the segment [0, 0]
        case _ => ignoredCost // more segments than elements in the segment, impossible
      }
    }
    
    var C = CostMatrix.firstColumn(nObs, kerEval) // i-th coefficient contains the cost of the segment [i, tauP], initialized with the costs [i, 0]
    
    for (tauP <- 1 to nObs - 1) { // in current iteration, total cost of segment [0, tauP] are computed for various values of D
      C = CostMatrix.nextColumn(C, kerEval) // C is updated to contains the costs [i, tauP] for current tauP
      
      L(0, tauP) = ignoredCost // this will never be used, hence the Real.PositiveInfinity to ensure it is never selected as an optimal solution, this is similar to what is found in the initialAccumulator
      L(1, tauP) = new SegCost(C.c(0), List(0)) // one segment containing all the observations, directly compute the cost of the segment [0, tauP], using ColumnCostMatrix, for tau (the first element in the segment) = 0
      
      for (D <- 2 to math.min(DMax, tauP + 1)) { // in the segment [0, tauP], there can not be more than tauP + 1 segments
        L(D, tauP) = { // for all D - 1 partitions, compute the cost of the best new partition of what remains
          val bestCost = Real.PositiveInfinity
          val bestDPartition = -1
          for (tau <- 0 to tauP) { // tau is the starting point of the new D-th segment
            
          }
          
          ???
        }
      }
    }
    
    ???
  }
}
