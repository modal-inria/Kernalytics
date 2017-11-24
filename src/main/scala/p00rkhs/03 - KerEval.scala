package p00rkhs

import breeze.linalg._
import p04various.TypeDef._

object KerEval {
    /**
   * Generate the kerEval function from the data. If gramCache argument is true, the Gram matrix will be computed and accessed.
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
  
  def linearCombKerEval(kArray: Array[(Index, Index) => Real], weights: Array[Real]): (Index, Index) => Real = {
    (i, j) => kArray
      .map(_(i, j)) // evaluate each fonction in the array
      .zip(weights) // associate the correct weight
      .map(p => p._1 * p._2) // product of evaluation with  weight
      .reduce(_ + _) // overall sum
  } 
}