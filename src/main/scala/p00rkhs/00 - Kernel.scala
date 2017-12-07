package p00rkhs

import breeze.linalg._
import p04various.TypeDef._

/**
 * This is the only point in the algorithm where the data types have to be known. Beyond that, only the kernel is evaluated, usually via KerEval.
 * 
 * Bibliography:
 * - https://en.wikipedia.org/wiki/Reproducing_kernel_Hilbert_space#Examples
 * - https://en.wikipedia.org/wiki/Positive-definite_kernel , for the notations used in the kernels
 */
object Kernel {
	object InnerProduct {
	  def R(x: Real, y: Real): Real = x * y
	  def Rn(x: DenseVector[Real], y: DenseVector[Real]): Real = x dot y
	  
	  /**
	   * A useless example because the inner product could be called directly instead.
	   */
	  def linear[Data](
	      x: Data,
	      y: Data,
	      innerProduct: (Data, Data) => Real)
	  : Real = innerProduct(x, y)
	  
	  def polynomial[Data](
	      x: Data,
	      y: Data,
	      innerProduct: (Data, Data) => Real,
	      c: Real,
	      d: Integer)
	  : Real = math.pow(innerProduct(x, y) + c, d)
	  
	  /**
	   * Same as Metric.gaussian, except that ((_)^(1/2))^(1/2) is removed as it is useless and time-consuming.
	   */
	  def gaussian[Data](
		    x: Data,
		    y: Data,
		    innerProduct: (Data, Data) => Real,
		    sd: Real)(implicit sub: Algebra.VectorSpace[Data])
		: Real = {val diff = sub.-(x, y); math.exp(- innerProduct(diff, diff) / (2.0 * math.pow(sd, 2.0)))}
	}
	
	object Norm {
	  /**
	   * Generate the norm deduced from an inner product as the function: x -> $\sqrt{<x, x>}$.
	   */
	  def InnerProductToNorm[Data](ip: (Data, Data) => Real): Data => Real =
	    x => math.sqrt(ip(x, x))
	}
	
	object Metric {
	  	/**
	   * Generate the metric deduced from a norm as the function: (x, y) -> ||x - y||. Note that for convenience the - operation
	   * does not need to be provided and is deduced using implicits.
	   */
	  def NormToMetric[Data](norm: Data => Real)(implicit sub: Algebra.VectorSpace[Data]): (Data, Data) => Real = // TODO: requirement here is to have - defined, not to be a vector space. The type system could be strenghtened to fully manage algebraic structures.
	    (x, y) => norm(sub.-(x, y)) // ||x - y||
	  
	  	/**
	   * Compute the metric derived from an inner product, (x, y) -> $$||x - y||
	   */
		def InnerProductToMetric[Data](ip: (Data, Data) => Real)(implicit sub: Algebra.VectorSpace[Data]): (Data, Data) => Real = 
//		  (x, y) => {val diff = numeric.minus(x, y); math.sqrt(ip(diff, diff))}
		  NormToMetric[Data](Norm.InnerProductToNorm[Data](ip))(sub)
	  
	  /**
	   * Must be used as a metric for the laplacian kernel to get the ChiSquared metric.
	   * 
	   * TODO: implement other squared norm based kernels, like Jensen divergence or Total Variation.
	   */
	  	def ChiSquared(x: DenseVector[Real], y: DenseVector[Real]): Real = {
	    val elements = DenseVector.tabulate(x.size)(i => {
	      math.pow(x(i) - y(i), 2.0) / (x(i) + y(i))
	    })
	    
	    return sum(elements)
	  }
		  
		def gaussian[Data](
		    x: Data,
		    y: Data,
		    metric: (Data, Data) => Real,
		    sd: Real)
		: Real = math.exp(- math.pow(metric(x, y), 2.0) / (2.0 * math.pow(sd, 2.0)))
		
	  /**
	   * Note the condition alpha > 0.
	   */
	  def laplacian[Data](
	      x: Data,
	      y: Data,
	      metric: (Data, Data) => Real,
	      alpha: Real)(implicit numeric: Numeric[Data])
	  : Real = math.exp(- alpha * metric(x, y))
	}

  /**
   * Previous kernel system, not based on algebraic structures. Should not be used anymore.
   */
	@deprecated("Use algebra-based kernels instead.")
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