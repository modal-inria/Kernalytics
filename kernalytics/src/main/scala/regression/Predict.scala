package regression

import breeze.linalg._

import rkhs.KerEval
import various.TypeDef._


object Predict {
  /**
   * The KerEval is a global KerEval mixing 
   * 
   * The first nLearn observations are the learning set, the last observation are the prediction set.
   */
  def predict(ker: KerEval) {
    ???
  }
  
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
