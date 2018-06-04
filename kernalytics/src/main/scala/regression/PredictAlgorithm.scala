package regression

import breeze.linalg._

import rkhs.KerEval
import various.TypeDef._

object PredictAlgorithm {
  /**
   * The KerEval is a global KerEval which mixes learning and prediction observations.
   *
   * The first nLearn observations of the KerEval correspond to the learning set, the last observation to the prediction set.
   *
   * @return The regression function evaluated at each point in the prediction set.
   */
  def predict(ker: KerEval, beta: DenseVector[Real]): DenseVector[Real] = {
//    println(s"nObsLearn: ${ker.nObsLearn}")
//    println(s"nObsPredict: ${ker.nObsPredict}")
//    println(ker.getK)
    DenseVector.tabulate[Real](ker.nObsPredict)(j => {
      val globalJ = j + ker.nObsLearn
      sum(DenseVector.tabulate[Real](ker.nObsLearn)(i => beta(i) * ker.k(i, globalJ)))
    })
  }
}
