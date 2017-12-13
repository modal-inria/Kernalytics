package p05offlinechangepoint.tests

import breeze.linalg._
import breeze.numerics._
import breeze.plot._
import p00rkhs.{Algebra, KerEval, Kernel}
import p04various.TypeDef._
import p05offlinechangepoint.{Segmentation, NumberSegmentSelection}

/**
 * More advanced segmentation test. The signal here is composed of observations of matrices.
 */
object TestSegmentationMatrix {    
  val baseDir = "data/p05offlinechangepoint/tests/02 - TestSegmentationMatrix"
  
  def matData(
      nPoints: Index,
      interPoint: DenseVector[Index],
      nPoint: Index,
      nRow: Index,
      nCol: Index)
  : DenseVector[DenseMatrix[Real]] = {
    val muLaw = breeze.stats.distributions.Uniform(-10.0, 10.0)
    val sdLaw = breeze.stats.distributions.Uniform(  0.1,  1.0)
    
    def matLaw = DenseMatrix.tabulate[breeze.stats.distributions.Gaussian](nRow, nCol)((i, j) => breeze.stats.distributions.Gaussian(muLaw.sample, sdLaw.sample))

    val laws = Array.fill(interPoint.size)(matLaw)
    
    val data =
      DenseVector.tabulate(nPoints)(i => {
        val seg = (interPoint.size - 1 to 0 by -1).find(interPoint(_) <= i).get
        laws(seg).map(_.sample)
      })
    
    return data
  }
  
  def main {
	  val nPoints = 1000
		val kernelSD = 0.5
		val dMax = 8
		val interPoint = DenseVector[Index](0, 250, 500, 750)
		val nRow = 3
		val nCol = 3

		val data = matData(nPoints, interPoint, nPoints, nRow, nCol)
		
		val kernel =
		  Kernel.Metric.gaussian(
		    _: DenseMatrix[Real],
		    _: DenseMatrix[Real],
		    Algebra.DenseMatrixReal.MetricSpace,
		    kernelSD)
    
		val kerEval = KerEval.generateKerEval(data, kernel, false)

		val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
		Segmentation.printAccumulator(res, "res")

		val bestPartition = Segmentation.bestPartition(res)
		Segmentation.printSegCost(bestPartition)
		
		val costs = res.L.last.map(_.cost)
    val bestD = NumberSegmentSelection.optimalNumberSegments(costs, nPoints, true, baseDir)
    println(s"Optimal number of segments: $bestD")
  }
}