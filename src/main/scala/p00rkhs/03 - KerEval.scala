package p00rkhs

import breeze.linalg._
import p04various.TypeDef._

object KerEval {
    /**
   * Generate the kerEval function from the data.
   * It differs from the kernel fonction in the sense that it is a function from a pair of indices to R. It corresponds
   * to the evaluation of the kernel on specific observations.
   * If gramCache argument is true, the Gram matrix will be computed and accessed.
   * 
   * @param data data vector
   * @param kernel kernel function
   * @param gramCache */
  def generateKerEval[Data](
      data: DenseVector[Data],
      kernel: (Data, Data) => Real,
      gramCache: Boolean)
  : (Index, Index) => Real = if (gramCache) {
    val gram = Gram.generate(data, kernel)
    (i, j) => gram(i, j)
  }
  else {
    (i, j) => kernel(data(i), data(j))
  }
  
  def linearCombKerEval(kArray: Array[(Index, Index) => Real], weights: DenseVector[Real]): (Index, Index) => Real =
    (i, j) => {
      val evaluationResult = DenseVector.tabulate[Real](kArray.size)(i => kArray(i)(i, j)) // evaluate the various kernels TODO: parallel evaluation
      evaluationResult.dot(weights) // weight the results
    }
}