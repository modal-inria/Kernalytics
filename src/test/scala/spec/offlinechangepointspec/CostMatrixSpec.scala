package spec.offlinechangepointspec

import algo.offlinechangepoint.{CostMatrix, Segmentation}
import breeze.linalg._
import breeze.numerics._
import org.scalatest._
import rkhs.{Algebra, KerEval, Kernel}
import various.TypeDef._

/**
 * A mix of various unit tests put here until a better place is found.
 */
class CostMatrixSpec extends FlatSpec with Matchers {
  "nextColumn" should "compute the cost matrix next column" in {
    val nPoints = 12
    val kernelSD = 1.0
    val segPoints = Array(0, 3, 6, 9)

    val sampleLawsStochastic = {
      val lawA = breeze.stats.distributions.Gaussian(10.0, 0.1)
      val lawB = breeze.stats.distributions.Gaussian(10.0, 1.0)

      Array[() => Real](
        () => lawA.sample,
        () => lawB.sample,
        () => lawA.sample,
        () => lawB.sample)
    }

    val data = Segmentation.generateData(sampleLawsStochastic, nPoints, segPoints)

    val kernel =
      Kernel.InnerProduct.gaussian(
        _: Real,
        _: Real,
        Algebra.R.InnerProductSpace,
        kernelSD)

    val kerEval = KerEval.generateKerEvalFunc(data, kernel)

    val costDirect = CostMatrix.completeCostMatrix(data, kernel)
    //		println("Cost with direct computation")
    //		println(costDirect)

    val costIterate = CostMatrix.completeMatrixViaColumn(nPoints, kerEval)
    //		println("Cost with iterated computation")
    //		println(costIterate)

    val maxRelativeError = max(abs(costIterate - costDirect))

    maxRelativeError should ===(0.0 +- 1e-8)
    //		println(s"maxRelativeError: $maxRelativeError")
  }
}