package regression

import breeze.linalg._
import breeze.plot._
import breeze.stats.distributions._

object Data {
  /**
   * Given a vector of points generate the lower degree polynomial that passes through all of them.
   */
  def polynomial1D(x: DenseVector[Double], y: DenseVector[Double]): DenseVector[Double] = {
    val nPoints = x.length
    val vanderMat = vandermonde(x, nPoints)
    return vanderMat \ y
  }

  def vandermonde(x: DenseVector[Double], order: Int): DenseMatrix[Double] = {
    val nObs = x.length
    return DenseMatrix.tabulate[Double](nObs, order)((i, j) => math.pow(x(i), j))
  }

  /**
   * @return a pair of vectors (x, y)
   */
  def oneDimFunction(
    pointsX: DenseVector[Double],
    pointsY: DenseVector[Double],
    sd: Double,
    min: Double,
    max: Double,
    nPoints: Int): (DenseVector[Double], DenseVector[Double]) = {
    val polyDegree = pointsX.length
    val coeff = polynomial1D(pointsX, pointsY)

    def polyEval(coeff: DenseVector[Double], x: Double): Double = {
      val powers = DenseVector.tabulate[Double](pointsX.length)(i => math.pow(x, i))
      return coeff dot powers
    }

    val uni = new Uniform(min, max)

    val x = DenseVector.rand[Double](nPoints, uni)

    val gauss = new Gaussian(0.0, sd)

    val y = x
      .map(polyEval(coeff, _))
      .map(_ + (gauss.sample))

    return (x, y)
  }

  def oneDimFunctionTest = {
    val (x, y) = oneDimFunction(
      DenseVector[Double](0.0, 10.0, 16.0), // pointsX
      DenseVector[Double](0.0, 10.0, 8.0), // pointsY
      1.0, // sd
      -3.0, // min
      18.0, // max,
      100) // nPoints

    val fig = Figure()
    val plt = fig.subplot(0)
    plt += plot(x, y, '.')
  }
}