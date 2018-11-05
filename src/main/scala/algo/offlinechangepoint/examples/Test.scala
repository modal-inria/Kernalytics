package algo.offlinechangepoint.examples

import breeze.linalg._
import rkhs.{ Algebra, KerEval, Kernel }
import various.TypeDef._
import scala.reflect.ClassTag
import algo.offlinechangepoint.NumberSegmentSelection
import algo.offlinechangepoint.Segmentation

/**
 * Generic functions for easy testing of kernels.
 *
 * @param dataGenerator one function per segment that
 */
object Test {
  def generateData[A: ClassTag](sampleLaws: Array[() => A], nPoints: Index, segPoints: Array[Index]): DenseVector[A] = DenseVector.tabulate[A](nPoints)(i => {
    val seg = (segPoints.size - 1 to 0 by -1).find(segPoints(_) <= i).get
    sampleLaws(seg)()
  })

  /**
   * Take laws and segments description as arguments. Generate the data, perform the segmentation and export the best segmentation.
   */
  def segment(kerEval: (Index, Index) => Real, dMax: Index, nPoints: Index, visualOutput: Option[String]): Array[Index] = { // TODO: understand why ClassTag is needed
    val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
    //    Segmentation.printAllPartitions(res)

    val bestD = NumberSegmentSelection.optimalNumberSegments(res, nPoints)

    return bestD.segPoints
  }
}