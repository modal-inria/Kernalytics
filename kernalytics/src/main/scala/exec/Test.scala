package exec

object Test {
  def mainSVM {
    val rootFolder = "data/svm/SimpleCase2D"
    val res = Exec.main(rootFolder)
    println(res)
  }

  def mainRegression {
    val rootFolder = "data/regression/SimpleExample"
    val res = Exec.main(rootFolder)
    println(res)
  }
}