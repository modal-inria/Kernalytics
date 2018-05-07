package exec

object Test {
  def svm {
    val rootFolder = "data/exec/svm/SimpleCase2D"
    val res = Exec.main(rootFolder)
    println(res)
  }

  def regression {
    val rootFolder = "data/exec/regression/SimpleExample"
    val res = Exec.main(rootFolder)
    println(res)
  }

  def offlinechangepoint {
    val rootFolder = "data/exec/offlinechangepoint"
    val res = Exec.main(rootFolder)
    println(res)
  }
}