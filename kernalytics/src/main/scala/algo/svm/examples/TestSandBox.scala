package algo.svm.examples

import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import rkhs.{ DataRoot, KerEval, KerEvalGenerator}
import various.TypeDef._
import algo.svm.Core
import algo.svm.Heuristics

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

  val nObs = x.length

  val y = DenseVector[Real](1.0, 1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0)

  val kerEvalFunc = KerEvalGenerator.generateKernelFromParamData("Linear", "", new DataRoot.VectorReal(x)).get

  val kerEval = new KerEval.Direct(x.length, 0, kerEvalFunc)

  /**
   * Note that i1 and i2 must be chosen with y1 != y2, otherwise the linear constraint force them to stay at 0.
   */
  def testOptim {
    val alpha0 = DenseVector.zeros[Real](nObs)
    val b0: Real = 0.0
    val C: Real = 1000000 // large value to penalize non compliance with margins
    val nLoop = 100 // complete loops over all pairs (i1, i2)

    val (psi0, _) = Core.checkSolution(kerEval, alpha0, y, C) // baseline value
    val (alpha1, b) = Heuristics.kkt(kerEval, y, C)
    val (psi1, _) = Core.checkSolution(kerEval, alpha1, y, C) // optimized value

    println(s"$psi0: $psi0, psi1: $psi1")
    println(s"alpha1: $alpha1, b: $b")
    println(psi1 < psi0)
  }
}