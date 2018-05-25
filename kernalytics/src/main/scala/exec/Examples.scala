package exec

object Examples {
  def svm {
    val rootFolder = "data/exec/svm"
    val res = Learn.main(rootFolder)
    println(res)
  }

  def regressionLearn {
    val rootFolder = "data/exec/regression"
    val res = Learn.main(rootFolder)
    println(res)
  }

  def regressionPredict {
    val rootFolder = "data/exec/regression"
    val res = Predict.main(rootFolder)
    println(res)
  }

  def offlinechangepoint {
    val rootFolder = "data/exec/offlinechangepoint"
    val res = Learn.main(rootFolder)
    println(res)
  }
}