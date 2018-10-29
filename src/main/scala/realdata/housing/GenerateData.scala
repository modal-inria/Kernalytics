package realdata.housing

import java.io.File
import org.apache.commons.io.FileUtils
import scala.io.Source
import scala.util.matching.Regex

import various.Def
import various.TypeDef._

/**
 * Comparison with basic imputation using the mean household value.
 *
 * TODO: use optimization algorithm to find the kernel coefficients and parameters
 */
object GenerateData {
  val rootFolder = "realdata/housing"
  val rawDataFile = rootFolder + Def.folderSep + "raw/housing.data"
  val learnDataFile = rootFolder + Def.folderSep + "learnData.csv"
  val predictDataFile = rootFolder + Def.folderSep + "predictData.csv"
  val algoFile = rootFolder + Def.folderSep + "algo.csv"
  val descFile = rootFolder + Def.folderSep + "desc.csv"
  val learnYFile = rootFolder + Def.folderSep + "learnY.csv"
  val predictYFileExpected = rootFolder + Def.folderSep + "predictYExpected.csv"

  val sampleLine = " 0.00632  18.00   2.310  0  0.5380  6.5750  65.20  4.0900   1  296.0  15.30 396.90   4.98  24.00"
  val varName = Array("CRIM", "ZN", "INDUS", "CHAS", "NOX", "RM", "AGE", "DIS", "RAD", "TAX", "PTRATIO", "B", "LSTAT", "MEDV")
  val dataType = Array.fill(varName.size)("Real")

  def parseLine(line: String): Array[String] = {
    val pattern = new Regex("[0-9.]+")
    pattern.findAllIn(line).toArray
  }

  def writeData(learnObs: Array[Index], predictObs: Array[Index]) {
    val content =
      Source
        .fromFile(rawDataFile)
        .getLines
        .toArray
        .map(parseLine)

    writeDataAndY(learnObs.map(content), learnDataFile, learnYFile)
    writeDataAndY(predictObs.map(content), predictDataFile, predictYFileExpected)
  }

  def writeDataAndY(completeData: Array[Array[String]], dataFile: String, yFile: String) {
    val allVars =
      (Array(varName, dataType) ++ completeData)
        .transpose

    val data =
      allVars
        .dropRight(1)
        .transpose
        .map(_.mkString(Def.csvSep))
        .mkString(Def.eol)

    val y =
      allVars
        .last
        .drop(2) // no header in y file
        .mkString(Def.eol)

    FileUtils.writeStringToFile(new File(dataFile), data, "UTF-8")
    FileUtils.writeStringToFile(new File(yFile), y, "UTF-8")
  }

  def writeConfig {
    val algo = Array(
      Array("algo", "regression"),
      Array("lambda", "1.0"),
      Array("cacheGram", "true"))

    val algoStr = algo.transpose.map(_.mkString(Def.csvSep)).mkString(Def.eol)
    FileUtils.writeStringToFile(new File(algoFile), algoStr, "UTF-8")

    val desc = varName.dropRight(1).map(Array(_, "1.0", "Gaussian(1.0)"))

    val descStr = desc.transpose.map(_.mkString(Def.csvSep)).mkString(Def.eol)
    FileUtils.writeStringToFile(new File(descFile), descStr, "UTF-8")
  }
}