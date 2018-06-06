package spec.iospec

import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._
import org.scalatest.TryValues._
import scala.util.{ Try, Success, Failure }

import rkhs.{ Algebra, KerEval, KerEvalGenerator, Kernel }
import various.TypeDef._
import offlinechangepoint.{ CostMatrix, Test }
import io.ReadVar
import svm.Core2

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

  "binaryOptimization" should "return None when optimum is provided as input" in {
    val alpha = DenseVector[Real](0.75, 0.75, 0.0, 0.0, 3.5, 0.0, 0.0, 0.0) // analytic solution
    val b: Real = 2.0
    val y = DenseVector[Real](1.0, 1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0)
    val C: Real = 1000 // large value to penalize non compliance with margins

    val nObs = alpha.length

    val kerEvalFunc = KerEvalGenerator.generateKernelFromParamData("Linear", "", new KerEval.DenseVectorDenseVectorReal(x)).get
    val kerEval = new KerEval(nObs, 0, kerEvalFunc, false)

    val cache = Core2.computeCache(alpha, b, y, kerEval)

    val res = Core2.binaryOptimization(0, 1, alpha, b, y, cache, kerEval, C)

    res match {
      case Some((a1, a2, b)) => println(s"a1: $a1, a2: $a2, b: $b")
      case None => println(None)
    }

    res shouldBe None
  }
}