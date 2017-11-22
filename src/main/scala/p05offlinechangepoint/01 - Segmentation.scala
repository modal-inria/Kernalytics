package p05offlinechangepoint

import breeze.linalg.{argmin, DenseVector} // not imported using _ to avoid masking Scala Vector with Breeze Vector
import p04various.TypeDef._

/**
 * The index of the first observation is 0 and not 1 as in the article.
 */
object Segmentation {  
  /**
   * Cost associated with a particular segmentation. Note that the segmentation is kept as a List which has to be reversed to be exploited.
   * */
  case class SegCost(val cost: Real, val seg: List[Index])
  
  /**
   * Accumulator used in Algorithm 3. The access pattern can be represented as the function segCost($\tau'$)(D) where $\tau'$ is the index of the first element AFTER the subsegment considered.
   * This follows the convention used in the article where the right bounds are open. The subsegment considered is therefore [0, $\tau'$[. All the costs are computed inside this storage
   * and corresponding segmentations for sub-collections containing observations from 0 to $\tau'$ - 1 (included).
   * Note that the 0 element of the Vector will never be accessed, as it corresponds to an empty sub-collection.
   * Note also that the article uses a 1-based indexing, while this implementation uses a 0-based indexing which is more natural regarding computational storage.
   * 
   * @param L storage for all the SegCost.
   * @param tauP index of the first observation NOT included in the subsegment (same notation as $L_{D, \tau'}$ in the article). This could be deduced from L.size, but the code is more expressive when it is kept as a separate value.
   * @param currCol column of the cost matrix
   */
  case class Accumulator(
      val L: Vector[Array[SegCost]],
      val tauP: Index,
      val currCol: CostMatrix.ColumnCostMatrix)
  
  /**
   * Outer loop in Algorithm 3, implemented using the recursive function iterate.
   */
  def loopOverTauP(
      nObs: Index,
      kerEval: (Index, Index) => Real,
      DMax: Index): Accumulator = {
    val initialAccumulator = Accumulator( // contains tauP = 0 and tauP = 1 to initialize the computation
        Vector(
            Array[SegCost](), // $\tau'$ = 0. The element 0 will never be accessed and is empty, it corresponds to a sub-segment that contains nothing
            Array( // $\tau'$ = 1. The element 1 contains only the cost of the segment consisting of the first observation.
                SegCost(Real.PositiveInfinity, Nil), // D = 0, no segments -> never used
                SegCost(0.0, List(0)))), // D = 1, one segment, cost is 0.0, as per Eq. 14
        1,
        CostMatrix.firstColumn(nObs, kerEval))
        
    val res = p04various.Iterate.iterate(
        initialAccumulator,
        updateAccumulator(
            _: Accumulator,
            kerEval,
            DMax),
        (acc: Accumulator) => acc.tauP == nObs) // stop if the next observation would be outside the data. The last column corresponds to the case where the complete set of observations has been taken into account.
    
    return res
  }
  
  def updateAccumulator(
      acc: Accumulator,
      kerEval: (Index, Index) => Real,
      DMax: Index)
  : Accumulator = {
    val nextCol = CostMatrix.nextColumn(acc.currCol, kerEval)
    val temporaryAcc = Accumulator(acc.L, acc.tauP, nextCol) // update the column and create a new accumulator with it
    return Accumulator(
        acc.L :+ loopOverD(temporaryAcc, DMax), // new column is stored as the last element of the updated Vector
        acc.tauP + 1,
        nextCol) // add result as the last element of the accumulator
  }
  
  /**
   * Recursively compute the optimal segmentation for all allowed values of D, given $\tau'$ (the index of the last element of the subdomain considered), and the computed values from the previous subdomains.
   * The results will be stored in an updated Accumulator, in the $\tau'$ + 1  element.
   */
  def loopOverD(acc: Accumulator, DMax: Index): Array[SegCost] = {
    val maxD = math.min(acc.tauP + 1, DMax) // acc.tauP is the index of the observation that is going to be included in the current computation. The max number of segment is the number of obervations. Hence the +1 because indices are 0 based.
    Array.tabulate(maxD + 1)(D => { // tabulate over the interval [0, maxD]
      	D match {
        	case 0 => SegCost(Real.PositiveInfinity, Nil) // this will never be used, hence the Real.PositiveInfinity to ensure it is never selected as an optimal solution, this is similar to what is found in the initialAccumulator
        	case 1 => SegCost(acc.currCol.c(0), List(0)) // one segment containing all the observations, directly compute the cost of the subsegment, using ColumnCostMatrix, for $\tau$ = 0
        	case _ => loopOverTau(acc, D) // recursive computation, using dynamical programming
    	  }})
  }
  
  /**
   * Compute the candidate costs for a range of starting points for a new segment, and return the segCost object describing the best segmentation containing D segments and finishing at $\tau'$ (included).
   * tau corresponds to the index of the first element of the new segment.
   */
  def loopOverTau(acc: Accumulator, D: Index): SegCost = {
    val tauFirst = D - 1 // First value of tau for which a candidate cost is computed. This corresponds to the case where all previous segments have one observation each, and the candidate segment contains all the remaining observations. -1 because observations indices are 0-based.
    val candidateCosts = DenseVector.tabulate(acc.tauP - tauFirst + 1)(i => { // the last candidate is t = tauP, which corresponds to the case where last segment only contains the tauP observation
      val tau = i + tauFirst // translation from local index to observation index
      candidateCost(
    		  acc,
    		  tau,
    		  D)
    })
    
    val minLocalIndex = argmin(candidateCosts)
    val minCost = candidateCosts(minLocalIndex)
    val minGlobalIndex = minLocalIndex + tauFirst
    
    return SegCost(minCost, minGlobalIndex :: acc.L(minGlobalIndex)(D - 1).seg)
  }
  
  /**
   * Cost of the segmentation with D segments of a sub-collection of the first $\tau'$ observations of a n long collection.
   * Use the knowledge of the D - 1 optimal segmentations and the cost matrix to compute it.
   * 
   * @param acc The accumulator containing the previously computed values, among which the quantities corresponding to the D - 1 segments will be used.
   * @param tau Index of the first element of the new segment that is introduced.
   * @param D number of segments in the resulting segmentation
   * @param ccm Cost matrix used to compute the cost of the new segment.
   */
  def candidateCost(
      acc: Accumulator,
      tau: Index,
      D: Index)
  : Real = {
    acc.L(tau)(D - 1).cost + acc.currCol.c(tau) // D - 1 not because index is 0-based, but because of the formula
  }
  
  def printAccumulator(acc: Accumulator, accumulatorName: String) {
    (0 to acc.L.size - 1).foreach(tauP => {
      (0 to acc.L(tauP).size - 1).foreach(D => {
          val cost = acc.L(tauP)(D).cost
          val partition = acc.L(tauP)(D).seg.mkString(", ")
          println(s"tauP: $tauP, D: $D, s.cost: $cost, partition: $partition")
        }) // complete output should be for tauP between 0 and nPoints - 1
    })
  }
  
  def bestPartition(acc: Accumulator): SegCost = acc
    .L
    .last
    .sortBy(_.cost)
    .head

    
  def printSegCost(s: SegCost) = {
    val cost = s.cost
    val partition = s.seg.mkString(", ")
    println(s"s.cost: $cost, partition: $partition")
  }
}
