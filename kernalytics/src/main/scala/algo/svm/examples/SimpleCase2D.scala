package algo.svm.examples

import breeze.linalg._
import breeze.stats.distributions._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.io.Source

import various.Def
import various.TypeDef._

/**
 * A simple two dimensional case linear case, with a few vectors, and for which the analytical solution is known.
 * Tgis is the case to generate data and config. The code to run are in:
 * - exec.Examples.svmLearn
 * - exec.Examples.svmPredict
 * See http://axon.cs.byu.edu/Dan/678/miscellaneous/SVM.example.pdf
 */
object SimpleCase2D {
  val rootFolder = "data/exec/svm"
  val varName = "v1"
  val varType = "VectorReal,2"
  val learnYFile = rootFolder + Def.folderSep + "learnY.csv"
  val predictYFile = rootFolder + Def.folderSep + "predictY.csv"
  val predictYExpectedFile = rootFolder + Def.folderSep + "predictYExpected.csv"
  val predictConfusionFile = rootFolder + Def.folderSep + "predictConfusion.csv"

  def writeAll {
    writeData
    writeConfig
  }

  def writeData {
    val nPoints = 100

    val C: Real = 1000 // large value to penalize non compliance with margins

    val xSample = new Uniform(0.0, 2.0 * math.Pi)
    val ySample = new Uniform(-1.0, 1.0)

    val dataLearn = Array.fill[Array[Real]](nPoints)(Array[Real](xSample.sample, ySample.sample))
    val yLearn = dataLearn.map(computeY)

    val dataLearnStr = dataLearn.map(a => a.mkString(Def.optionSep)).mkString(Def.eol)
    val xLearnStr = varName + Def.eol + varType + Def.eol + dataLearnStr
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "learnData.csv"), xLearnStr, "UTF-8")

    val yLearnStr = yLearn.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(learnYFile), yLearnStr, "UTF-8")

    val dataPredict = Array.fill[Array[Real]](nPoints)(Array[Real](xSample.sample, ySample.sample))
    val yPredict = dataPredict.map(computeY)

    val dataPredictStr = dataPredict.map(a => a.mkString(Def.optionSep)).mkString(Def.eol)
    val xPredictStr = varName + Def.eol + varType + Def.eol + dataPredictStr
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "predictData.csv"), xPredictStr, "UTF-8")

    val yPredictStr = yPredict.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(predictYExpectedFile), yPredictStr, "UTF-8")
  }

  def computeY(point: Array[Real]): Real =
    if (point(1) >= math.sin(point(0))) 1.0 else -1.0

  def writeConfig {
    val algo = Array(
      Array("algo", "svm"),
      Array("C", "1000"),
      Array("gramOpti", "LowRank(3)"))
      //Array("gramOpti", "Cache()"))

    val algoStr = algo.transpose.map(_.mkString(Def.csvSep)).mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "algo.csv"), algoStr, "UTF-8")

    val desc = Array(
      Array(varName, "1.0", "Gaussian(1.0)"))
    val descStr = desc.transpose.map(_.mkString(Def.csvSep)).mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "desc.csv"), descStr, "UTF-8")
  }

  /**
   * Compute and export confusion matrix.
   */
  def checkPrediction = {
    val res = DenseMatrix.zeros[Real](2, 2)

    val expectedData =
      Source
        .fromFile(predictYExpectedFile)
        .getLines
        .toArray
        .map(_.toReal)

    val computedData =
      Source
        .fromFile(predictYFile)
        .getLines
        .toArray
        .map(_.toReal)

    val nObs = expectedData.length

    for (i <- 0 to nObs - 1) {
      val currData = Array(expectedData(i), computedData(i))
      val indices = currData.map(y => if (y < 0.0) 0 else 1)
      res(indices(0), indices(1)) += 1
    }

    csvwrite(new File(predictConfusionFile), res)
  }
}