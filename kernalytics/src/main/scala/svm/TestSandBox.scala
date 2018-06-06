package svm

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

/**
 * Formal solution has been derived in http://axon.cs.byu.edu/Dan/678/miscellaneous/SVM.example.pdf
 */
object TestSandBox {
  def testOptim {
    val alpha = DenseVector[Real](0.75, 0.75, 0.0, 0.0, 3.5, 0.0, 0.0, 0.0) // analytic solution
    val b: Real = 2.0
    val y = DenseVector[Real](1.0, 1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0)
    val C: Real = 1000 // large value to penalize non compliance with margins
    
    val nObs = alpha.length

    val kerEvalFunc = KerEvalGenerator.generateKernelFromParamData("Linear", "", new KerEval.DenseVectorReal(alpha)).get
    val kerEval = new KerEval(nObs, 0, kerEvalFunc, false)
    
    val cache = Core2.computeCache(alpha, b, y, kerEval)
    println(cache)
    
    val res = Core2.binaryOptimization(0, 1, alpha, b, y, cache, kerEval, C)
    
    res match {
      case Some((a1, a2, b)) => println(s"a1: $a1, a2: $a2, b: $b")
      case None => println(None)
    }
    
    println(res == None)
  }
}