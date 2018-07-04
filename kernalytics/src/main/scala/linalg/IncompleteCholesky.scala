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
    val ikVec = ArrayBuffer.fill[Index](ncol)(0) // set Ik in the articles: list of selected pivots
    val jkSet = (0 to (ncol - 1)).to[SortedSet] // mutable SortedSet
    var jkBuffer = jkSet.toBuffer // a buffer is an indexedSeq with constant access, that could be used for slicing

    for (k <- 0 to m - 1) {
      //      val ik = argmax(d(jkBuffer)) // take as pivot the i_k which maximizes the lower bound, TODO: use absolute indexing, not slice indexing
      //      val ik = k // not using any heuristic

      val ik =
        jkBuffer
          .map(j => (j, d(j)))
          .reduceLeft((p1, p2) => if (p1._2 < p2._2) p2 else p1)
          ._1

      ikVec(k) = ik // update IK
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

  def test {
    val nObs = 5

    val kMat = DenseMatrix(
      (2.0, -1.0, 0.0),
      (-1.0, 2.0, -1.0),
      (0.0, -1.0, 2.0))

    println(det(kMat))

    val res = icd(kMat, 3)
    println("g")
    println(res)
    println("g * g.t")
    println(res * res.t)
  }

  def test2 {
    val nObs = 5
    val m = nObs

    val lambda = 1.0 // A + lambda * Id to get from semi positive definite to positive definite matrix
    val A = DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => i * nObs + j) // column vectors will be used to generate a Gram matrix
    val kMat = DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => A(i, ::) dot A(j, ::)) + lambda * DenseMatrix.eye[Real](nObs)
    println(det(A))
    println(det(kMat))
    println(A)
    println("")
    println(kMat)

    val res = icd(kMat, m)
    val approxKMat = res * res.t
    println(res)
    println("")
    println(approxKMat)
    println("")
    println(kMat)
  }

  def test3 {
    val nObs = 5
    val m = 3
    val lambda = 1.0 // A + lambda * Id to get positive definite matrix
    val normalLaw = Gaussian(0.0, 1.0)

    val A = DenseMatrix.fill[Real](nObs, nObs)(normalLaw.sample) // column vectors will be used to generate a Gram matrix
    val kMat = DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => A(i, ::) dot A(j, ::)) + lambda * DenseMatrix.eye[Real](nObs)
    println(det(A))
    println(det(kMat))
    println(kMat)

    val res = icd(kMat, m)
    println("g")
    println(res)
    println("g * g.t")
    println(res * res.t)
  }
}