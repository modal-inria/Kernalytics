package algo.offlinechangepoint

import breeze.linalg.{ linspace, DenseVector, DenseMatrix }
import scala.reflect._
import scala.util.{ Try, Success, Failure }
import various.TypeDef._

/**
 * The index of the first observation is 0 and not 1 as in the article, because arrays in Scala are 0-based.
 *
 * This implementation uses mutable states and loops, to be more readable and improve performances.
 */
object Segmentation {
  /**
   * Cost associated with a particular segmentation. Note that the segmentation is kept as a List which has to be reversed to be exploited.
   *
   * @param cost the cost the best partition which end at the current point
   * @param seg the starting point of each segment in the partition, reversed, because it is faster to prepend to a list
   */
  class SegCost(val cost: Real, val seg: List[Index])

  /**
   * Cost of the ignoredCost segmentation is so high that any other non degenerate alternative will be chosen instead.
   */
  val ignoredCost = new SegCost(Real.PositiveInfinity, Nil)

  def inputCheck(nObs: Index, DMax: Index): Try[Unit] =
    if (DMax <= nObs) {
      Success()
    } else {
      Failure(new Exception(s"Number of observations ($nObs) must be inferior or equal to DMax ($DMax)"))
    }

  /**
   * Loops as presented in Algorithm 3.
   * 
   * @return A matrix (D, tauP) -> The segcost corresponding to the best segmentation for D segments, over the [0, tauP] segment 
   */
  def loops(nObs: Index, kerEval: (Index, Index) => Real, DMax: Index, printProgress: Boolean): DenseMatrix[SegCost] = {
    if (printProgress) {
      println("Offline change point computation starts...")
      println("Progress is expressed in terms of tauP, not in terms of remaining time. Each iteration takes longer than the preceding one.")
    }
    
    val percents = (linspace(0.0, 1.0, 11) * 100.0).toArray.map(math.round(_))
    val steps = (linspace(0.0, 1.0, 11) * (nObs - 1).toReal).toArray.map(math.round(_))
    val all = steps.zip(percents)
    var nextStep = 1

    val L = DenseMatrix.fill[SegCost](DMax + 1, nObs)(ignoredCost) // same notation as in article, DenseMatrix is a mutable array hence the val

    L(1, 0) = new SegCost(0.0, List(0)) // initialization of first column, D = 1, one element partition, cost of the segment [0, 0]

    var C = CostMatrix.firstColumn(nObs, kerEval) // i-th coefficient contains the cost of the segment [i, tauP], initialized with the costs [i, 0], for which the only relevant element is the first, set at 0

    for (tauP <- 1 to nObs - 1) { // in current iteration, total cost of segment [0, tauP] are computed for various values of D, using partitions computed no [0, tauP - 1]
      C = CostMatrix.nextColumn(C, kerEval) // C is updated to contains the costs [i, tauP] for current tauP

      L(0, tauP) = ignoredCost // this will never be used, hence the Real.PositiveInfinity to ensure it is never selected as an optimal solution, this is similar to what is found in the initialAccumulator
      L(1, tauP) = new SegCost(C.c(0), List(0)) // one segment containing all the observations, directly compute the cost of the segment [0, tauP], using ColumnCostMatrix, for tau (the first element in the segment) = 0

      val maxD = math.min(DMax, tauP + 1) // maxD = tauP + 1 when there is one timestep per part

      for (D <- 2 to maxD) { // in the segment [0, tauP], there can not be more than tauP + 1 segments
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

      if (printProgress && tauP == all(nextStep)._1) {
        println(s"Progress: ${all(nextStep)._2}%")
        nextStep += 1
      }
    }

    return L
  }

  /**
   * Take laws and segments description as arguments. Generate the data, perform the segmentation and export the best segmentation.
   */
  def segment(kerEval: (Index, Index) => Real, dMax: Index, nPoints: Index, visualOutput: Option[String]): Array[Index] = { // TODO: understand why ClassTag is needed
    val res = loops(nPoints, kerEval, dMax, false)
    //    Segmentation.printAllPartitions(res)

    val penCost = NumberSegmentSelection.penalizedCostComputation(res, nPoints)

    return NumberSegmentSelection.bestSegment(penCost)
  }

  def generateData[A: ClassTag](sampleLaws: Array[() => A], nPoints: Index, segPoints: Array[Index]): DenseVector[A] = DenseVector.tabulate[A](nPoints)(i => {
    val seg = (segPoints.size - 1 to 0 by -1).find(segPoints(_) <= i).get
    sampleLaws(seg)()
  })

}
