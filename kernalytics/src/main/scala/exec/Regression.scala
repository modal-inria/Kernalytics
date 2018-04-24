package exec

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.io.Source
import scala.util.{ Try, Success, Failure }
import various.Def
import various.TypeDef._
import regression.EstimationRidge

object Regression {
  val headerSizeY = 2
  
  def main(param: Exec.AlgoParam): Try[Unit] = {
    val rootFolder = param.rootFolder
    val yFile = rootFolder + Def.folderSep + "yLearn.csv"

    val res = for {
      y <- parseY(param.kerEval.nObs, yFile)
      lambda <- getLambda(param)
      resAlgo <- Success(EstimationRidge.estimate(param.kerEval, y, lambda))
      resWrite <- writeResults(param.rootFolder, resAlgo)
    } yield resWrite

    return res
  }

  /**
   * Check that the parameter C has been provided, is convertible and strictly positive.
   */
  def getLambda(param: Exec.AlgoParam): Try[Real] =
    lambdaExistence(param)
      .flatMap(C => Try(param.algo("lambda").toReal))
      .flatMap(lambdaPositive)

  def lambdaExistence(param: Exec.AlgoParam): Try[Exec.AlgoParam] = {
    if (param.algo.contains("lambda"))
      Success(param)
    else
      Failure(new Exception("Lambda parameter not found in algo.csv."))
  }

  def lambdaPositive(C: Real): Try[Real] = {
    if (0.0 <= C)
      Success(C)
    else
      Failure(new Exception("C must be positive."))
  }

  /**
   * Check that the response file y has been provided and contains the right number of correctly formatted elements.
   * This should be moved later to io, as a response file in general has a lot of things to check.
   */
  def parseY(nObs: Index, fileName: String): Try[DenseVector[Real]] = {
    Try(Source.fromFile(new File(fileName)))
      .map(_.getLines.map(_.split(Def.csvSep)).toArray.transpose)
      .flatMap(checkNElements(nObs, _))
      .flatMap(d => Try(d(0).drop(headerSizeY).map(s => s.toReal)))
      .map(DenseVector[Real])
  }

  /**
   * Check that csv only has one columns, and the correct number of rows.
   */
  def checkNElements(nObs: Index, data: Array[Array[String]]): Try[Array[Array[String]]] = {
    if (data.size == 1 && data(0).size == nObs + headerSizeY)
      Success(data)
    else
      Failure(new Exception(s"y.csv must have one column and $nObs data rows."))
  }

  /**
   * Write the result with a column of alpha coefficients, and a column with just b.
   */
  def writeResults(rootFolder: String, res: DenseVector[Real]): Try[Unit] = {
    val outFile = rootFolder + Def.folderSep + "model.csv"
    return Try(FileUtils.writeStringToFile(new File(outFile), "alpha" + Def.eol + res.data.mkString(Def.eol), "UTF-8"))
  }
}