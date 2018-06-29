package linalg

import breeze.linalg._
import scala.collection.mutable.SortedSet

import various.TypeDef._

object IncompleteCholesky {
  def icd(kMat: DenseMatrix[Real], m: Index): DenseMatrix[Real] = {
    val nrow = kMat.rows
    val ncol = kMat.cols

    val g = DenseMatrix.zeros[Real](nrow, m)
    val d = diag(kMat)
    val ikVec = DenseVector.zeros[Index](m) // set Ik in the articles: list of selected pivots
    val jkSet = (0 to (m - 1)).to[SortedSet]
    var jKBuffer = jkSet.toBuffer // toArray would be useless, as Array does not have the IndexedSeq trait required for slicing...

    for (k <- 0 to m - 1) {
      val ik = argmax(d(jKBuffer)) // take as pivot the i_k which maximizes the lower bound
      println(s"k: $k, ik: $ik")
      println(s"g:\n$g")
      println(s"d: $d")
      ikVec(k) = ik // update IK
      jkSet -= ik //update JK
      jKBuffer = jkSet.toBuffer // update IndexedSet version of jkSet
      
      g(ik, k) = math.sqrt(d(ik))

      val sumTerm = DenseVector.zeros[Real](jKBuffer.size)
      for (j <- 0 to k - 2) {
        println(s"j: $j")
        sumTerm += g(jKBuffer, j) *:* g(ik, j)
        println(sumTerm)
      }

      val scale = 1.0 / g(ik, k)
      for (j <- jKBuffer) {
        g(j, k) = scale * (kMat(j, ik) - sumTerm(j)) // g(jKBuffer, k) = ... not possible, hence the manual slicing
      }
      
      for (j <- jKBuffer) {
        d(j) = d(j) - g(j, k) * g(j, k)
      }
    }

    return g
  }
  
  def test {
    val nObs = 3
    
    val kMat = DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => j + i * nObs)
    val res = icd(kMat, 3)
    println(res)
  }
}