package exec.learn

import exec.Learn

object Examples {
  def svm {
    val rootFolder = "data/exec/svm"
    val res = Learn.main(rootFolder)
    println(res)
  }

  def regression {
    val rootFolder = "data/exec/regression"
    val res = Learn.main(rootFolder)
    println(res)
  }

  def offlinechangepoint {
    val rootFolder = "data/exec/offlinechangepoint"
    val res = Learn.main(rootFolder)
    println(res)
  }
}