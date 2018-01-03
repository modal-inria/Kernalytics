package p05offlinechangepoint

import breeze.linalg._
import p00rkhs.{Algebra, KerEval, Kernel}
import p04various.TypeDef._
import scala.reflect.ClassTag

/**
 * Generic functions for easy testing of kernels.
 * 
 * @param dataGenerator one function per segment that 
 */
object Test {
  /**
   * Take laws and segments description as arguments. Generate the data, perform the segmentation and export the best segmentation.
   */
  def segment[A: ClassTag]( // TODO: understand why ClassTag is needed
      sampleLaws: Array[() => A],
      kernel: (A, A) => Real,
      dMax: Index,
      nPoints: Index,
      segPoints: Array[Index])
  : Array[Index] = {
    val data =
      DenseVector.tabulate[A](nPoints)(i => {
        val seg = (segPoints.size - 1 to 0 by -1).find(segPoints(_) <= i).get
        sampleLaws(seg)()
      })
    
    val kerEval = KerEval.generateKerEval(data, kernel, false)
    
		val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
//		Segmentation.printAccumulator(res, "res")

		val bestPartition = Segmentation.bestPartition(res)
//		Segmentation.printSegCost(bestPartition)
		
		val costs = res.L.last.map(_.cost)
    val bestD = NumberSegmentSelection.optimalNumberSegments(costs, nPoints, false, "")
    
    return res
    .L
    .last(bestD)
    .seg
    .reverse
    .toArray
  }
}