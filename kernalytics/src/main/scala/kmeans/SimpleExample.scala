package kmeans

import breeze.linalg._
import rkhs.{ Gram, Kernel }
import kmeans.Base.ComputationState
import various.Iterate

object SimpleExample {
  def main {
    val kernel: (DenseVector[Double], DenseVector[Double]) => Double = Kernel.Legacy.Rn.linear
    val sd = 10.0

    val proportion = DenseVector[Double](0.3, 0.7)
    val paramGenerator = Array( // paramGenerator(j)(k)
      Array(
        new Data.GaussianClassParam(-10.0, sd),
        new Data.GaussianClassParam(10.0, sd)),
      Array(
        new Data.GaussianClassParam(-10.0, sd),
        new Data.GaussianClassParam(10.0, sd)))
    val nObs = 100
    val nClass = 2

    val nIteration = 100

    val data = Data.gaussianMixture(
      proportion,
      paramGenerator,
      nObs)

    val gram = Gram.generate(data.data, kernel) // compute Gram matrix
    val param = Base.init(nObs, nClass) // initialize the algorithm by selecting a representative element per class and setting the class centers using them
    val zeroCompState = new ComputationState(
      0,
      gram,
      param,
      DenseMatrix.zeros[Double](nObs, nClass))
    val initCompState = Base.eStep(zeroCompState) // an e step is performed, so that the ComputationState is valid

    val res = Iterate.iterate( // launch the real computation, which alternates E and M steps, updating the computation state
      initCompState,
      Base.emIteration,
      (s: ComputationState) => s.nIteration == nIteration)

    val ziComputed = DenseVector.tabulate[Int](nObs)(i => argmax(res.zik(i, ::)))
    val matConf = computeMatConf(nClass, data.zi, ziComputed)

    println(matConf)

    val lim = new Plot.plotLim(-10.0, 10.0, -10.0, 10.0, 100, 100)
    Plot.plotIn(lim, data)
    Plot.plotRes(lim, kernel, gram, param, data.data)
  }

  /**
   * It would be possible to provide an immutable implementation of this function. But it would be cumbersome and
   * would run in log(n). One possible implementation would use a scala Vector which is updated in a fold for example.
   *
   * I use a for loop because a DenseVector does not provide the zip method (unlike an Array for example).
   */
  def computeMatConf(
    nClass: Int,
    realClass: DenseVector[Int],
    predictedClass: DenseVector[Int]): DenseMatrix[Int] = {
    val nObs = realClass.length
    val matConf = DenseMatrix.zeros[Int](nClass, nClass)

    for (i <- 0 to nObs - 1) {
      matConf(realClass(i), predictedClass(i)) += 1
    }

    return matConf
  }
}