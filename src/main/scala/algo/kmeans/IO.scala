package algo.kmeans

import breeze.linalg._
import rkhs.KerEval
import various.{ Iterate }
import various.TypeDef._

object IO {
  class ReturnValue(val model: DenseMatrix[Real], val labels: DenseVector[Index])

  def runKMeans(kerEval: KerEval, nClass: Index, nIteration: Index): ReturnValue = {
    val gram = kerEval.getK

    val param = Base.init(kerEval.nObs, nClass) // initialize the algorithm by selecting a representative element per class and setting the class centers using them
    val zeroCompState = new Base.ComputationState(
      0,
      gram,
      param,
      DenseMatrix.zeros[Double](kerEval.nObs, nClass))
    val initCompState = Base.eStep(zeroCompState) // an e step is performed, so that the ComputationState is valid

    val res = Iterate.iterate( // launch the real computation, which alternates E and M steps, updating the computation state
      initCompState,
      Base.emIteration,
      (s: Base.ComputationState) => s.nIteration == nIteration)

    val ziComputed = DenseVector.tabulate[Int](kerEval.nObs)(i => argmax(res.zik(i, ::)))
    val coeff = res.param

    return new ReturnValue(coeff, ziComputed)
  }

}