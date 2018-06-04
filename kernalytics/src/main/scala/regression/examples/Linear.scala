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

object Linear {
  val rootFolder = "data/exec/regression/Linear"

  def writeAll {
    writeLearnData
    writeConfig
  }

  def writeLearnData {
    val min = 0.0
    val max = 1.0

    val beta0 = 0.0
    val beta1 = 3.0

    val varName = "v1"
    val varType = "Real"
    val nPointsLearn = 10
    val nPointsPredict = 6

    val xLearn = linspace(min, max, nPointsLearn)
    val xLearnStr = varName + Def.eol + varType + Def.eol + xLearn.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "learnData.csv"), xLearnStr, "UTF-8")

    val yLearn = beta1 * xLearn + beta0
    val yStr = yLearn.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "learnY.csv"), yStr, "UTF-8")

    val xPredict = linspace(min, max, nPointsPredict)
    val xPredictStr = varName + Def.eol + varType + Def.eol + xPredict.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "predictData.csv"), xPredictStr, "UTF-8")

    val yPredict = beta1 * xPredict + beta0
    val yPredictStr = yPredict.data.mkString(Def.eol)
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
      Array("v1", "1.0", "Linear()"))
    val descStr = desc.transpose.map(_.mkString(Def.csvSep)).mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "desc.csv"), descStr, "UTF-8")
  }

  def compareExpectedPredicted {
    ???
  }
}