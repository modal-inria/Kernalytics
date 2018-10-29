package exec.predict

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.io.Source
import scala.util.{ Try, Success, Failure }

import algo.svm.PredictAlgorithm
import various.Def
import various.TypeDef._
import exec.Predict
import exec.Param

object SVM {
  /**
   * Need to be provided: alpha, b, kerEval and y (for learned dataset)
   */
  def main(param: Predict.AlgoParam): Try[Unit] = {
    val inAlphaFileName = "paramAlpha.csv"
    val inBFileName = "paramB.csv"
    val inYFileName = "learnY.csv"
    val outYFileName = "predictY.csv"

    val res = for {
      alpha <- parseVectorFile(param.kerEval.nObsLearn, param.rootFolder, inAlphaFileName)
      bArray <- parseVectorFile(1, param.rootFolder, inBFileName)
      yLearn <- parseVectorFile(param.kerEval.nObsLearn, param.rootFolder, inYFileName)
      yPredict <- Success(PredictAlgorithm.predict(alpha, bArray(0), param.kerEval, yLearn))
      resWrite <- writeResults(param.rootFolder, outYFileName, yPredict)
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
      Failure(new Exception(s"$fileName must have 1 column and $nObs data rows."))
  }

  def writeResults(rootFolder: String, yFileName: String, res: DenseVector[Real]): Try[Unit] = {
    val outFile = rootFolder + Def.folderSep + yFileName
    return Try(FileUtils.writeStringToFile(new File(outFile), res.data.mkString(Def.eol), "UTF-8"))
  }
}