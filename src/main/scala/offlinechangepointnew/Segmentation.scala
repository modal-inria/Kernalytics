package offlinechangepoint

import breeze.linalg.{argmin, DenseVector} // not imported using _ to avoid masking Scala Vector with Breeze Vector
import various.TypeDef._
import rkhs.Gram
import offlinechangepointlegacy.CostMatrix

/**
 * The index of the first observation is 0 and not 1 as in the article.
 */
object Segmentation {  
	/**
	 * Cost associated with a particular segmentation. Note that the segmentation is kept as a List which has to be reversed to be exploited.
	 * */
	class SegCost(val cost: Real, val seg: List[Index])

	/**
	 * Accumulator used in Algorithm 3.
	 * The indices in L correspond to $L_{D, \tau' + 1}$. With tau' the last element included in the segment. For example, the column 5 corresponds to the segments [0, 4] (and the column 0 to empty segments []).
	 * The column index thus corresponds to the first elements to the right of the segment.
	 * Last element corresponds to $L_{D, \tau' + 1}$, which stores the computation for the sub-segment [0, tau'].
	 * Hence there are $\tau' + 2$ elements in L. The element 0 corresponds to an empty segment (tau' + 1 = 0), the element 1 (tau' + 1 = 1) to the segment [0, 0], and so on.
	 * 
	 * @param L storage of all the SegCost.
	 * @param tauP index of the last observation included in the last element of L (same notation as $L_{D, \tau' + 1}$ in the article).
	 * @param currCol column of the cost matrix
	 */
	class Accumulator(val L: Array[List[SegCost]], val tauP: Index, val currCol: CostMatrix.ColumnCostMatrix)

	/**
	 * Outer loop in Algorithm 3, implemented using the recursive function iterate.
	 */
	def loopOverTauP(nObs: Index, kerEval: (Index, Index) => Real, DMax: Index): Accumulator = {
			val initialAccumulator = new Accumulator(
					Array.tabulate[List[SegCost]](DMax + 1)(D => D match {
    					case 0 => List(new SegCost(Real.PositiveInfinity, Nil), new SegCost(Real.PositiveInfinity, Nil)) // D = 0, every cost is infinite, and the list of segments is empty. Costs correspond to List([0, 0], []). Note that the head is always the LONGEST segment, as it is the last computed in the algorithm that follows.
    					case 1 => List(new SegCost(0.0, List(0))) // D = 1, cost of the segment [0, 0]
    					case _ => Nil // Since the bigger segment at the moment is [0, 0], it is impossible to have more than 1 segment
					}),  
					0, // the max segment is [0, 0], hence with notation [0, tauP], tauP = 0
					CostMatrix.firstColumn(nObs, kerEval)) // first column corresponds to the cost of the segment [0, 0], internaly has tauP = 0, and corresponds to the column tauP + 1 in the Cost matrix

			various.Iterate.iterate(
					initialAccumulator,
					updateAccumulator(
							_: Accumulator,
							kerEval,
							DMax),
					(acc: Accumulator) => acc.tauP == nObs - 1) // stop when the last element of L contains the computation for the complete segment
	}

	/**
	 * Computation of the best new segmentation is performed in one pass. The sum of the D - 1 best segmentation for a given [0, tau - 1] and the cost [tau, tauP] is computed for current tau
	 * and immediately compared to the best combination so far. The accumulator thus always contains the best new segmentation so far for a given (D, tauP).
	 */

	def updateAccumulator(acc: Accumulator, kerEval: (Index, Index) => Real, DMax: Index): Accumulator = {
			val nextCol = CostMatrix.nextColumn(acc.currCol, kerEval)
			val temporaryAcc = new Accumulator(acc.L, acc.tauP, nextCol) // update the column and create a new "temporary" accumulator that will be used in loopOverD
			val optimalSegCost = loopOverD(temporaryAcc, DMax) // Array with a List for each value of D in [0, DMax]
			val newL = (optimalSegCost, acc.L).zipped.map(_ ++ _)
			
			new Accumulator(
					newL, // new column is stored as the last element of the updated Vector
					acc.tauP + 1,
					nextCol) // add result as the last element of the accumulator
	}
	  
