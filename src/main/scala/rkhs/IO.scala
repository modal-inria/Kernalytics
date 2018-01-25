package rkhs

import breeze.linalg._
import scala.util.{Try, Success, Failure}

import various.TypeDef._

/**
 * Parsing for instantiation of kernels. As long as the number of combination of data and kernels is low, it is possible to maintain it by hand.
 * A more automated solution would have to be implemented later. This next solution could use the same interface with nameStr, paramStr and the data, though.
 */
object IO {
  /**
   * Parse the parameter string to extract both the kernel name 
   */
  def parseParam(str: String): Try[(String, String)] = {
    val paramPattern = raw"([a-zA-Z0-9]+)\((.+)\)".r
    val t = Try({val t = paramPattern.findAllIn(str); (t.group(1), t.group(2))})
    
    t match {
      case Success(_) => t
      case Failure(_) => Failure(new Exception(str + " is not a valid parameter String")) // default exception for pattern matching is not expressive enough
    }
  }

  /**
   * Generate a KerEval from a combination of parameter string and data.
   */
	def generateKernel(kernelNameStr: String, paramStr: String, data: KerEval.DataRoot): Try[(Index, Index) => Real] = data match {
    	case KerEval.DenseVectorReal(data) if kernelNameStr == "Linear" => {
    		Success(KerEval.generateKerEval(
    				data,
    				Kernel.InnerProduct.linear(
    						_: Real,
    						_: Real,
    						Algebra.R.InnerProductSpace),
    				true))
    	}
    
    	case KerEval.DenseVectorReal(data) if kernelNameStr == "Gaussian" => {
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
    
    	case KerEval.DenseVectorMatrixReal(data) if kernelNameStr == "Gaussian" => {
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
    
    	case _ => Failure(new Exception(kernelNameStr + " kernel is not available for " + data.typeName + "data type."))
	}
}