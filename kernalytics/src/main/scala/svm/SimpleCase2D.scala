package svm

import breeze.linalg._
import scala.util.{ Try, Success, Failure }

import io.{ CombineVarParam, ReadVar, ReadParam }
import rkhs.{ KerEval, IO }
import various.Def
import various.TypeDef._

/**
 * A simple two dimensional case linear case, with a few vectors, and for which the analytical solution is known.
 * See http://axon.cs.byu.edu/Dan/678/miscellaneous/SVM.example.pdf
 */
object SimpleCase2D {
  def main {
    val C: Real = 10000000 // large value to penalize non compliance with margins
    val y = DenseVector[Real](1.0, 1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0)

    //    val alpha0 = DenseVector.zeros[Real](8) // standard initialization
    //    val b0: Real = 0.0

    //    val alpha0 = DenseVector[Real](0.75, 0.75, 0.0, 0.0, 3.5, 0.0, 0.0, 0.0) // analytic solution
    //    val b0: Real = 2.0

    val alpha0 = DenseVector[Real](0.75, 0.75, 0.0, 0.0, 3.5, 0.0, 0.0, 0.0) // perturbed analytic solution
    val b0: Real = 12.0

    val rootFolder = "data/svm/SimpleCase2D"
    val dataFile = rootFolder + Def.folderSep + "data.csv"
    val descriptorFile = rootFolder + Def.folderSep + "desc.csv"

    val kerEvalTry =
      for {
        data <- ReadVar.readAndParseVars(dataFile)
        param <- ReadParam.readAndParseParam(descriptorFile)
        kerEval <- CombineVarParam.generateAllKerEval(data, param)
      } yield (data(0).data.nPoint, kerEval)

    kerEvalTry match {
      case Failure(m) => { println("Parsing error: " + m.toString) }
      case Success(s) => {}
    }

    // println(errorMessage)

    // TODO: output lambda coefficients to a a csv file

    kerEvalTry
      .map(kerEval => Core.optimizeImpl(kerEval._1, kerEval._2, y, C, alpha0, b0))
      .map(res => { println(res._1); println(res._2) })
  }
}