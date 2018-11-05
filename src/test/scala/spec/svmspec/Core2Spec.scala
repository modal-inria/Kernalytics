package spec.iospec

import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._
import org.scalatest.TryValues._
import scala.util.{ Try, Success, Failure }

import rkhs.{ Algebra, DataRoot, KerEval, KerEvalGenerator, Kernel }
import various.Def
import various.TypeDef._
import io.ReadVar
import algo.svm.Core

/**
 * Test IO, using data present on file.
 */
class Core2Spec extends FlatSpec with Matchers {
  val x = DenseVector[DenseVector[Real]](
    DenseVector[Real](3, 1),
    DenseVector[Real](3, -1),
    DenseVector[Real](6, 1),
    DenseVector[Real](6, -1),
    DenseVector[Real](1, 0),
    DenseVector[Real](0, 1),
    DenseVector[Real](-1, 0),
    DenseVector[Real](0, -1))

  val nObs = x.length

  val y = DenseVector[Real](1.0, 1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0)

  val kerEvalFunc = KerEvalGenerator.generateKernelFromParamData("Linear", "", new DataRoot.VectorReal(x)).get
  val kerEval = new KerEval.Direct(x.length, 0, kerEvalFunc)

  "binaryOptimization" should "return None when optimum is already provided as input" in {
    val alpha = DenseVector[Real](0.75, 0.75, 0.0, 0.0, 3.5, 0.0, 0.0, 0.0) // analytic solution
    val b: Real = 2.0
    val C: Real = 1000 // large value to penalize non compliance with margins

    val cache = Core.computeCache(alpha, b, y, kerEval)
    val res = Core.binaryOptimization(0, 1, alpha, b, y, cache, kerEval, C)

    res shouldBe None
  }

  "binaryOptimization" should "return optimal solution when it is perturbed" in { // TODO: not sure if the analytical solution provided is correct anyway...
    val alpha = DenseVector[Real](0.75, 0.75, 0.0, 0.0, 3.5, 0.0, 0.0, 0.0) // analytic solution
    val b: Real = 2.0
    val C: Real = 1000000 // large value to penalize non compliance with margins
    val perturbation = 0.25

    val (psiUnperturbed, _) = Core.checkSolution(kerEval, alpha, y, C)

    alpha(0) += perturbation // perturb solution
    alpha(1) -= perturbation

    val (psiPerturbed, _) = Core.checkSolution(kerEval, alpha, y, C)

    val cache = Core.computeCache(alpha, b, y, kerEval)
    val res = Core.binaryOptimization(0, 1, alpha, b, y, cache, kerEval, C).map(t => t match {
      case (a1, a2, b) => {
        (math.abs(a1 - 0.75) < Def.epsilon) && (math.abs(a2 - 0.75) < Def.epsilon)
      }
    })

    res.contains(true) shouldBe true
  }

  "binaryOptimization" should "reduce objective function when called with standard, 0-based initialization" in {
    val i1 = 0
    val i2 = 4
    val alpha = DenseVector.zeros[Real](nObs)
    val b: Real = 0.0
    val C: Real = 1000000 // large value to penalize non compliance with margins

    val cache = Core.computeCache(alpha, b, y, kerEval)
    val (psi0, _) = Core.checkSolution(kerEval, alpha, y, C)

    val res = Core.binaryOptimization(i1, i2, alpha, b, y, cache, kerEval, C).map(t => t match {
      case (a1, a2, bNew) => {
        alpha(i1) = a1
        alpha(i2) = a2

        val (psi1, _) = Core.checkSolution(kerEval, alpha, y, C)
        psi1 - psi0
      }
    })

    res.exists(_ < 0.0) shouldBe true
  }
}