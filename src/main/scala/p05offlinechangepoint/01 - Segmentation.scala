package p05offlinechangepoint

import breeze.linalg.{argmin, DenseVector} // not imported to avoid masking scala Vector with Breeze Vector
import p04various.TypeDef._

object Segmentation {
  /**
   * Cost associated with a particular segmentation. Note that the segmentation is kept as a List which has to be reversed to be exploited.
   * */
  case class SegCost(val cost: Real, val seg: List[Index])
  
  /**
   * Accumulator used in Algorithm 3. The access pattern can be represented as the function segCost($\tau'$)(D) where $\tau'$ is the index of the first element AFTER the subsegment considered.
   * This follows the convention used in the article where the right bounds are open. The subsegment considered is therefore [0, $\tau'$[. Inside this storage are computed all the costs.
   * and corresponding segmentations for sub-collections containing observations from 0 to $\tau'$ - 1 (included).
   * Note that the 0 element of the Vector will never be accessed, as it corresponds to an empty sub-collection.
   * Note also that the article uses a 1-based indexing, while this implementation uses a 0-based indexing which is more natural.
   */
  case class Accumulator(val L: Vector[Array[SegCost]], val currCol: CostMatrix.ColumnCostMatrix)
  
  /**
   * Outer loop described in Algorithm 3, implemented using an iterate function.
   */
  def loopOverTauP(
      nObs: Index,
      kerEval: (Index, Index) => Real,
      DMax: Index): Accumulator = {
    println("loopOverTauP")
    val initialAccumulator = Accumulator(
        Vector(Array[SegCost](), Array(SegCost(0.0, List(0)))), // The element 0 will never be accessed and is empty. The element 1 contains only cost of the segment consisting of the first element Which is 0
        CostMatrix.firstColumn(nObs, kerEval))
    
    println(s"initialAccumulator.L.size, " + initialAccumulator.L.size)
        
    val res = p04various.Iterate.iterate(
        initialAccumulator,
        updateAccumulator(
            _: Accumulator,
            kerEval,
            DMax),
        (acc: Accumulator) => acc.L.size == nObs + 1) // A L with nObs + 1 elements covers the totality of observations
    
    return initialAccumulator
  }
  
  def updateAccumulator(
      acc: Accumulator,
      kerEval: (Index, Index) => Real,
      DMax: Index)
  : Accumulator = {
    println("updateAccumulator, size of Accumulator: " + acc.L.size)
    val nextCol = CostMatrix.nextColumn(acc.currCol, kerEval)
    val temporaryAcc = Accumulator(acc.L, nextCol) // update the column and create a new accumulator with it
    return Accumulator(acc.L.updated(acc.L.size, loopOverD(temporaryAcc, DMax)), nextCol) // add result as the last element of the accumulator
  }
  
  /**
   * Recursively compute the optimal segmentation for all allowed values of D, given $\tau'$ (the index of the last element of the subdomain considered), and the computed values from the previous subdomains.
   * The results will be stored in an updated Accumulator, in the $\tau'$ + 1  element.
   */
  def loopOverD(acc: Accumulator, DMax: Index): Array[SegCost] = {
    println("loopOverD")
    val tauP = acc.L.size // Index of the last observation in the subdomain considered.
    val minD = 1
    val maxD = math.min(tauP + 1, DMax) // + 1 because observations are 0-based, while the number of segments is 1-based, so to speak.
    Array.tabulate(maxD - minD + 1)(D => {
      	println(s"loopOverD, D: $D")
      	D match {
        	case 0 => SegCost(Real.PositiveInfinity, Nil) // this will never be used, hence the Real.PositiveInfinity to ensure it is never selected as an optimal solution
        	case 1 => SegCost(acc.currCol.c(0), List(0)) // one segment containing all the observations, directly compute the cost of the subsegment, using ColumnCostMatrix, for $\tau$ = 0
        	case _ => loopOverTau(acc, D) // recursive computation, using dynamical programming
    	  }})
  }
  
  /**
   * Compute the candidate costs for a range of starting points, and return the segCost object describing the best segmentation containing D segments and finishing at $\tau'$.
   * This is used to fill the $\tau'$ + 1 element of the Accumulator.
   */
  def loopOverTau(acc: Accumulator, D: Index): SegCost = {
    println("loopOverTau")
    val tauP = acc.L.size // Index of the last observation in the subdomain considered.
    val tauFirst = D - 1 // First value of tau for which a candidate cost is computed. This corresponds to the case where all previous segments have one observation each, and the candidate segment contains all the remaining observations.
    val candidateCosts = DenseVector.tabulate(tauP - tauFirst + 1)(i => { // the last candidate is t = tauP, which corresponds to the case where last segment only contains the tauP observation
      val tau = i + tauFirst // translation from local index to observation index
      println(s"loopOverTau, tau: $tau")
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
    println("candidateCost, acc.L.size: " + acc.L.size + ", acc.L(tau).size: " + acc.L(tau).size)
    acc.L(tau)(D - 1).cost + acc.currCol.c(tau) // D - 1 not because index is 0-based, but because of the formula
  }
  
  def printAccumulator(acc: Accumulator) {
    (0 to acc.L.size - 1).foreach(tauP => {
      (0 to acc.L(tauP).size - 1).foreach(s => {
          val cost = acc.L(tauP)(s).cost
          val partition = acc.L(tauP)(s).seg.mkString(", ")
          println(s"tauP: $tauP, s: $s, s.cost: $cost, partition: $partition")
        }) // complete output should be for tauP between 0 and nPoints - 1
    })
  }
}
