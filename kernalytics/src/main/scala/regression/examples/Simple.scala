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

  def writeLearnData {
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

    val xStr = varName + Def.eol + varType + Def.eol + xLearn.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "learnData.csv"), xStr, "UTF-8")

    val yStr = yLearn.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "learnY.csv"), yStr, "UTF-8")
  }

  def writePredictData {
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

    val xStr = varName + Def.eol + varType + Def.eol + xLearn.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "predictData.csv"), xStr, "UTF-8")

    val yStr = yLearn.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "predictExpectedY.csv"), yStr, "UTF-8")
  }

  def compareExpectedPredicted {
    ???
  }
}