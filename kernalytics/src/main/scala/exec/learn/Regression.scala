package exec.learn

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.io.Source
import scala.util.{ Try, Success, Failure }
import regression.EstimationRidge
import various.Def
import various.TypeDef._
import exec.Learn
import exec.Param

object Regression {
  val yFileName = "learnY.csv"
  val betaFileName = "paramBeta.csv"
  
  def main(param: Learn.AlgoParam): Try[Unit] = {
    val yFile = param.rootFolder + Def.folderSep + yFileName

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
  def getLambda(param: Learn.AlgoParam): Try[Real] =
    Param.existence(param, "lambda")
      .flatMap(C => Try(param.algo("lambda").toReal))
      .flatMap(Param.realStricltyPositive(_, "lambda"))

  /**
   * Check that the response file y has been provided and contains the right number of correctly formatted elements.
   * This should be moved later to io, as a response file in general has a lot of things to check.
   */
  def parseY(nObs: Index, fileName: String): Try[DenseVector[Real]] = {
    Try(Source.fromFile(new File(fileName)))
      .map(_.getLines.map(_.split(Def.csvSep)).toArray.transpose)
      .flatMap(checkNElements(nObs, _))
      .flatMap(d => Try(d(0).map(s => s.toReal)))
      .map(DenseVector[Real])
  }

  /**
   * Check that csv only has one columns, and the correct number of rows.
   */
  def checkNElements(nObs: Index, data: Array[Array[String]]): Try[Array[Array[String]]] = {
    if (data.size == 1 && data(0).size == nObs)
      Success(data)
    else
      Failure(new Exception(s"$yFileName must have one column and $nObs data rows."))
  }

  def writeResults(rootFolder: String, res: DenseVector[Real]): Try[Unit] = {
    val outFile = rootFolder + Def.folderSep + betaFileName
    return Try(FileUtils.writeStringToFile(new File(outFile), res.data.mkString(Def.eol), "UTF-8"))
  }
}