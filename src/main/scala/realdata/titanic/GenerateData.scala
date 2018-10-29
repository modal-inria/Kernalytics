package realdata.titanic

import scala.util.matching.Regex

import various.Def

object GenerateData {
  val rootFolder = "realdata/titanic"

  val algoFile = rootFolder + Def.folderSep + "algo.csv"
  val descFile = rootFolder + Def.folderSep + "desc.csv"

  val rawLearnFile = rootFolder + Def.folderSep + "raw/train.data"
  val learnDataFile = rootFolder + Def.folderSep + "learnData.csv"
  val learnYFile = rootFolder + Def.folderSep + "learnY.csv"

  val rawPredictFile = rootFolder + Def.folderSep + "raw/predict.data"
  val predictDataFile = rootFolder + Def.folderSep + "predictData.csv"
  val predictYFileExpected = rootFolder + Def.folderSep + "predictYExpected.csv"

  val sampleLine = " 0.00632  18.00   2.310  0  0.5380  6.5750  65.20  4.0900   1  296.0  15.30 396.90   4.98  24.00"
  val varName = Array("CRIM", "ZN", "INDUS", "CHAS", "NOX", "RM", "AGE", "DIS", "RAD", "TAX", "PTRATIO", "B", "LSTAT", "MEDV")
  val dataType = Array.fill(varName.size)("Real")

  def parseLine(line: String): Array[String] = {
    val pattern = new Regex("[0-9.]+")
    pattern.findAllIn(line).toArray
  }
}