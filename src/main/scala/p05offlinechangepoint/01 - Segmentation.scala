package p05offlinechangepoint

import breeze.linalg.{argmin, DenseVector} // not imported to avoid masking scala Vector with Breeze Vector
import p04various.TypeDef._

object Segmentation {
  /**
   * Cost associated with a particular segmentation. Note that the segmentation is kept as a List which has to be reversed to be exploited.
   * */
  case class SegCost (val cost: Real, val seg: List[Index])
  
  /**
   * Accumulator used in Algorithm 3. The access pattern can be represented as the function segCost($\tau'$)(D) where $\tau'$. Inside are computed all the costs
   * and corresponding segmentations for sub-collections containing observations from 0 to $\tau'$ - 1 (included).
   * Note that the 0 element of the Vector will never be accessed, as it corresponds to an empty sub-collection.
   * It is considered only to simplify notations and to avoid offsetting every index to access elements.
   */
  type Accumulator = Vector[Array[SegCost]]
  
  /**
   * Cost of the segmentation with D segments of a sub-collection of the first $\tau'$ observations of a n long collection.
   * Use the knowledge of the D - 1 optimal segmentations and the cost matrix to compute it.
   * 
   * @param acc The accumulator containing the previously computed values, among which the quantities corresponding to the D - 1 segments will be used.
   * @param tau Index of the first element of the new segment that is introduced.
   * @param D number of segments in the resulting segmentation
   * @param ccm Cost matrix used to compute the cost of the new segment.
   */
  def candidateCost (
      acc: Accumulator,
      tau: Index,
      D: Index,
      ccm: CostMatrix.ColumnCostMatrix)
  : Real = acc(tau)(D - 1).cost + ccm.c(tau)
  
  /**
   * Compute the candidate costs for a range of starting points, and return the segCost object describing the best segmentation containing D segments and finishing at $\tau'$.
   * This is used to fill the $\tau'$ + 1 element of the Accumulator.
   */
  def optimalCost(
      acc: Accumulator,
      D: Index,
      ccm: CostMatrix.ColumnCostMatrix): SegCost = {
    val tauP = acc.size // Index of the last observation in the subdomain considered.
    val tauFirst = D - 1 // First value of tau for which a candidate cost is computed. This correspond to the case where all previous segments have one observation each, and the candidate segment contains all the remaining observations.
    val candidateCosts = DenseVector.tabulate(tauP - tauFirst + 1)(i => { // the last candidate is t = tauP, which corresponds to the case where last segment only contains the tauP observation
      val tau = i + tauFirst // translation from local index to observation index
      candidateCost (
    		  acc,
    		  tau,
    		  D,
    		  ccm)
    }) 
    
    val minLocalIndex = argmin(candidateCosts)
    val minCost = candidateCosts(minLocalIndex)
    val minGlobalIndex = minLocalIndex + tauFirst
    
    return SegCost(minCost, minGlobalIndex :: acc(minGlobalIndex)(D - 1).seg)
  }
  
  /**
   * Recursively compute the optimal segmentation for all allowed values of D, given $\tau'$ (the index of the last element of the subdomain considered), and the computed values from the previous subdomains.
   * The results will be stored in an updated Accumulator, in the $\tau'$ + 1  element.
   */
  def costForEachD(
      acc: Accumulator, 
      ccm: CostMatrix.ColumnCostMatrix,
      DMax: Index): Array[SegCost] = {
    val tauP = acc.size
    val minD = 1
    val maxD = math.min(tauP + 1, DMax) // + 1 because observations are 0-based, while the number of segments is 1-based, so to speak.
    Array.tabulate(maxD - minD + 1)(D => D match {
      case 0 => SegCost(Real.PositiveInfinity, Nil) // this will never be used, hence the Real.PositiveInfinity to ensure it is never selected as an optimal solution
      case 1 => SegCost(ccm.c(0), List(0)) // one segment containing all the observations, directly compute the cost of the subsegment, using ColumnCostMatrix, for $\tau$ = 0
      case _ => optimalCost(acc, D, ccm) // recursive computation, using dynamical programming
    })
  }
}
