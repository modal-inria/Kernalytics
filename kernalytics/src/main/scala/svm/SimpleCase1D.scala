package svm

import breeze.linalg._
import scala.util.{ Try, Success, Failure }

import io.{ CombineVarParam, ReadVar, ReadParam }
import rkhs.{ KerEval, IO }
import various.Def
import various.TypeDef._

/**
 * A simple one dimensional case linear case, with a few vectors, and for which the analytical solution is known.
 */
object SimpleCase1D {
  def main {
    val C: Real = 10000000 // high penalty so that no point lies on the wrong side of the margins
    val y = DenseVector[Real](1.0, 1.0, -1.0, -1.0) // TODO: manage this from a file

    val rootFolder = "data/svm/SimpleCase1D"
    val dataFile = rootFolder + Def.folderSep + "data.csv"
    val descriptorFile = rootFolder + Def.folderSep + "desc.csv"

    val kerEvalTry =
      for {
        data <- ReadVar.readAndParseVars(dataFile)
        param <- ReadParam.readAndParseParam(descriptorFile)
        kerEval <- CombineVarParam.generateAllKerEval(data, param)
      } yield (data(0).data.nPoint, kerEval)

    val (errorMessage, parsedData) = kerEvalTry match {
      case Failure(m) => (m.toString, (-1, -1))
      case Success(s) => ("", s)
    }

    println(errorMessage)

    // TODO: output lambda coefficients to a a csv file

    kerEvalTry
      .map(kerEval => Core.optimize(kerEval._1, kerEval._2, y, C))
      .map(res => { println(res._1); println(res._2) })
  }

  /**
   * The 1D case is very simple. Here are the numerical values obtained from the analytical solution.
   * This can be used for utest, and also to perturb the solution and see if SMO behaves correctly.
   * C is supposed to be very large, so as to work in the perfectly separated framework.
   * See https://en.wikipedia.org/wiki/Support_vector_machine graphical explanations.
   */
  def analyticSolution {
    //    val x = DenseVector[Real](0.5, 1.5, 2.5, 3.5)
    //    val y = DenseVector[Real](1.0, 1.0, -1.0, -1.0)

    val wNorm = 2.0 // 1 = 2 / wNorm
    val b = 4.0 // b / wNorm = 2

    val A = DenseMatrix(
      (1, 2),
      (3, 4))

    val x = DenseVector(5, 6)

    println(A * x)
  }
}