package p05offlinechangepoint.tests

import breeze.linalg.{max, DenseVector}
import breeze.numerics._
import breeze.plot._
import p00rkhs.{Algebra, KerEval, Kernel}
import p04various.TypeDef._
import p05offlinechangepoint.CostMatrix

/**
 * Simple data generation for the first tests of the algorithm.
 */
object TestCostMatrix {
  val baseDir = "data/p05offlinechangepoint/tests/01 - TestCostMatrix"
  
  def main {
    val nPoints = 10
    val kernelSD = 1.0
		val interPoint = DenseVector[Real](0.0, 2.5, 5.0, 7.5, 10.0)

		val data = TestSegmentationNormal.expAndNormalData(nPoints, interPoint, baseDir)
		
		val kernel =
		  Kernel.InnerProduct.gaussian(
		    _: Real,
		    _: Real,
		    Algebra.R.InnerProductSpace,
		    kernelSD)
		
		val kerEval = KerEval.generateKerEval(data, kernel, true)	

		val costDirect = CostMatrix.completeCostMatrix(data, kernel)
		println("Cost with direct computation")
		println(costDirect)
		
		val costIterate = CostMatrix.completeMatrixViaColumn(nPoints, kerEval)
		println("Cost with iterated computation")
		println(costIterate)
		
		val maxRelativeError = max(abs(costIterate - costDirect))
		println(s"maxRelativeError: $maxRelativeError")
  }
}