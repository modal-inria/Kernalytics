package realdata.housing

import scala.util.Random

import various.TypeDef._

object Run {
  val rootFolder = "realdata/housing"
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
  }

  def computeError(expectedYFile: String, computedYFile: String) {
    ???
  }
}