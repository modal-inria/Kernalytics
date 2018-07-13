package exec

object Examples {
  def svm {
    algo.svm.examples.SimpleCase2D.writeAll
    val rootFolder = "data/exec/svm"

    val resLearn = Learn.main(rootFolder)
    println(resLearn)

    val resPredict = Predict.main(rootFolder)
    println(resPredict)

    algo.svm.examples.SimpleCase2D.checkPrediction
  }

  def regression {
    algo.regression.examples.Simple.writeAll
    val rootFolder = "data/exec/regression"
    
    val resLearn = Learn.main(rootFolder)
    println(resLearn)
    
    val resPredict = Predict.main(rootFolder)
    println(resPredict)
    
    algo.regression.examples.Simple.compareExpectedPredicted
  }

  def offlinechangepoint {
    val rootFolder = "data/exec/offlinechangepoint"
    val res = Learn.main(rootFolder)
    println(res)
  }
}
