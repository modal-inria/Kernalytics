package p00rkhs

import breeze.linalg._
import p04various.TypeDef._
import Numeric._

/**
 * Note that only here are the data types known. In the Gram package, everything occurs in the RKHS, and the data types need to to be nown since
 * only the kernel evaluations are required.
 * Bibliography:
 * -	https://en.wikipedia.org/wiki/Reproducing_kernel_Hilbert_space#Examples
 */
object Kernel {
	object InnerProduct {
	  def R(x: Real, y: Real): Real = x * y
	  def Rn(x: DenseVector[Real], y: DenseVector[Real]): Real = x dot y
	  
	  def linear[Data](
	      x: Data,
	      y: Data,
	      innerProduct: (Data, Data) => Real)
	  : Real = innerProduct(x, y)
	}
	
	object Metric {
	  /**
	   * Compute the metric derived from an inner product, as the norm of the difference of the vectors.
	   */
		def InnerProductToMetric[Data](ip: (Data, Data) => Real)(implicit numeric: Numeric[Data]): (Data, Data) => Real = // TODO: requirement here is to have - defined, not to be a vector space
		  (x, y) => {val diff = numeric.minus(x, y); math.sqrt(ip(diff, diff))}
		  
		def gaussian[Data](
		    x: Data,
		    y: Data,
		    metric: (Data, Data) => Real,
		    sd: Real)
		: Real = math.exp(- math.pow(metric(x, y), 2.0) / (2.0 * math.pow(sd, 2.0))) // TODO: for metrics derived from inner product, it is suboptimal to compute the sqrt in InnerProductToMetric and then square it here. Actually the optimized version should be part of InnerProduct, and the generic version be here.
	}

  /**
   * Previous kernel system, not based on algebraic structures.
   */
	object Legacy {
		object R {
			def product(x: Real, y: Real): Real = x * y
					def gaussian(x: Real, y: Real, sd: Real): Real = math.exp(- math.pow(x - y, 2.0) / (2.0 * math.pow(sd, 2.0)))
		}

		object Rn {
			def linear(x: DenseVector[Real], y: DenseVector[Real]): Real = x dot y
		}
	}
}