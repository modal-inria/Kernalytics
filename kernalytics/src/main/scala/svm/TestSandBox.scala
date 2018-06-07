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

  val nObs = x.length

  val y = DenseVector[Real](1.0, 1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0)

  val kerEvalFunc = KerEvalGenerator.generateKernelFromParamData("Linear", "", new KerEval.DenseVectorDenseVectorReal(x)).get

  val kerEval = new KerEval(x.length, 0, kerEvalFunc, false)

  /**
   * Note that i1 and i2 must be chosen with y1 != y2, otherwise the linear constraint force them to stay at 0.
   */
  def testOptim {
    val i1 = 0
    val i2 = 4
    val alpha = DenseVector.zeros[Real](nObs)
    val b: Real = 0.0
    val C: Real = 1000000 // large value to penalize non compliance with margins

    val cache = Core2.computeCache(alpha, b, y, kerEval)
    val (psi0, _) = Core2.checkSolution(kerEval, alpha, y, C)
    
    val res = Core2.binaryOptimization(i1, i2, alpha, b, y, cache, kerEval, C).map(t => t match {
      case (a1, a2, bNew) => {
        println(s"bNew: $bNew")
        alpha(i1) = a1
        alpha(i2) = a2
        
        println(s"a1: $a1, a2: $a2, bNew: $bNew")

        val (psi1, _) = Core2.checkSolution(kerEval, alpha, y, C)
        println(s"psi0: $psi0, psi1: $psi1")
        psi1 - psi0
      }
    })

    println(res.exists(_ < 0.0))
  }
}