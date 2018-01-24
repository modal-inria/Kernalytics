package rkhs

import breeze.linalg._
import scala.util.{Try, Success, Failure}

import various.TypeDef._

/**
 * Parsing for instanciation of kernels.
 */
object IO {
  class IOKernel(
      val name: String,
      val paramConv: (String, KerEval.DenseVectorRoot) => Try[(Index, Index) => Real])
   
   val Linear = new IOKernel(
       "Linear",
       (paramStr, dv) => {
         dv match {
           case KerEval.DenseVectorReal(data) => {
             Success(KerEval.generateKerEval(
            		 data,
            		 Kernel.InnerProduct.linear(
            				 _: Real,
            				 _: Real,
            				 Algebra.R.InnerProductSpace),
            		 true))
           }
           
           case _ => Failure(new Exception("Linear kernel is not available for " + dv.typeName))
         }
       })
      
   val Gaussian = new IOKernel(
       "Gaussian",
       (paramStr, dv) => {
         Try(paramStr.toDouble)
         .flatMap(sd => {
           dv match {
             case KerEval.DenseVectorReal(data) => 
               Success(
                   KerEval.generateKerEval(
                		   data,
                		   Kernel.InnerProduct.gaussian(
                				   _: Real,
                				   _: Real,
                				   Algebra.R.InnerProductSpace,
                				   sd),
                		   true))
              		 
             case KerEval.DenseVectorMatrixReal(data) =>
               Success(
            		   KerEval.generateKerEval(
            				   data,
            				   Kernel.Metric.gaussian(
            						   _: DenseMatrix[Real],
            						   _: DenseMatrix[Real],
            						   Algebra.DenseMatrixReal.MetricSpace,
            						   sd),
            				   true))
            				   
             case _ => Failure(new Exception("Gaussian kernel is not available for " + dv.typeName))
           }
         })
       })
      
   /** To be available, a Kernel must be included in this Array. */
   val registeredKernels: Array[IOKernel] = Array(Linear, Gaussian)
}