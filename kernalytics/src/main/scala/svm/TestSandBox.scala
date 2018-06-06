package svm

import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import scala.util.{ Try, Success, Failure }

import rkhs.{ Algebra, KerEval, KerEvalGenerator, Kernel }
import various.Def
import various.TypeDef._
import offlinechangepoint.{ CostMatrix, Test }
import io.ReadVar

/**
 * Formal solution has been derived in http://axon.cs.byu.edu/Dan/678/miscellaneous/SVM.example.pdf
 */
object TestSandBox {
  val x = DenseVector[DenseVector[Real]](
    DenseVector[Real](3, 1),
    DenseVector[Real](3, -1),
    DenseVector[Real](6, 1),
    DenseVector[Real](6, -1),
    DenseVector[Real](1, 0),
    DenseVector[Real](0, 1),
    DenseVector[Real](-1, 0),
    DenseVector[Real](0, -1))

  def testOptim {
    val alpha = DenseVector[Real](0.75, 0.75, 0.0, 0.0, 3.5, 0.0, 0.0, 0.0) // analytic solution
    val b: Real = 2.0
    val y = DenseVector[Real](1.0, 1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0)
    val C: Real = 1000000 // large value to penalize non compliance with margins
    val perturbation = 0.25

    val nObs = alpha.length

    val kerEvalFunc = KerEvalGenerator.generateKernelFromParamData("Linear", "", new KerEval.DenseVectorDenseVectorReal(x)).get
    val kerEval = new KerEval(nObs, 0, kerEvalFunc, false)

    val (psiUnperturbed, _) = Core2.checkSolution(kerEval, alpha, y, C)

    alpha(0) += perturbation // perturb solution
    alpha(1) -= perturbation

    val (psiPerturbed, _) = Core2.checkSolution(kerEval, alpha, y, C)

    val cache = Core2.computeCache(alpha, b, y, kerEval)
    val res = Core2.binaryOptimization(0, 1, alpha, b, y, cache, kerEval, C).map(t => t match {
      case (a1, a2, b) => {
        (math.abs(a1 - 0.75) < Def.epsilon) && (math.abs(a2 - 0.75) < Def.epsilon)
      }
    })

    println(res.contains(true))
  }
}