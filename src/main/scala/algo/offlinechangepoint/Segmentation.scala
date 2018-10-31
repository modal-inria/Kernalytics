package algo.offlinechangepoint

import breeze.linalg.{ DenseVector }
import scala.annotation.tailrec
import various.TypeDef._
import offlinechangepoint.CostMatrix

/**
 * The index of the first observation is 0 and not 1 as in the article.
 */
object Segmentation {
  /**
   * Cost associated with a particular segmentation. Note that the segmentation is kept as a List which has to be reversed to be exploited.
   */
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
  class Accumulator(val L: Array[List[SegCost]], val tauP: Index, val tauPMax: Index, val currCol: CostMatrix.ColumnCostMatrix)

  /**
   * Outer loop in Algorithm 3, implemented using the recursive function iterate.
   */
  def loopOverTauP(nObs: Index, kerEval: (Index, Index) => Real, DMax: Index): Accumulator = {
    val initialAccumulator = new Accumulator(
      Array.tabulate[List[SegCost]](DMax + 1)(D => // size is DMax + 1 because a list is created for every D, including 0
        D match {
          case 0 => List(new SegCost(Real.PositiveInfinity, Nil), new SegCost(Real.PositiveInfinity, Nil)) // D = 0, every cost is infinite, and the list of segments is empty. Costs correspond to List([0, 0], []). Note that the head is always the LONGEST segment, as it is the last computed in the algorithm that follows.
          case 1 => List(new SegCost(0.0, List(0))) // D = 1, cost of the segment [0, 0]
          case _ => Nil // Since the bigger segment at the moment is [0, 0], it is impossible to have more than 1 segment
        }),
      0, // the max segment is [0, 0], hence with notation [0, tauP], tauP = 0
      nObs - 1,
      CostMatrix.firstColumn(nObs, kerEval)) // first column corresponds to the cost of the segment [0, 0], internaly has tauP = 0, and corresponds to the column tauP + 1 in the Cost matrix

    various.Iterate.iterate(
      initialAccumulator,
      updateAccumulator(
        _: Accumulator,
        kerEval,
        DMax),
      (acc: Accumulator) => {
//        if (acc.tauP % (acc.tauPMax / 10) == 0) { // print current progress, every 10 %
//          println(s"Computation progress: ${acc.tauP.toReal / acc.tauPMax.toReal * 100.0} %")
//        }
        acc.tauP == acc.tauPMax // stop when the last element of L contains the computation for the complete segment
      })
  }

  /**
   * Computation of the best new segmentation is performed in one pass. The sum of the D - 1 best segmentation for a given [0, tau - 1] and the cost [tau, tauP] is computed for current tau
   * and immediately compared to the best combination so far. The accumulator thus always contains the best new segmentation so far for a given (D, tauP).
   */

  def updateAccumulator(acc: Accumulator, kerEval: (Index, Index) => Real, DMax: Index): Accumulator = {
    val nextCol = CostMatrix.nextColumn(acc.currCol, kerEval)
    val temporaryAcc = new Accumulator(acc.L, acc.tauP, acc.tauPMax, nextCol) // update the column and create a new "temporary" accumulator that will be used in loopOverD
    val optimalSegCost = loopOverD(temporaryAcc, DMax) // Array with a List for each value of D in [0, DMax]
    val newL = (optimalSegCost, acc.L).zipped.map(_ ++ _)

    new Accumulator(
      newL, // new column is stored as the last element of the updated Vector
      acc.tauP + 1,
      acc.tauPMax,
      nextCol) // add result as the last element of the accumulator
  }

  /**
   * Recursively compute the optimal segmentation for all allowed values of D, given $\tau'$ (the index of the last element of the subdomain considered), and the computed values from the previous subdomains.
   * The results will be stored in an updated Accumulator, in the $\tau'$ + 1  element.
   * For values of D that should yield no result, for example when D = 4 and the segment to compute the new segmentation is [0, 2], the corresponding element is an empty List.
   *
   * @param acc Accumulator in which the currCol element corresponds to the segment [0, tauP + 1] instead of [0, tauP] as expected in the definition of Accumulator
   */
  def loopOverD(acc: Accumulator, DMax: Index): Array[List[SegCost]] = {
    val tauP = acc.tauP + 1 // current $\tau'$, which corresponds to the loop "for $\tau'$ = 2 to n do", in the article. The best segmentations are being computed for the segment [0, tauP]
    val DMaxBounded = math.min(tauP + 1, DMax) // The max number of segments is the number of observations. Hence the +1 because [0, tauP] contains tauP + 1 elements.

    Array.tabulate(DMax + 1)(D => D match { // tabulate over the interval [0, DMaxBounded] which contains maxD + 1 elements
      case 0 => List(new SegCost(Real.PositiveInfinity, Nil)) // this will never be used, hence the Real.PositiveInfinity to ensure it is never selected as an optimal solution, this is similar to what is found in the initialAccumulator
      case 1 => List(new SegCost(acc.currCol.c(0), List(0))) // one segment containing all the observations, directly compute the cost of the segment [0, tauP], using ColumnCostMatrix, for tau (the first element in the segment) = 0
      case _ if (D <= DMaxBounded) => List(loopOverTau(acc.L(D - 1), acc.currCol.c, tauP, D)) // recursive computation, using dynamic programming
      case _ => Nil
    })
  }

  /**
   * Compute the candidate costs for a range of starting points for a new segment, and return the segCost object describing the best segmentation containing D segments and finishing at $\tau'$ (included).
   * tau corresponds to the index of the first element of the new segment.
   *
   * @param dM1Val values for D - 1 element partition, for the segment [0, tauP - 1]
   * @param tauP index of the end of the segment currently computed [0, tauP]
   */
  def loopOverTau(dM1Val: List[SegCost], currCol: DenseVector[Real], tauP: Index, D: Index): SegCost = {
    val head :: tail = dM1Val
    val initalSegCost = new SegCost(head.cost + currCol(tauP), tauP :: head.seg) // SegCost for the case where the only element in the new segment is tauP
    recurLoopOverTau(tail, currCol, tauP - 1, initalSegCost)
  }

  /**
   * Recursive computation of the best SegCost, knowing the currently best SegCost.
   *
   * @param ls list of the remaining SegCost, corresponding to the D - 1 line in Accumulator.L. For example, ls.head corresponds to the best D - 1 partition of the segment [0, tau]
   * @param currInd index of the starting point just right after the head element of ls. In the example, tau + 1
   * @param bestCost best SegCost for the segments [0, tauP] for tau <= tauP
   */
  @tailrec
  def recurLoopOverTau(ls: List[SegCost], currCol: DenseVector[Real], currInd: Index, bestCost: SegCost): SegCost = ls match {
    case Nil => bestCost
    case head :: tail => {
      val newCost = head.cost + currCol(currInd)
      val newBestCost = if (bestCost.cost < newCost) bestCost else new SegCost(newCost, currInd :: head.seg)
      recurLoopOverTau(tail, currCol, currInd - 1, newBestCost)
    }
  }

  def printAllPartitions(acc: Accumulator) =
    acc
      .L
      .map(_.head)
      .foreach(printSegCost)

  def printSegCost(s: SegCost) = {
    val cost = s.cost
    val partition = s.seg.reverse.mkString(", ")
    println(s"s.cost: $cost, partition: $partition")
  }
}
