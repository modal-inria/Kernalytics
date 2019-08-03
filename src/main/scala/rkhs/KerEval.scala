package rkhs

import breeze.linalg._
import various.TypeDef._

import scala.util.{Failure, Success, Try}

sealed trait KerEval {
  val nObs: Index = nObsLearn
  val nObsLearn: Index
  val nObsPredict: Index
  val totalObs: Index
  def getK: DenseMatrix[Real] = DenseMatrix.tabulate[Real](totalObs, totalObs)(k)
  def k(i: Index, j: Index): Real
}

object KerEval {
  /** No cache nor optimization, each value is computed directly when needed. */
  class Direct(val nObsLearn: Index, val nObsPredict: Index, val kerEvalFunc: (Index, Index) => Real) extends KerEval {
    val totalObs: Index = nObsLearn + nObsPredict
    def k(i: Index, j: Index): Real = kerEvalFunc(i, j)
  }

  /** Gram matrix is computed and cached. kernel evaluation is coefficient evaluation. */
  class Cache(val nObsLearn: Index, val nObsPredict: Index, val kerEvalFunc: (Index, Index) => Real) extends KerEval {
    val totalObs: Index = nObsLearn + nObsPredict
    val cache: DenseMatrix[Real] = DenseMatrix.tabulate[Real](totalObs, totalObs)(kerEvalFunc)
    def k(i: Index, j: Index): Real = cache(i, j)
  }

  /** Low rank approximation is G is cached. Computation is obtained from the product G * G^t */
  class LowRank(val nObsLearn: Index, val nObsPredict: Index, val kerEvalFunc: (Index, Index) => Real, val gt: DenseMatrix[Real], val m: Index) extends KerEval {
    val totalObs: Index = nObsLearn + nObsPredict
    def k(i: Index, j: Index): Real = gt(::, i).dot(gt(::, j)) // column slices are more efficient in column major storage, that is why the transpose of g is stored
  }

  /** This class is used to provide an individual description for each kernel component in a multivariable / multikernel setting. */
  case class KerEvalFuncDescription(weight: Real, data: DataRoot, kernel: String, param: String)

  /**
   * Generate the kerEval function from the data. The resulting function does not depend on the data type. Data is kept
   * in the closure.
   *
   * It differs from the kernel function in the sense that it is a function from a pair of indices to R. The kernel function is from
   * the data space to R instead. It corresponds to the evaluation of the kernel on observations only. The advantage of this abstraction
   * is that it does not depend on the data type, because the data is kept in the closure of the function.
   * The generated function is always is thus always of type (Index, Index) => Real.
   *
   * Note that this only builds a part of a KerEval. The KerEval also contains a cache, and potentially low rank approximation and such.
   *
   * @param data data vector
   * @param kernel kernel function
   * @return function that evaluates the kernel, hiding reference to the underlying Data type
   */
  def generateKerEvalFunc[Data](data: DenseVector[Data], kernel: (Data, Data) => Real): (Index, Index) => Real = {
    (i, j) => kernel(data(i), data(j))
  }

  def linearCombKerEvalFunc(kArray: Array[(Index, Index) => Real], weights: DenseVector[Real]): (Index, Index) => Real =
    (i, j) => {
      val evaluationResult = DenseVector.tabulate[Real](kArray.length)(k => kArray(k)(i, j)) // evaluate the various kernels funcs TODO: parallel evaluation
      evaluationResult.dot(weights) // weight the results
    }

  /**
   * Take data description, generate the individual kernels and compute linear combination to generate final kernel.
   */
  def multivariateKerEval(data: List[KerEvalFuncDescription]): Try[(Index, Index) => Real] = {
    val nVar = data.size
    val weights = DenseVector.tabulate[Real](nVar)(i => data(i).weight) // extract the weights for linearCombKerEvalFunc

    if (min(weights) < 0.0) return Failure(new Exception("Kernel coefficients must be positive."))

    data
      .reverse
      .foldLeft[Try[List[(Index, Index) => Real]]](Success(Nil))((acc, e) =>
        acc.flatMap(l => KerEvalGenerator.generateKernelFromParamData(e.kernel, e.param, e.data).map(k => k :: l)))
      .map(kList => linearCombKerEvalFunc(kList.toArray, weights))
  }
}