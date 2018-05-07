package exec

object Test {
  def svm {
    val rootFolder = "data/exec/svm"
    val res = Exec.main(rootFolder)
    println(res)
  }

  def regression {
    val rootFolder = "data/exec/regression"
    val res = Exec.main(rootFolder)
    println(res)
  }

  def offlinechangepoint {
    val rootFolder = "data/exec/offlinechangepoint"
    val res = Exec.main(rootFolder)
    println(res)
  }
}