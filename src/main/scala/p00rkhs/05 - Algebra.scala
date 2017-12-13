package p00rkhs

import breeze.linalg._
import p04various.Math
import p04various.TypeDef._

object Algebra {
	object Traits {
		trait InnerProductSpace[T] {
			def -(x: T, y: T): T
			def ip(x: T, y: T): Real
		}

		trait NormedSpace[T] {
			val ipSpace: InnerProductSpace[T]
					def norm(x: T): Real
		}

		trait MetricSpace[T] {
			def distance(x: T, y: T): Real
		}

		def NormedSpaceFromInnerProductSpace[T](v: InnerProductSpace[T]): NormedSpace[T] = {
				object NormedSpace extends NormedSpace[T] {
					val ipSpace = v
							def norm(x: T) = math.sqrt(v.ip(x, x))
				}

				return NormedSpace
		}

		def MetricSpaceFromNormedSpace[T](n: NormedSpace[T]): MetricSpace[T] = {
				object MetricSpace extends MetricSpace[T] {
					def distance(x: T, y: T) = n.norm(n.ipSpace.-(x, y))
				}

				return MetricSpace
		}

		def MetricSpaceFromInnerProductSpace[T](v: InnerProductSpace[T]): MetricSpace[T] =
				MetricSpaceFromNormedSpace(NormedSpaceFromInnerProductSpace(v))
	}

  object Distribution { // TODO: implement a normed space on distributions based on the ChiSquared function
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
  }
    
  object R {
	  object InnerProductSpace extends Traits.InnerProductSpace[Real] {
		  def -(x: Real, y: Real): Real = x - y
			def ip(x: Real, y: Real): Real = x * y
	  }
	  
	  val NormedSpace = Traits.NormedSpaceFromInnerProductSpace(InnerProductSpace)
	  
	  val MetricSpace = Traits.MetricSpaceFromInnerProductSpace(InnerProductSpace)
  }

  object DenseMatrixReal {
	  object InnerProductSpace extends Traits.InnerProductSpace[DenseMatrix[Real]] {
		  def -(x: DenseMatrix[Real], y: DenseMatrix[Real]): DenseMatrix[Real] = x - y
			def ip(x: DenseMatrix[Real], y: DenseMatrix[Real]): Real = Math.frobeniusInnerProduct(x, y) // because trace(x.t * y) computes a lot of useless coefficients (every non-diagonal terms)
	  }
	  
	  val NormedSpace = Traits.NormedSpaceFromInnerProductSpace(InnerProductSpace)
	  
	  val MetricSpace = Traits.MetricSpaceFromInnerProductSpace(InnerProductSpace)
  }
}