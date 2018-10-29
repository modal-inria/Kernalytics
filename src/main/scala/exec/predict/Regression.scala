package exec.predict

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.io.Source
import scala.util.{ Try, Success, Failure }
import algo.regression.PredictAlgorithm
import various.Def
import various.TypeDef._
import exec.Predict
import exec.Param

object Regression {
  def main(param: Predict.AlgoParam): Try[Unit] = {
    val inBetaFileName = "paramBeta.csv"
    val outYFileName = "predictY.csv"

    val res = for {
      beta <- parseVectorFile(param.kerEval.nObsLearn, param.rootFolder, inBetaFileName)
      resAlgo <- Success(PredictAlgorithm.predict(param.kerEval, beta))
      resWrite <- writeResults(param.rootFolder, outYFileName, resAlgo)
    } yield resWrite

    return res
  }

  /**
   * Check that the response file y has been provided and contains the right number of correctly formatted elements.
   * This should be moved later to io, as a response file in general has a lot of things to check.
   */
  def parseVectorFile(nObs: Index, rootFolder: String, fileName: String): Try[DenseVector[Real]] = {
    val inCompletePath = rootFolder + Def.folderSep + fileName

    Try(Source.fromFile(new File(inCompletePath)))
      .map(_.getLines.map(_.split(Def.csvSep)).toArray.transpose)
      .flatMap(checkNElements(nObs, _, fileName))
      .flatMap(d => Try(d(0).map(s => s.toReal)))
      .map(DenseVector[Real])
  }

  /**
   * Check that csv only has one column, and the correct number of rows.
   */
  def checkNElements(nObs: Index, data: Array[Array[String]], fileName: String): Try[Array[Array[String]]] = {
    if (data.size == 1 && data(0).size == nObs)
      Success(data)
    else
      Failure(new Exception(s"$fileName must have one column and $nObs data rows."))
  }

  def writeResults(rootFolder: String, betaFileName: String, res: DenseVector[Real]): Try[Unit] = {
    val outFile = rootFolder + Def.folderSep + betaFileName
    return Try(FileUtils.writeStringToFile(new File(outFile), res.data.mkString(Def.eol), "UTF-8"))
  }
}