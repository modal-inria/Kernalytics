package svm

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.io.Source
import scala.util.{ Try, Success, Failure }

import exec.Exec
import various.Def
import various.TypeDef._

object SVM {
  def main(param: Exec.AlgoParam): Try[Unit] = {
    val rootFolder = param.rootFolder
    val yFile = rootFolder + Def.folderSep + "y.csv"

    val res = for {
      y <- parseY(param.nObs, yFile)
      C <- getC(param)
      resAlgo <- Success(CoreNoHeuristic.optimize(param.nObs, param.kerEval, y, C))
      resWrite <- writeResults(param.rootFolder, resAlgo)
    } yield resWrite

    return res
  }

  /**
   * Check that the parameter C has been provided, is convertible and strictly positive.
   */
  def getC(param: Exec.AlgoParam): Try[Real] =
    CExistence(param)
      .flatMap(C => Try(param.algo("C").toReal))
      .flatMap(CStricltyPositive)

  def CExistence(param: Exec.AlgoParam): Try[Exec.AlgoParam] = {
    if (param.algo.contains("C"))
      Success(param)
    else
      Failure(new Exception("C parameter not found in algo.csv."))
  }

  def CStricltyPositive(C: Real): Try[Real] = {
    if (Def.epsilon < C)
      Success(C)
    else
      Failure(new Exception("C must be strictly positive."))
  }

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
      Failure(new Exception(s"y.csv must have one column and $nObs rows."))
  }

  /**
   * Write the result with a column of alpha coefficients, and a column with just b.
   */
  def writeResults(rootFolder: String, res: (DenseVector[Real], Real)): Try[Unit] = {
    val nObs = res._1.size
    val outFile = rootFolder + Def.folderSep + "model.csv"
    
    val header = "alpha" + Def.csvSep + "b"
    val data = Array.tabulate(nObs + 1)(i => i match {
      case 0 => header
      case 1 => res._1(0) + Def.csvSep + res._2
      case _ => res._1(i - 1) + Def.csvSep
    })
    
    return Try(FileUtils.writeStringToFile(new File(outFile), data.mkString(Def.eol), "UTF-8"))
  }
}