package spec.offlinechangepointspec

import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._
import various.TypeDef._
import org.scalactic.source.Position.apply
import org.scalatest.TryValues._

import linalg.IncompleteCholesky
import rkhs.{ Algebra, KerEval, Kernel }

/**
 * A mix of various unit tests put here until a better place is found.
 */
class IncompleteCholeskySpec extends FlatSpec with Matchers {
  "icd" should "provide an exact decomposition when full rank is asked for" in {
    val nObs = 6
    val m = nObs

    val lambda = 1.0 // A + lambda * Id to get from semi positive definite to positive definite matrix
    val A = DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => i * nObs + j) // column vectors will be used to generate a Gram matrix
    val kMat = DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => A(i, ::) dot A(j, ::)) + lambda * DenseMatrix.eye[Real](nObs)

    val maxRelativeError =
      IncompleteCholesky
        .icd(kMat, m)
        .map(res => {
          val approxKMat = res * res.t
          max(abs(approxKMat - kMat))
        })

    maxRelativeError.success.value === (0.0 +- 1e-8)
  }

  "icd" should "provide an exact decomposition even when a function is provided" in {
    val nObs = 6
    val m = nObs

    val lambda = 1.0 // A + lambda * Id to get from semi positive definite to positive definite matrix
    val A = DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => i * nObs + j) // column vectors will be used to generate a Gram matrix
    val kMat = DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => A(i, ::) dot A(j, ::)) + lambda * DenseMatrix.eye[Real](nObs)

    def kerEvalFunc(i: Index, j: Index): Real = kMat(i, j)

    val maxRelativeError =
      IncompleteCholesky
        .icd(nObs, kerEvalFunc, m)
        .map(res => {
          val approxKMat = res * res.t
          max(abs(approxKMat - kMat))
        })


    maxRelativeError.success.value === (0.0 +- 1e-8)
  }
}