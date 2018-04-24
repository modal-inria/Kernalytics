package exec

import svm.SVM

object Test {
  def main {
    val rootFolder = "data/svm/SimpleCase2D"
    val res = Exec.main(rootFolder)
    println(res)
  }
}