package spec.offlinechangepointspec

import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._
import various.TypeDef._
import org.scalactic.source.Position.apply

import linalg.IncompleteCholesky
import offlinechangepoint.{ CostMatrix, Test }
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

    val res = IncompleteCholesky.icd(kMat, m)
    val approxKMat = res * res.t

    val maxRelativeError = max(abs(approxKMat - kMat))
    maxRelativeError should ===(0.0 +- 1e-8)
  }
}