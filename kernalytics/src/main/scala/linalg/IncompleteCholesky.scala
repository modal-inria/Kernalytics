package linalg

import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SortedSet

import various.TypeDef._

object IncompleteCholesky {
  def icd(kMat: DenseMatrix[Real], m: Index): DenseMatrix[Real] = {
    val nrow = kMat.rows
    val ncol = kMat.cols

    val g = DenseMatrix.zeros[Real](nrow, m)
    val d = diag(kMat).copy
    val jkSet = (0 to (ncol - 1)).to[SortedSet] // mutable SortedSet
    var jkBuffer = jkSet.toBuffer // a buffer is an indexedSeq with constant access, that could be used for slicing

    for (k <- 0 to m - 1) {
      val ik =
        jkBuffer
          .map(j => (j, d(j)))
          .reduceLeft((p1, p2) => if (p1._2 < p2._2) p2 else p1)
          ._1

      jkSet -= ik //update JK
      jkBuffer = jkSet.toBuffer // update IndexedSet version of jkSet (used for slicing)

      g(ik, k) = math.sqrt(d(ik))

      val sumTerm = DenseVector.zeros[Real](jkBuffer.size)
      for (j <- 0 to k - 1) {
        sumTerm += g(jkBuffer, j) *:* g(ik, j)
      }

      val scale = 1.0 / g(ik, k)
      g(jkBuffer, k) := scale *:* (kMat(jkBuffer, ik) - sumTerm)

      for (j <- jkBuffer) {
        d(j) = d(j) - g(j, k) * g(j, k)
      }
    }

    return g
  }
}