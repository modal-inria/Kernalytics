package rkhs

import breeze.linalg._
import scala.util.{ Try, Success, Failure }

import linalg.IncompleteCholesky
import various.Def
import various.TypeDef._

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
    val totalObs = nObsLearn + nObsPredict
    def k(i: Index, j: Index): Real = kerEvalFunc(i, j)
  }

  /** Gram matrix is computed and cached. kernel evaluation is coefficient evaluation. */
  class Cache(val nObsLearn: Index, val nObsPredict: Index, val kerEvalFunc: (Index, Index) => Real) extends KerEval {
    val totalObs = nObsLearn + nObsPredict
    val cache = DenseMatrix.tabulate[Real](totalObs, totalObs)(k)
    def k(i: Index, j: Index): Real = cache(i, j)
  }

  /** Low rank approximation is G is cached. Computation is obtained from the product G * G^t. */
  class LowRank(val nObsLearn: Index, val nObsPredict: Index, val kerEvalFunc: (Index, Index) => Real, val m: Index) extends KerEval {
    val totalObs = nObsLearn + nObsPredict
    val gt = (IncompleteCholesky.icd(totalObs, kerEvalFunc, m)).t
    def k(i: Index, j: Index): Real = gt(::, i).dot(gt(::, j)) // column slices are more efficient in column major storage, that is why the transpose of g is stored
  }

  /**
   * Definition of traits to encapsulate container types, and avoid type erasure in pattern matching (in function detectDenseVectorType for example).
   * Note that any type of containers could be used, not just DenseVector, because the data container is not specified in DataRoot, but in the derived
   * types.
   */
  sealed trait DataRoot {
    val typeName: String
    def nPoint: Index
  }

  case class DenseVectorReal(val data: DenseVector[Real]) extends DataRoot {
    val typeName = "Real"
    def nPoint: Index = data.size
  }

  case class DenseVectorDenseVectorReal(val data: DenseVector[DenseVector[Real]]) extends DataRoot {
    val typeName = "VectorReal"
    def nPoint: Index = data.size
  }

  case class DenseVectorDenseMatrixReal(val data: DenseVector[DenseMatrix[Real]]) extends DataRoot {
    val typeName = "Matrix of Real"
    def nPoint: Index = data.size
  }

  class KerEvalFuncDescription(
    val weight: Real,
    val data: DataRoot,
    val kernel: String,
    val param: String)

  /**
   * Generate the kerEval function from the data. The resulting function does not depend on the data type. Data is kept
   * in the closure.
   *
   * It differs from the kernel function in the sense that it is a function from a pair of indices to R. It corresponds
   * to the evaluation of the kernel on specific observations.
   * The advantage of this abstraction is that it does not depend on the data type, because the data is kept in the closure of
   * the function. KerEval is always (Index, Index) => Real.
   *
   * Note that this only builds a part of a KerEval. The KerEval also contains a cache, and potentially low rank approximation and such.
   *
   * @param data data vector
   * @param kernel kernel function
   * @param gramCache
   */
  def generateKerEvalFunc[Data](data: DenseVector[Data], kernel: (Data, Data) => Real): (Index, Index) => Real =
    (i, j) => kernel(data(i), data(j))

  def linearCombKerEvalFunc(kArray: Array[(Index, Index) => Real], weights: DenseVector[Real]): (Index, Index) => Real =
    (i, j) => {
      val evaluationResult = DenseVector.tabulate[Real](kArray.size)(k => kArray(k)(i, j)) // evaluate the various kernels funcs TODO: parallel evaluation
      evaluationResult.dot(weights) // weight the results
    }

  /**
   * Take data description, generate the individual kernels and compute linear combination to generate final kernel.
   */
  def multivariateKerEval(data: List[KerEvalFuncDescription]): Try[(Index, Index) => Real] = {
    val nVar = data.size
    val weights = DenseVector.tabulate[Real](nVar)(i => data(i).weight) // extract the weights for linearCombKerEvalFunc

    if (min(weights) < 0.0) return Failure(new Exception("Kernel coefficients must be positive."))

    return data
      .reverse
      .foldLeft[Try[List[(Index, Index) => Real]]](Success(Nil))((acc, e) =>
        acc.flatMap(l => KerEvalGenerator.generateKernelFromParamData(e.kernel, e.param, e.data).map(k => k :: l)))
      .map(kList => linearCombKerEvalFunc(kList.toArray, weights))
  }
}