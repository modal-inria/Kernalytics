package p05offlinechangepoint

import breeze.linalg.{argmin, DenseVector} // not imported using _ to avoid masking Scala Vector with Breeze Vector
import p04various.TypeDef._
import p00rkhs.Gram

/**
 * The index of the first observation is 0 and not 1 as in the article.
 */
object Segmentation {  
  /**
   * Cost associated with a particular segmentation. Note that the segmentation is kept as a List which has to be reversed to be exploited.
   * */
  case class SegCost(val cost: Real, val seg: List[Index])
  
  /**
   * Accumulator used in Algorithm 3.
   * 
   * @param L storage of all the SegCost. Last element corresponds to $L_{D, \tau' + 1}$, and stores the computation for the sub-segment [0, tau']. Hence there are $\tau' + 2$ elements in L. The element 0 corresponds to an empty segment, the element 1 to the segment [0, 0], and so on.
   * @param tauP index of the last observation included in the last element of L (same notation as $L_{D, \tau' + 1}$ in the article).
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
    val initialAccumulator = Accumulator( // contains $\tau' + 1 = 0$ and $\tau' + 1 = 1$ to initialize the computation
        Vector(
            Array[SegCost](SegCost(Real.PositiveInfinity, Nil)), // $\tau' + 1$ = 0. The element 0 will never be accessed and only contains the case D = 0. It corresponds to a sub-segment that contains nothing, and not segmented.
            Array( // $\tau' + 1 = 1$. The element 1 contains only the cost of the segment consisting of the first observation.
                SegCost(Real.PositiveInfinity, Nil), // D = 0, no segments -> never used
                SegCost(0.0, List(0)))), // D = 1, one segment, cost is 0.0, as per Eq. 14
        0,
        CostMatrix.firstColumn(nObs, kerEval))
        
    val res = p04various.Iterate.iterate(
        initialAccumulator,
        updateAccumulator(
            _: Accumulator,
            kerEval,
            DMax),
        (acc: Accumulator) => acc.tauP == nObs - 1) // stop when the last element of L contains the computation for the complete segment
    
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
    val tauP = acc.tauP + 1 // current $\tau'$, which corresponds to the loop "for $\tau'$ = 2 to n do", in the article. It is the index of the observation that is being included in current iteration.
    
    val maxD = math.min(tauP + 1, DMax) // The max number of segment is the number of observations. Hence the +1 because indices are 0 based.
    Array.tabulate(maxD + 1)(D => { // tabulate over the interval [0, maxD], the +1 is here for the right side inclusion
      	D match {
        	case 0 => SegCost(Real.PositiveInfinity, Nil) // this will never be used, hence the Real.PositiveInfinity to ensure it is never selected as an optimal solution, this is similar to what is found in the initialAccumulator
        	case 1 => SegCost(acc.currCol.c(0), List(0)) // one segment containing all the observations, directly compute the cost of the subsegment, using ColumnCostMatrix, for $\tau$ = 0
        	case _ => loopOverTau(acc, tauP, D) // recursive computation, using dynamical programming
    	  }})
  }
  
  /**
   * Compute the candidate costs for a range of starting points for a new segment, and return the segCost object describing the best segmentation containing D segments and finishing at $\tau'$ (included).
   * tau corresponds to the index of the first element of the new segment.
   */
  def loopOverTau(acc: Accumulator, tauP: Index, D: Index): SegCost = {
    val tauFirst = D - 1 // First value of tau for which a candidate cost is computed. This corresponds to the case where all previous segments have one observation each, and the candidate segment contains all the remaining observations. -1 because observations indices are 0-based.
    val candidateCosts = DenseVector.tabulate(tauP - tauFirst + 1)(i => { // the last candidate is tau = tauP, which corresponds to the case where last segment only contains the tauP observation
      val tau = i + tauFirst // translation from local index to observation index
      candidateCost(
    		  acc,
    		  tau,
    		  D)
    })
    
    val minLocalIndex = argmin(candidateCosts)
    val minCost = candidateCosts(minLocalIndex)
    val minGlobalIndex = minLocalIndex + tauFirst
    
    return SegCost(minCost, minGlobalIndex :: acc.L(minGlobalIndex)(D - 1).seg) // append minGlobalIndex to the best segmentation with D - 1 segments
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
    acc.L(tau)(D - 1).cost + acc.currCol.c(tau) // D - 1 because the best segmentation with one less segment is considered (not because index is 0-based)
  }
  
  def printAccumulator(acc: Accumulator, accumulatorName: String) {
    (0 to acc.L.size - 1).foreach(tauP => {
      (0 to acc.L(tauP).size - 1).foreach(D => {
          val cost = acc.L(tauP)(D).cost
          val partition = acc.L(tauP)(D).seg.reverse.mkString(", ")
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
    val partition = s.seg.reverse.mkString(", ")
    println(s"s.cost: $cost, partition: $partition")
  }
}
