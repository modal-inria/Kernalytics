package linalg

import breeze.linalg._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SortedSet

import various.TypeDef._

object IncompleteCholesky {
  def icd(kMat: DenseMatrix[Real], m: Index): DenseMatrix[Real] = {
    val nrow = kMat.rows
    val ncol = kMat.cols

    val g = DenseMatrix.zeros[Real](nrow, m)
    val d = diag(kMat)
    val ikVec = ArrayBuffer.fill[Index](ncol)(0) // set Ik in the articles: list of selected pivots
    val jkSet = (0 to (m - 1)).to[SortedSet] // mutable SortedSet
    var jkBuffer = jkSet.toBuffer // a buffer is an indexedSeq with constant access, that could be used for slicing
    println(s"jkBuffer: $jkBuffer")

    for (k <- 0 to m - 1) {
      //      val ik = argmax(d(jkBuffer)) // take as pivot the i_k which maximizes the lower bound, TODO: use absolute indexing, not slice indexing
      //      val ik = k // not using any heuristic

      val ik =
        jkBuffer
          .map(j => (j, d(j)))
          .reduceLeft((p1, p2) => if (p1._2 < p2._2) p2 else p1)
          ._1

      println(s"k: $k, ik: $ik")
      println(s"g:\n$g")
      println(s"d: $d")
      ikVec(k) = ik // update IK
      jkSet -= ik //update JK
      jkBuffer = jkSet.toBuffer // update IndexedSet version of jkSet (used for slicing)
      println(s"jkBuffer: $jkBuffer")

      g(ik, k) = math.sqrt(d(ik))
      println(s"g(ik, k): ${g(ik, k)}")

      val sumTerm = DenseVector.zeros[Real](jkBuffer.size)
      for (j <- 0 to k - 1) {
        println(s"j: $j")
        sumTerm += g(jkBuffer, j) *:* g(ik, j)
        println("sumTerm:")
        println(sumTerm)
      }

      val scale = 1.0 / g(ik, k)
      g(jkBuffer, k) := scale *:* (kMat(jkBuffer, ik) - sumTerm)

      for (j <- jkBuffer) {
        d(j) = d(j) - g(j, k) * g(j, k)
      }
    }

//    println(s"ikVec: $ikVec")
//    println("unordered")
//    println(g)
//    return g(ikVec.toBuffer, ::).toDenseMatrix
    return g
  }

  def test {
    val nObs = 3

    val kMat = DenseMatrix(
      (2.0, -1.0, 0.0),
      (-1.0, 2.0, -1.0),
      (0.0, -1.0, 2.0))

    val res = icd(kMat, 3)
    println("g")
    println(res)
    println("g * g.t")
    println(res * res.t)
  }
}