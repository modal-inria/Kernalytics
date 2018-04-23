package svm

import breeze.linalg._
import scala.util.{ Try, Success, Failure }

import exec.Exec
import various.Def
import various.TypeDef._

object SVM {
  def main(param: Exec.AlgoParam): Try[Unit] = {
    val rootFolder = param.rootFolder
    val yFile = rootFolder + Def.folderSep + "y.csv"
    
    // run the algorithm
    // write the result with a column of alpha coefficients, and a column with just b

    ???
  }
  
  /**
   * Check that the parameter C has been provided, is convertible and strictly positive
   */
  def checkCorrectC(param: Exec.AlgoParam): Try[Exec.AlgoParam] = {
    ???
  }
 
  /**
   * Check that the response file y has been provided and contains the right number of correctly formatted elements.
   * This should be moved later to io, as a response file in general has a lot of things to check.
   */
  def parseY(fileName: String): Try[DenseVector[Real]] = {
    ???
  }
  
  def writeResults(rootFolder: String, res: (DenseVector[Real], Real)): Try[Unit] = {
    ???
  }
}