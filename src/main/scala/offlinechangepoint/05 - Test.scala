package offlinechangepoint

import breeze.linalg._
import rkhs.{Algebra, KerEval, Kernel}
import various.TypeDef._
import scala.reflect.ClassTag

/**
 * Generic functions for easy testing of kernels.
 * 
 * @param dataGenerator one function per segment that 
 */
object Test {
  def generateData[A: ClassTag](
      sampleLaws: Array[() => A],
      nPoints: Index,
      segPoints: Array[Index])
  : DenseVector[A] = 
    DenseVector.tabulate[A](nPoints)(i => {
    	  val seg = (segPoints.size - 1 to 0 by -1).find(segPoints(_) <= i).get
    	  sampleLaws(seg)()
    })
  
  /**
   * Take laws and segments description as arguments. Generate the data, perform the segmentation and export the best segmentation.
   */
  def segment[A: ClassTag]( // TODO: understand why ClassTag is needed
      kerEval: (Index, Index) => Real,
      dMax: Index,
      nPoints: Index,
      visualOutput: Boolean,
      baseDir: String)
  : Array[Index] = {    
		val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
//		Segmentation.printAccumulator(res, "res")

		val bestPartition = Segmentation.bestPartition(res)
//		Segmentation.printSegCost(bestPartition)
		
		val costs = res.L.last.map(_.cost)
    val bestD = NumberSegmentSelection.optimalNumberSegments(costs, nPoints, visualOutput, baseDir)
    
    return res
    .L
    .last(bestD)
    .seg
    .reverse
    .toArray
  }
}