package p05offlinechangepoint

import breeze.linalg.{csvwrite, linspace, max, DenseVector, DenseMatrix}
import breeze.numerics._
import breeze.plot._
import java.io.File
import p00rkhs.{Gram, Kernel}
import p04various.TypeDef._

/**
 * Simple data generation for the first tests of the algorithm.
 */
object TestCostMatrix {
  val baseDir = "data/p05offlinechangepoint/03-TestCostMatrix"
  
  def main {
    val nPoints = 10
    val kernelSD = 1.0
		val interPoint = DenseVector[Real](0.0, 2.5, 5.0, 7.5, 10.0)

		val data = TestNormalSignal.expAndNormalData(nPoints, interPoint, baseDir)
		
		def kerEval(i: Index, j: Index): Real = { // kerEval is defined here with a direct evaluation. An alternative would be to precompute the Gram matrix and then to access its elements. The function that generates a "cached" version of kerEval from a data set should be in p00rkhs
      Kernel.R.gaussian(data(i), data(j), kernelSD)
    }
		
		val costDirect = CostMatrix.completeCostMatrix(data, Kernel.R.gaussian(_: Real, _: Real, kernelSD))
		println("Cost with direct computation")
		println(costDirect)
		
		val costIterate = CostMatrix.completeMatrixViaColumn(nPoints, kerEval)
		println("Cost with iterated computation")
		println(costIterate)
		
		val maxRelativeError = max(abs(costIterate - costDirect))
		println(s"maxRelativeError: $maxRelativeError")
  }
}