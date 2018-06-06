package rkhs

import breeze.linalg._
import scala.util.{ Try, Success, Failure }

import various.Def
import various.TypeDef._

/**
 * Rich container for kernel, instead of just having a function (Index, Index) => Real.
 * Subsequent access should always be performed using k instead of kerEval, as k uses the cache if it has been computed.
 * TODO: implement low rank approximation in this class.
 */
class KerEval(val nObsLearn: Index, val nObsPredict: Index, val f: (Index, Index) => Real, val cacheGram: Boolean) {
  val totalObs = nObsLearn + nObsPredict
  val nObs = totalObs // for legacy code compatibility

  val cacheMatrix = if (cacheGram)
    Some(DenseMatrix.tabulate[Real](totalObs, totalObs)((i, j) => f(i, j)))
  else
    None

  /**
   * Return the Gram matrix. If it has been computed in cache, return it, otherwise compute it completely.
   */
  def getK: DenseMatrix[Real] = cacheMatrix match {
    case Some(m) => m
    case None => DenseMatrix.tabulate[Real](totalObs, totalObs)((i, j) => f(i, j))
  }

  /**
   * Generate the k function. If there is a cache matrix, this would access the element, otherwise this would directly compute the result using f.
   * 
   * Never call f directly, because you would ignore if the user wanted to cache the Gram matrix content.
   */
  val k: (Index, Index) => Real = cacheMatrix match {
    case Some(m) => (i, j) => m(i, j)
    case None => (i, j) => f(i, j)
  }
}

object KerEval {
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