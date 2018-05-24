package regression

import breeze.linalg._

import rkhs.KerEval
import various.TypeDef._

object Predict {
  /**
   * The KerEval is a global KerEval which mixes learning and prediction observations.
   *
   * The first nLearn observations of the KerEval correspond to the learning set, the last observation to the prediction set.
   *
   * @return The regression function evaluated at each point in the prediction set.
   */
  def predict(ker: KerEval, beta: DenseVector[Real]): DenseVector[Real] =
    DenseVector.tabulate[Real](ker.nObsPredict)(i => {
      sum(DenseVector.tabulate[Real](ker.nObs)(j => beta(j) * ker.k(j, i)))
    })

  /**
   * Compute the evaluation of the minimizer function.
   */
  def evaluateMinimize[Data](
    predictSample: DenseVector[Data],
    coefficients: DenseVector[Real],
    learnSample: DenseVector[Data],
    kernel: (Data, Data) => Real): DenseVector[Real] = {
    predictSample.map({ x =>
      val eval = DenseVector.tabulate[Real](learnSample.length)(i => kernel(learnSample(i), x)) // evaluate learn sample at each point of learn predict
      coefficients dot eval
    })
  }
}
