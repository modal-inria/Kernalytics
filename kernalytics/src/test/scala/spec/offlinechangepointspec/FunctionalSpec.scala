package spec.offlinechangepointspec

import breeze.linalg._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._
import rkhs.{ KerEvalGenerator, KerEval }
import various.TypeDef._
import offlinechangepoint.{ Test }
import org.scalactic.source.Position.apply

/**
 * This class contains functional tests. This means that those tests check entire segmentations.
 */
class FunctionalSpec extends FlatSpec with Matchers {
  "normal" should "compute segmentation for a univariate float signal" in {
    val dMax = 8
    val nPoints = 1000
    val segPoints = Array(0, 250, 500, 750)

    val sampleLawDeterministic = Array[() => Real]( // TODO: use stochastic laws, once the setting of random seed is possible
      () => -10.0,
      () => 10.0,
      () => -10.0,
      () => 10.0)

    val sampleLawsStochastic = {
      val lawA = breeze.stats.distributions.Gaussian(10.0, 0.1)
      val lawB = breeze.stats.distributions.Gaussian(10.0, 1.0)

      Array[() => Real](
        () => lawA.sample,
        () => lawB.sample,
        () => lawA.sample,
        () => lawB.sample)
    }

    val data = Test.generateData(sampleLawDeterministic, nPoints, segPoints)
    val kerEval = KerEvalGenerator.generateKernelFromParamData("Gaussian", "0.5", KerEval.DenseVectorReal(data)).get
    val seg = Test.segment(kerEval, dMax, nPoints, None)

    (segPoints) should ===(seg)
  }

  "matrix" should "compute segmentation for a univariate float signal" in {
    val dMax = 8
    val nPoints = 1000
    val segPoints = Array(0, 250, 500, 750)

    val unitMat = DenseMatrix.ones[Real](3, 3)

    val sampleLawDeterministic = Array[() => DenseMatrix[Real]]( // TODO: use stochastic laws, once the setting of random seed is possible
      () => -10.0 *:* unitMat, // *:* is the element-wise product
      () => 10.0 *:* unitMat,
      () => -10.0 *:* unitMat,
      () => 10.0 *:* unitMat)

    val sampleLawStochastic = {
      val muLaw = breeze.stats.distributions.Uniform(-10.0, 10.0)
      val sdLaw = breeze.stats.distributions.Uniform(0.1, 0.1)

      def matLaw = DenseMatrix.tabulate[breeze.stats.distributions.Gaussian](3, 3)((i, j) => breeze.stats.distributions.Gaussian(muLaw.sample, sdLaw.sample))

      Array
        .fill(4)(matLaw)
        .map(s => () => s.map(_.sample)) // for each segment, generate the function that sample every element of the corresponding matLaw
    }

    val data = Test.generateData(sampleLawDeterministic, nPoints, segPoints)
    val kerEval = KerEvalGenerator.generateKernelFromParamData("Gaussian", "0.5", KerEval.DenseVectorDenseMatrixReal(data)).get
    val seg = Test.segment(kerEval, dMax, nPoints, None)

    (segPoints) should ===(seg)
  }

  "multiKernel" should "perform a segmentation" in { // TODO: rewrite using Test.segment, like the other test
    val dMax = 8
    val nPoints = 1000
    val segPoints = Array(0, 250, 500, 750)

    val sampleLawDeterministic = Array[() => Real]( // TODO: use stochastic laws, once the setting of random seed is possible
      () => -10.0,
      () => 10.0,
      () => -10.0,
      () => 10.0)

    val sampleLawsStochastic = {
      val lawA = breeze.stats.distributions.Gaussian(10.0, 0.1)
      val lawB = breeze.stats.distributions.Gaussian(10.0, 1.0)

      Array[() => Real](
        () => lawA.sample,
        () => lawB.sample,
        () => lawA.sample,
        () => lawB.sample)
    }

    val data = Test.generateData(sampleLawDeterministic, nPoints, segPoints)
    val kerEval0 = KerEvalGenerator.generateKernelFromParamData("Gaussian", "0.5", KerEval.DenseVectorReal(data)).get
    val kerEval1 = KerEvalGenerator.generateKernelFromParamData("Linear", "", KerEval.DenseVectorReal(data)).get
    val kerEval = KerEval.linearCombKerEvalFunc(Array(kerEval0, kerEval1), DenseVector[Real](0.5, 0.5))
    val seg = Test.segment(kerEval, dMax, nPoints, None)

    (segPoints) should ===(seg)
  }

  /**
   * Similar to previous utest, except that here variable descriptions are used, which are similar to what would be used with real data.
   */
  "multiVariable" should "perform a segmentation" in { // TODO: rewrite using Test.segment, like the other test
    val dMax = 8
    val nPoints = 1000
    val segPoints = Array(0, 250, 500, 750)

    val sampleLawDeterministic = Array[() => Real]( // TODO: use stochastic laws, once the setting of random seed is possible
      () => -10.0,
      () => 10.0,
      () => -10.0,
      () => 10.0)

    val sampleLawsStochastic = {
      val lawA = breeze.stats.distributions.Gaussian(10.0, 0.1)
      val lawB = breeze.stats.distributions.Gaussian(10.0, 1.0)

      Array[() => Real](
        () => lawA.sample,
        () => lawB.sample,
        () => lawA.sample,
        () => lawB.sample)
    }

    val data0 = Test.generateData(sampleLawDeterministic, nPoints, segPoints)
    val data1 = Test.generateData(sampleLawDeterministic, nPoints, segPoints)

    val varDescription =
      List(
        new KerEval.VarDescription(0.5, KerEval.DenseVectorReal(data0), "Gaussian", "0.5"),
        new KerEval.VarDescription(0.5, KerEval.DenseVectorReal(data1), "Linear", ""))
    val kerEval = KerEval.multivariateKerEval(varDescription)
    val seg = Test.segment(kerEval.get, dMax, nPoints, None)

    (segPoints) should ===(seg)
  }
}