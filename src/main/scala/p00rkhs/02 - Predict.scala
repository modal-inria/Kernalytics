package p00rkhs

import breeze.linalg._

object Predict {
  /**
   * Compute the evaluation of the minimizer function.
   * */
  def evaluateMinimize[Data](
      predictSample: DenseVector[Data],
      coefficients: DenseVector[Double],
      learnSample: DenseVector[Data],
      kernel: (Data, Data) => Double)
  : DenseVector[Double] = {
    predictSample.map({x =>
      val eval = DenseVector.tabulate[Double](learnSample.length)(i => kernel(learnSample(i), x)) // evaluate learn sample at each point of learn predict
      coefficients dot eval
    })
  }
}
