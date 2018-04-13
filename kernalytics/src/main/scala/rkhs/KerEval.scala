package rkhs

import breeze.linalg._
import various.TypeDef._

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

  case class DenseVectorMatrixReal(val data: DenseVector[DenseMatrix[Real]]) extends DataRoot {
    val typeName = "Matrix of Real"
    def nPoint: Index = data.size
  }

  class VarDescription(
    val weight: Real,
    val data: DataRoot,
    val param: String)

  /**
   * Generate the kerEval function from the data.
   * It differs from the kernel fonction in the sense that it is a function from a pair of indices to R. It corresponds
   * to the evaluation of the kernel on specific observations.
   * If gramCache argument is true, the Gram matrix will be computed and accessed.
   *
   * @param data data vector
   * @param kernel kernel function
   * @param gramCache
   */
  def generateKerEval[Data](
    data: DenseVector[Data],
    kernel: (Data, Data) => Real,
    gramCache: Boolean): (Index, Index) => Real =
    if (gramCache) {
      val gram = Gram.generate(data, kernel)
      (i, j) => gram(i, j)
    } else {
      (i, j) => kernel(data(i), data(j))
    }

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