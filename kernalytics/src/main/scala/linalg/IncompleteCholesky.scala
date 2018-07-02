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
    val jkSet = (0 to (m - 1)).to[SortedSet] // mutable SortedSet
    var jkBuffer = jkSet.toBuffer // a buffer is an indexedSeq with constant access, that could be used for slicing
    println(s"jkBuffer: $jkBuffer")

    for (k <- 0 to m - 1) {
      val ik = argmax(d(jkBuffer)) // take as pivot the i_k which maximizes the lower bound
      println(s"k: $k, ik: $ik")
      println(s"g:\n$g")
      println(s"d: $d")
      ikVec(k) = ik // update IK
      jkSet -= ik //update JK
      jkBuffer = jkSet.toBuffer // update IndexedSet version of jkSet
      println(s"jkBuffer: $jkBuffer")
      
      g(ik, k) = math.sqrt(d(ik))

      val sumTerm = DenseVector.zeros[Real](jkBuffer.size)
      for (j <- 0 to k - 1) {
        println(s"j: $j")
        sumTerm += g(jkBuffer, j) *:* g(ik, j)
        println(sumTerm)
      }

      val scale = 1.0 / g(ik, k)
      g(jkBuffer, k) := scale *:* (kMat(jkBuffer, ik) - sumTerm)
      
      for (j <- jkBuffer) {
        d(j) = d(j) - g(j, k) * g(j, k)
      }
    }

    return g
  }
  
  def test {
    val nObs = 3
    
//    val kMat = DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => j + i * nObs)
    val kMat = DenseMatrix(
        (2.0, -1.0, 0.0),
        (-1.0, 2.0, -1.0),
        (0.0, -1.0, 2.0))
        
    val res = icd(kMat, 3)
    println(res)
  }
}