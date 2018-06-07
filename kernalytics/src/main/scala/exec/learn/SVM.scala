package exec.learn

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.io.Source
import scala.util.{ Try, Success, Failure }
import various.Def
import various.TypeDef._
import svm.Core
import exec.Learn
import exec.Param

object SVM {
  val headerSizeY = 2
  val yFileName = "learnY.csv"
  val alphaFileName = "paramAlpha.csv"
  val bFileName = "paramB.csv"

  def main(param: Learn.AlgoParam): Try[Unit] = {
//    val yFile = param.rootFolder + Def.folderSep + yFileName
//
//    val res = for {
//      y <- parseY(param.kerEval.nObs, yFile)
//      C <- getC(param)
//      resAlgo <- Success(Core.optimize(param.kerEval.nObs, param.kerEval.k, y, C))
//      resWrite <- writeResults(param.rootFolder, resAlgo)
//    } yield resWrite
//
//    return res
    
    ???
  }

  /**
   * Check that the parameter C has been provided, is convertible and strictly positive.
   */
  def getC(param: Learn.AlgoParam): Try[Real] =
    Param.existence(param, "C")
      .flatMap(C => Try(param.algo("C").toReal))
      .flatMap(Param.realStricltyPositive(_, "C"))

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
  def writeResults(rootFolder: String, res: (DenseVector[Real], Real)): Try[Unit] = {
    val alphaFile = rootFolder + Def.folderSep + alphaFileName
    val bFile = rootFolder + Def.folderSep + bFileName

    return for {
      _ <- Try(FileUtils.writeStringToFile(new File(alphaFile), res._1.data.mkString(Def.eol), "UTF-8"))
      _ <- Try(FileUtils.writeStringToFile(new File(bFile), res._2.toString, "UTF-8"))
    } yield ()
  }
}