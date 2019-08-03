package algo.svm

import breeze.linalg._

import rkhs.KerEval
import various.TypeDef._

object PredictAlgorithm {
  /**
   * Formula for prediction with kernels is u_i = \sum_{j = 1}^N y_j \alpha_j K(x_j, x_i) - b
   * 
   * @return the vector with predicted y
   */
  def predict(alpha: DenseVector[Real], b: Real, kerEval: KerEval, y: DenseVector[Real]): DenseVector[Real] = {
    val res = DenseVector.tabulate[Real](kerEval.nObsPredict)(i => {
      val absoluteI = i + kerEval.nObsLearn
      val components = DenseVector.tabulate[Real](kerEval.nObsLearn)(j => y(j) * alpha(j) * kerEval.k(j, absoluteI))
      math.signum(sum(components) - b)
    })
    
    res
  }
}