	  /**
	   * Recursively compute the optimal segmentation for all allowed values of D, given $\tau'$ (the index of the last element of the subdomain considered), and the computed values from the previous subdomains.
	   * The results will be stored in an updated Accumulator, in the $\tau'$ + 1  element.
	   * For values of D that should yield no result, for example when D = 4 and the segment to compute the new segmentation is [0, 2], the corresponding element is an empty List.
	   */
	def loopOverD(acc: Accumulator, DMax: Index): Array[List[SegCost]] = {
			val tauP = acc.tauP + 1 // current $\tau'$, which corresponds to the loop "for $\tau'$ = 2 to n do", in the article. The best segmentations are being computed for the segment [0, tauP]
			val DMaxBounded = math.min(tauP + 1, DMax) // The max number of segments is the number of observations. Hence the +1 because [0, tauP] contains tauP + 1 elements.

			Array.tabulate(DMax + 1)(D => D match { // tabulate over the interval [0, DMaxBounded] which contains maxD + 1 elements
    			case 0 => List(new SegCost(Real.PositiveInfinity, Nil)) // this will never be used, hence the Real.PositiveInfinity to ensure it is never selected as an optimal solution, this is similar to what is found in the initialAccumulator
    			case 1 => List(new SegCost(acc.currCol.c(0), List(0))) // one segment containing all the observations, directly compute the cost of the segment [0, tauP], using ColumnCostMatrix, for tau (the first element in the segment) = 0
    			case _ if (D <= DMaxBounded) => List(loopOverTau(acc, tauP, D)) // recursive computation, using dynamical programming
    			case _ => Nil
			})
	}
	  
	  /**
	   * Compute the candidate costs for a range of starting points for a new segment, and return the segCost object describing the best segmentation containing D segments and finishing at $\tau'$ (included).
	   * tau corresponds to the index of the first element of the new segment.
	   */
	  def loopOverTau(acc: Accumulator, tauP: Index, D: Index): SegCost = {
//	    val tauFirst = D - 1 // First value of tau for which a candidate cost is computed. This corresponds to the case where all previous segments have one observation each, and the candidate segment contains all the remaining observations. -1 because observations indices are 0-based.
//	    val candidateCosts = DenseVector.tabulate(tauP - tauFirst + 1)(i => { // the last candidate is tau = tauP, which corresponds to the case where last segment only contains the tauP observation
//	      val tau = i + tauFirst // translation from local index to observation index
//	      candidateCost(
//	    		  acc,
//	    		  tau,
//	    		  D)
//	    })
//	    
//	    val minLocalIndex = argmin(candidateCosts)
//	    val minCost = candidateCosts(minLocalIndex)
//	    val minGlobalIndex = minLocalIndex + tauFirst
//	    
//	    return SegCost(minCost, minGlobalIndex :: acc.L(minGlobalIndex)(D - 1).seg) // append minGlobalIndex to the best segmentation with D - 1 segments
	    
	    ???
	  }
	//  
	//  /**
	//   * Cost of the segmentation with D segments of a sub-collection of the first $\tau'$ observations of a n long collection.
	//   * Use the knowledge of the D - 1 optimal segmentations and the cost matrix to compute it.
	//   * Note that both acc.L and acc.currCol.c are accessed at the tau element. This confirms that the tau element in acc.L corresponds to the [0, tau - 1] segment.
	//   * 
	//   * @param acc The accumulator containing the previously computed values, among which the quantities corresponding to the D - 1 segments will be used.
	//   * @param tau Index of the first element of the new segment that is introduced.
	//   * @param D number of segments in the resulting segmentation
	//   * @param ccm Cost matrix used to compute the cost of the new segment.
	//   */
	//  def candidateCost(
	//      acc: Accumulator,
	//      tau: Index,
	//      D: Index)
	//  : Real = {
	//    acc.L(tau)(D - 1).cost + acc.currCol.c(tau) // D - 1 because the best segmentation with one less segment is considered (not because index is 0-based)
	//  }
	//  
	//  def printAccumulator(acc: Accumulator, accumulatorName: String) {
	//    (0 to acc.L.size - 1).foreach(tauP => {
	//      (0 to acc.L(tauP).size - 1).foreach(D => {
	//          val cost = acc.L(tauP)(D).cost
	//          val partition = acc.L(tauP)(D).seg.reverse.mkString(", ")
	//          println(s"tauP: $tauP, D: $D, s.cost: $cost, partition: $partition")
	//        }) // complete output should be for tauP between 0 and nPoints - 1
	//    })
	//  }
	//  
	//  def bestPartition(acc: Accumulator): SegCost = acc
	//    .L
	//    .last
	//    .sortBy(_.cost)
	//    .head
	//
	//  def printSegCost(s: SegCost) = {
	//    val cost = s.cost
	//    val partition = s.seg.reverse.mkString(", ")
	//    println(s"s.cost: $cost, partition: $partition")
	//  }
}
