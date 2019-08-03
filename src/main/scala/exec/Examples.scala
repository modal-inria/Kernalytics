package exec

object Examples {
  def svm() {
    algo.svm.examples.SimpleCase2D.writeAll
    val rootFolder = "data/exec/svm"

    Learn.main(rootFolder)
    Predict.main(rootFolder)

    algo.svm.examples.SimpleCase2D.checkPrediction
  }

  def regression() {
    algo.regression.examples.Simple.writeAll
    val rootFolder = "data/exec/regression"
    
    val resLearn: Unit = Learn.main(rootFolder)
    println(resLearn)
    
    val resPredict: Unit = Predict.main(rootFolder)
    println(resPredict)
    
    algo.regression.examples.Simple.compareExpectedPredicted
  }

  def offlinechangepoint() {
    val rootFolder = "data/exec/offlinechangepoint"
    Learn.main(rootFolder)
  }
}
