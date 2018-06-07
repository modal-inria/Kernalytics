package algo.kmeans

import breeze.linalg._
import breeze.plot._
import rkhs.{ Gram }

object Plot {
  class plotLim(
    val xMin: Double,
    val xMax: Double,
    val yMin: Double,
    val yMax: Double,
    val nX: Int,
    val nY: Int)

  def plotRes(
    lim: plotLim,
    kernel: (DenseVector[Double], DenseVector[Double]) => Double,
    gram: DenseMatrix[Double],
    param: DenseMatrix[Double],
    learnObs: DenseVector[DenseVector[Double]]) {
    val squaredNormCk = param(::, *)
      .map(c => Gram.scalarProduct(gram, c, c)) // compute the norm of each center of class (each column of param) as it is a term used multiple times in the computation of the distance
      .t

    val mapValues = DenseMatrix.tabulate[Double](lim.nX, lim.nY)((i, j) => Base.predict(
      kernel,
      param,
      squaredNormCk,
      learnObs,
      getPos(lim, i, j)))

    //    val mapValues = DenseMatrix.tabulate[Double](lim.nX, lim.nY)((i, j) => if (i == 5 && j == 10) 1.0 else 0.0)

    val f = Figure()
    f.subplot(0) += image(mapValues)
  }

  def getPos(
    lim: plotLim,
    i: Int,
    j: Int): DenseVector[Double] = DenseVector[Double](
    j.toDouble / lim.nY.toDouble * (lim.yMax - lim.yMin) + lim.yMin,
    i.toDouble / lim.nX.toDouble * (lim.xMax - lim.xMin) + lim.xMin)

  def plotIn(lim: plotLim, data: Data.GaussianGeneratedData) {
    val nObs = data.zi.length
    val i0 = (0 to nObs - 1).filter(i => data.zi(i) == 0).toArray
    val i1 = (0 to nObs - 1).filter(i => data.zi(i) == 1).toArray

    val x0 = i0.map(data.data(_)(0))
    val y0 = i0.map(data.data(_)(1))

    val x1 = i1.map(data.data(_)(0))
    val y1 = i1.map(data.data(_)(1))

    val f = Figure()
    f.subplot(0) += plot(x0, y0, '+')
    f.subplot(0) += plot(x1, y1, '.')
  }
}