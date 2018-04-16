package svm

import breeze.linalg._

import io.{ CombineVarParam, ReadVar, ReadParam }
import rkhs.{ KerEval, IO }
import various.TypeDef._

/**
 * A simple two dimensional case linear case, with a few vectors, and for which the analytical solution is known.
 * Points are scattered around the line x = y, with two support vectors on each side, and some random points further.
 */
object SimpleCase2D {
  def main {
    val C = 1.0
    val y = new DenseVector[Real](Array[Real](1.0, 1.0, 1.0, 1.0, 1.0, -1.0, -1.0, -1.0, -1.0, -1.0)) // TODO: manage this from a file
    val dataFile = "data/svm/SimpleCase2D/data.csv"
    val descriptorFile = "data/svm/SimpleCase2D/descriptor.csv"

    val kerEvalTry =
      for {
        data <- ReadVar.readAndParseVars(dataFile)
        param <- ReadParam.readAndParseParam(descriptorFile)
        kerEval <- CombineVarParam.generateAllKerEval(data, param)
      } yield (data(0).data.nPoint, kerEval)

    val seg =
      kerEvalTry.map(kerEval => Core.optimize(kerEval._1, kerEval._2, y, C))
  }
}