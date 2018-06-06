package spec.variousspec

import breeze.linalg._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._
import various.Math
import various.TypeDef._
import rkhs.Algebra
import org.scalactic.source.Position.apply

/**
 * http://www.scalatest.org/user_guide/using_matchers
 * 
 * TODO: Note that the linear constraint sum_i \alpha_i y_i = 0 is not satisfied by the solution. Take it with caution...
 */
class MathSpec extends FlatSpec with Matchers {
  "factorial" should "compute factorial values correctly" in {
    (0 to 5).toList.map(Math.factorial) should ===(List(1, 1, 2, 6, 24, 120))
  }

  "binomial" should "compute binomial" in {
    (Math.binomial(5, 0)) should ===(1)
    (Math.binomial(7, 3) + Math.binomial(7, 4)) should ===(Math.binomial(8, 4))
  }

  "linear regression" should "estimate a linear regression using least squares" in {
    val nObs = 30
    val nVar = 2

    val theta = DenseVector[Real](3.0, -12.0)

    val nonLinearFunctions = Array[Real => Real](
      x => math.pow(x, 2) + 24.0,
      x => 3.0 * math.log(x))

    val sampledDistribution = Uniform(10.0, 20.0)
    val x = DenseMatrix.fill[Real](nObs, nVar)(sampledDistribution.sample)

    val xFuncApplied = DenseMatrix.tabulate[Real](nObs, nVar)((i, j) => nonLinearFunctions(j)(x(i, j)))
    val y = xFuncApplied(*, ::).map(r => theta dot r)

    val computedTheta = Math.linearRegression(xFuncApplied, y)

    norm(theta - computedTheta) should ===(0.0 +- 1.0e-8)
  }

  /**
   * Expected value obtained from https://www.wolframalpha.com/input/?i=log(binomial(100,+20))
   */
  "logBinomial" should "provide a good approximate of log(binomial)" in {
    val n = 100
    val k = 20

    val expectedValue = 47.73

    Math.logBinomial(n, k) should ===(expectedValue +- 0.01)
  }

  "logBinomial" should "provide a good approximate of log(binomial) even for small values of k" in {
    val n = 10
    val k = DenseVector[Index](0, 1, 2, 3)

    val direct = k.map(Math.logBinomialExact(n, _))
    val approximate = k.map(Math.logBinomial(n, _))

    norm(direct - approximate) should ===(0.0 +- 0.1)
  }

  "segmentationMatrix" should "provide a matrix which Frobenius norm is the square of the number of segments" in {
    val seg = List(7, 5, 2, 0)
    val nObs = 10

    val res = Math.segmentationMatrix(seg, nObs)

    math.pow(Algebra.DenseMatrixReal.NormedSpace.norm(res), 2.0) should ===(4.0 +- 1.0e-4)
  }
}