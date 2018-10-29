package realdata.housing

import scala.io.Source
import scala.util.Random

import various.Def
import various.TypeDef._

object Run {
  val rootFolder = "realdata/housing"
  val predictYFile = rootFolder + Def.folderSep + "predictY.csv"
  val predictYFileExpected = rootFolder + Def.folderSep + "predictYExpected.csv"
  val learnRatio = 0.8 // the rest is used for prediction

  def main {
    val nObs = 506
    val nLearnObs = math.round(nObs * learnRatio).toIndex
    val nPredictObs = nObs - nLearnObs

    val allObs = Random.shuffle((0 to nObs - 1).toList)

    val learnObs = allObs.take(nLearnObs).toArray
    val predictObs = allObs.takeRight(nPredictObs).toArray

    GenerateData.writeData(learnObs, predictObs)

    val resLearn = exec.Learn.main(rootFolder)
    println(resLearn)

    val resPredict = exec.Predict.main(rootFolder)
    println(resPredict)

    computeError
  }

  def computeError() {
    val yComputed =
      Source
        .fromFile(predictYFile)
        .getLines
        .map(_.toReal)
        .toArray
        
    

    val yExpected =
      Source
        .fromFile(predictYFileExpected)
        .getLines
        .map(_.toReal)
        .toArray

    val nObs = yComputed.size

    val meanYComputed = yComputed.reduce(_ + _) / (nObs.toReal)
    val errorWithMean = yComputed.map(y => math.abs(y - meanYComputed)).reduce(_ + _) / (nObs.toReal)
    
    val errorWithRegression =
      yComputed
        .zip(yExpected)
        .map(p => math.abs(p._1 - p._2))
        .reduce(_ + _)
        ./(nObs.toReal)

    println(s"Error with mean: $errorWithMean, error with regression: $errorWithRegression")
  }
}