package rkhs

import breeze.linalg._
import various.TypeDef._

/**
 * Rich container for kernel, instead of just having a function (Index, Index) => Real.
 * Subsequent access should always be performed using k instead of kerEval, as k uses the cache if it has been computed.
 * TODO: implement low rank approximation in this class.
 */
class KerEval(val nObs: Index, val kerEval: (Index, Index) => Real, val cacheGram: Boolean) {
  val cacheMatrix = if (cacheGram)
    Some(DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => kerEval(i, j)))
  else
    None

  def getK: DenseMatrix[Real] = cacheMatrix match {
    case Some(m) => m
    case None => DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => kerEval(i, j))
  }

  val k: (Index, Index) => Real = cacheMatrix match {
    case Some(m) => (i, j) => m(i, j)
    case None => (i, j) => kerEval(i, j)
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

  class VarDescription(
    val weight: Real,
    val data: DataRoot,
    val param: String)

  /**
   * Generate the kerEval function from the data.
   * It differs from the kernel function in the sense that it is a function from a pair of indices to R. It corresponds
   * to the evaluation of the kernel on specific observations.
   * The advantage of this abstraction is that it does not depend on the data type, because the data is kept in the closure of
   * the function. KerEval is always (Index, Index) => Real.
   *
   * @param data data vector
   * @param kernel kernel function
   * @param gramCache
   */
  def generateKerEval[Data](
    data: DenseVector[Data],
    kernel: (Data, Data) => Real): (Index, Index) => Real =
    (i, j) => kernel(data(i), data(j))

  def linearCombKerEval(kArray: Array[(Index, Index) => Real], weights: DenseVector[Real]): (Index, Index) => Real =
    (i, j) => {
      val evaluationResult = DenseVector.tabulate[Real](kArray.size)(k => kArray(k)(i, j)) // evaluate the various kernels TODO: parallel evaluation
      evaluationResult.dot(weights) // weight the results
    }

  /**
   * Take data description, generate the individual kernels and compute linear combination to generate final kernel.
   */
  def multivariateKerEval(data: Array[VarDescription]): (Index, Index) => Real = {
    val nVar = data.size
    val weights = DenseVector.tabulate[Real](nVar)(i => data(i).weight)

    val kArray = data.map(v => IO.parseParamAndGenerateKernel(v.data, v.param).get)

    return KerEval.linearCombKerEval(kArray, weights)
  }
}