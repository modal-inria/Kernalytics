package p00rkhs

import breeze.linalg._
import p04various.TypeDef._

object Predict {
  /**
   * Compute the evaluation of the minimizer function.
   * */
  def evaluateMinimize[Data](
      predictSample: DenseVector[Data],
      coefficients: DenseVector[Real],
      learnSample: DenseVector[Data],
      kernel: (Data, Data) => Real)
  : DenseVector[Real] = {
    predictSample.map({x =>
      val eval = DenseVector.tabulate[Real](learnSample.length)(i => kernel(learnSample(i), x)) // evaluate learn sample at each point of learn predict
      coefficients dot eval
    })
  }
}
