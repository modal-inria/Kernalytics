import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._

import p00rkhs.{Algebra, KerEval, Kernel}
import p04various.TypeDef._
import p05offlinechangepoint.{CostMatrix, Test}

/**
 * A mix of various unit tests put here until a better place is found.
 */
class VariousSpec extends FlatSpec with Matchers {
//	"nextColumn" should "compute the cost matrix next column" in {
//		val nPoints = 10
//		val kernelSD = 1.0
//		val interPoint = DenseVector[Real](0.0, 2.5, 5.0, 7.5, 10.0)
//
//		val data = TestSegmentationNormal.expAndNormalData(nPoints, interPoint, baseDir)
//
//		val kernel =
//		  Kernel.InnerProduct.gaussian(
//				  _: Real,
//				  _: Real,
//				  Algebra.R.InnerProductSpace,
//				  kernelSD)
//
//		val kerEval = KerEval.generateKerEval(data, kernel, true)	
//
//		val costDirect = CostMatrix.completeCostMatrix(data, kernel)
//		println("Cost with direct computation")
//		println(costDirect)
//
//		val costIterate = CostMatrix.completeMatrixViaColumn(nPoints, kerEval)
//		println("Cost with iterated computation")
//		println(costIterate)
//
//		val maxRelativeError = max(abs(costIterate - costDirect))
//		println(s"maxRelativeError: $maxRelativeError")
//	}
}