package rkhs

import breeze.linalg._
import scala.util.{Try, Success, Failure}

import various.TypeDef._

/**
 * Parsing for instanciation of kernels.
 */
object IO {  
   def generateKernel(nameStr: String, paramStr: String, data: KerEval.DataRoot): Try[(Index, Index) => Real] = data match {
     case KerEval.DenseVectorReal(data) if nameStr == "Linear" => {
      	 Success(KerEval.generateKerEval(
      			 data,
      			 Kernel.InnerProduct.linear(
      					 _: Real,
      					 _: Real,
      					 Algebra.R.InnerProductSpace),
      			 true))
     }
     
     case KerEval.DenseVectorReal(data) if nameStr == "Gaussian" => {
       Try(paramStr.toDouble)
       .map(sd => {
      	   KerEval.generateKerEval(
      					   data,
      					   Kernel.InnerProduct.gaussian(
      							   _: Real,
      							   _: Real,
      							   Algebra.R.InnerProductSpace,
      							   sd),
      					   true)
       })
     }
       
     case KerEval.DenseVectorMatrixReal(data) if nameStr == "Gaussian" => {
      	 Try(paramStr.toDouble)
      	 .map(sd => {
      		 KerEval.generateKerEval(
      				 data,
      				 Kernel.Metric.gaussian(
      						 _: DenseMatrix[Real],
      						 _: DenseMatrix[Real],
      						 Algebra.DenseMatrixReal.MetricSpace,
      						 sd),
      				 true)
      	 })
     }
     
     case _ => Failure(new Exception(nameStr + " kernel is not available for " + data.typeName + "data type."))
   }
}