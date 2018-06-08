package algo.svm.examples

import breeze.linalg._
import breeze.stats.distributions._
import java.io.File
import org.apache.commons.io.FileUtils

import various.Def
import various.TypeDef._

/**
 * A simple two dimensional case linear case, with a few vectors, and for which the analytical solution is known.
 * See http://axon.cs.byu.edu/Dan/678/miscellaneous/SVM.example.pdf
 */
object SimpleCase2D {
  val rootFolder = "data/exec/svm"
  val varName = "v1"
  val varType = "VectorReal,2"

  def writeAll {
    writeData
    writeConfig
  }
  
  def writeData {
    val nPoints = 1000

    val C: Real = 1000 // large value to penalize non compliance with margins

    val xSample = new Uniform(0.0, 2.0 * math.Pi)
    val ySample = new Uniform(0.0, 1.0)

    val data = Array.fill[Array[Real]](nPoints)(Array[Real](xSample.sample, ySample.sample))
    val y = data.map(computeY)

    val dataStr = data.map(a => a.mkString(Def.optionSep)).mkString(Def.eol)
    val xLearnStr = varName + Def.eol + varType + Def.eol + dataStr
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "learnData.csv"), xLearnStr, "UTF-8")

    val yLearnStr = y.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "learnY.csv"), yLearnStr, "UTF-8")
  }

  def computeY(point: Array[Real]): Real =
    if (point(1) >= math.sin(point(0))) 1.0 else -1.0

  def writeConfig {
    val algo = Array(
      Array("algo", "svm"),
      Array("C", "1000"),
      Array("cacheGram", "true"))

    val algoStr = algo.transpose.map(_.mkString(Def.csvSep)).mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "algo.csv"), algoStr, "UTF-8")

    val desc = Array(
      Array(varName, "1.0", "Gaussian(0.1)"))
    val descStr = desc.transpose.map(_.mkString(Def.csvSep)).mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "desc.csv"), descStr, "UTF-8")
  }
}