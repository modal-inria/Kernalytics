package p00rkhs

import breeze.linalg._
import p04various.TypeDef._

object KerEval {
  sealed trait DenseVectorRoot { // Definition of traits to encapsulate container types, and avoid type erasure in pattern matching (in function detectDenseVectorType for example)
    def nPoint: Index
  } 
  case class DenseVectorReal(val data: DenseVector[Real]) extends DenseVectorRoot {
    def nPoint: Index = data.size
  }
  case class DenseVectorMatrixReal(val data: DenseVector[DenseMatrix[Real]]) extends DenseVectorRoot {
    def nPoint: Index = data.size
  }
  
  sealed trait ParameterRoot
  case class ParameterLinear()               extends ParameterRoot
  case class ParameterGaussian(val sd: Real) extends ParameterRoot
  
  class VarDescription(
      val weight: Real, 
      val data: DenseVectorRoot,
      val param: ParameterRoot)
  
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
  : (Index, Index) => Real =
    if (gramCache) {
      val gram = Gram.generate(data, kernel)
    	  (i, j) => gram(i, j)
    }
    else {
      (i, j) => kernel(data(i), data(j))
    }
  
  def linearCombKerEval(kArray: Array[(Index, Index) => Real], weights: DenseVector[Real]): (Index, Index) => Real =
    (i, j) => {
      val evaluationResult = DenseVector.tabulate[Real](kArray.size)(k => kArray(k)(i, j)) // evaluate the various kernels TODO: parallel evaluation
      evaluationResult.dot(weights) // weight the results
    }
    
  /**
   * Generate a KerEval using data and pattern matching on the parameters.
   */
  def paramToKerEval(
      data: DenseVectorRoot,
      param: ParameterRoot)
  : Option[(Index, Index) => Real] = // TODO: return a Try instead of an Option, as it would mix better with the rest of the exception handling
    (data, param) match {
      case (DenseVectorReal(data), ParameterLinear()) =>
        Some(KerEval.generateKerEval(
      		  data,
      		  Kernel.InnerProduct.linear(
      				  _: Real,
      				  _: Real,
      				  Algebra.R.InnerProductSpace),
      		  true))
      		  
      case (DenseVectorReal(data), ParameterGaussian(sd)) =>
        Some(KerEval.generateKerEval(
      		  data,
      		  Kernel.InnerProduct.gaussian(
      				  _: Real,
      				  _: Real,
      				  Algebra.R.InnerProductSpace,
      				  sd),
      		  true))
      	
      case (DenseVectorMatrixReal(data), ParameterGaussian(sd)) =>
        Some(KerEval.generateKerEval(
      		  data,
      		  Kernel.Metric.gaussian(
      				  _: DenseMatrix[Real],
      				  _: DenseMatrix[Real],
      				  Algebra.DenseMatrixReal.MetricSpace,
      				  sd),
      		  true))
      		  
      case _ => None
  }
  
  /**
   * Take data description and generate the corresponding KerEval.
   */
  def multivariateKerEval(data: Array[VarDescription]): (Index, Index) => Real = {
    val nVar = data.size
    val weights = DenseVector.tabulate[Real](nVar)(i => data(i).weight)
    
    val kArray = data.map(v => KerEval.paramToKerEval(v.data, v.param).get)
    
    return KerEval.linearCombKerEval(kArray, weights)
  }
}