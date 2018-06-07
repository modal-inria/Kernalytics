package spec.iospec

import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._
import org.scalatest.TryValues._
import scala.util.{ Try, Success, Failure }
import rkhs.{ Algebra, KerEval, KerEvalGenerator, Kernel }
import various.Def
import various.TypeDef._
import offlinechangepoint.{ CostMatrix, Test }
import io.ReadVar
import algo.svm.{ Core, Heuristics }

/**
 * Test IO, using data present on file.
 */
class HeuristicsSpec extends FlatSpec with Matchers {
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

  val kerEvalFunc = KerEvalGenerator.generateKernelFromParamData("Linear", "", new KerEval.DenseVectorDenseVectorReal(x)).get
  val kerEval = new KerEval(x.length, 0, kerEvalFunc, false)

  "naive heuristic" should "reduce the objective function" in {
    val alpha0 = DenseVector.zeros[Real](nObs)
    val b0: Real = 0.0
    val C: Real = 1000000 // large value to penalize non compliance with margins
    val nLoop = 100 // complete loops over all pairs (i1, i2)

    val (psi0, _) = Core.checkSolution(kerEval, alpha0, y, C) // baseline value
    val (alpha1, b) = Heuristics.naive(kerEval, y, C, nLoop)
    val (psi1, _) = Core.checkSolution(kerEval, alpha1, y, C) // optimized value

    val delta = psi1 - psi0

    delta should be < 0.0
  }

  "kkt heuristic" should "reduce the objective function" in {
    val alpha0 = DenseVector.zeros[Real](nObs)
    val b0: Real = 0.0
    val C: Real = 1000000 // large value to penalize non compliance with margins
    val nLoop = 100 // complete loops over all pairs (i1, i2)

    val (psi0, _) = Core.checkSolution(kerEval, alpha0, y, C) // baseline value
    val (alpha1, b) = Heuristics.kkt(kerEval, y, C)
    val (psi1, _) = Core.checkSolution(kerEval, alpha1, y, C) // optimized value

    val delta = psi1 - psi0

    delta should be < 0.0
  }
}