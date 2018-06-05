package regression.examples

import breeze.linalg._
import breeze.plot._
import breeze.stats.distributions._
import java.io.File
import org.apache.commons.io.FileUtils
import rkhs.{ Algebra, Gram, Kernel }
import various.Def
import various.TypeDef._
import regression.Data
import regression.EstimationBFGS
import regression.PredictAlgorithm

object Simple {
  val rootFolder = "data/exec/regression/Simple"

  def writeAll {
    writeData
    writeConfig
  }

  def writeData {
    val min = -3.0
    val max = 18.0
    val sd = 1.0
    val varName = "v1"
    val varType = "Real"
    val nPoints = 100

    val (xLearn, yLearn) = Data.oneDimFunction(
      DenseVector[Real](0.0, 10.0, 16.0), // pointsX
      DenseVector[Real](0.0, 10.0, 8.0), // pointsY
      1.0, // sd
      min, // min
      max, // max,
      100) // nPoints

    val xLearnStr = varName + Def.eol + varType + Def.eol + xLearn.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "learnData.csv"), xLearnStr, "UTF-8")

    val yLearnStr = yLearn.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "learnY.csv"), yLearnStr, "UTF-8")

    val (xPredict, yPredict) = Data.oneDimFunction(
      DenseVector[Real](0.0, 10.0, 16.0), // pointsX
      DenseVector[Real](0.0, 10.0, 8.0), // pointsY
      1.0, // sd
      min, // min
      max, // max,
      100) // nPoints

    val xPredictStr = varName + Def.eol + varType + Def.eol + xPredict.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "predictData.csv"), xPredictStr, "UTF-8")

    val yPredictStr = yLearn.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "predictExpectedY.csv"), yPredictStr, "UTF-8")
  }

  def writeConfig {
    val algo = Array(
      Array("algo", "regression"),
      Array("lambda", "1e-4"),
      Array("cacheGram", "true"))

    val algoStr = algo.transpose.map(_.mkString(Def.csvSep)).mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "algo.csv"), algoStr, "UTF-8")

    val desc = Array(
      Array("v1", "1.0", "Gaussian(1.0)"))
    val descStr = desc.transpose.map(_.mkString(Def.csvSep)).mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "desc.csv"), descStr, "UTF-8")
  }

  def compareExpectedPredicted {
    ???
  }
}