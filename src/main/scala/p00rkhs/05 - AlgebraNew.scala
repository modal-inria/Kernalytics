package p00rkhs

import breeze.linalg._
import p04various.TypeDef._

object AlgebraNew {
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
  
  object R {
	  object InnerProductSpace extends InnerProductSpace[Real] {
		  def -(x: Real, y: Real): Real = x - y
				  def ip(x: Real, y: Real): Real = x * y
	  }
  }

  object DenseMatrixReal {
	  object InnerProductSpace extends InnerProductSpace[DenseMatrix[Real]] {
		  def -(x: DenseMatrix[Real], y: DenseMatrix[Real]): DenseMatrix[Real] = x - y
			def ip(x: DenseMatrix[Real], y: DenseMatrix[Real]): Real = trace(x.t * y) // this computes a lot of useless coefficients, hence the direct definition of the normed space below, using Frobenius norm from Breeze
	  }
	  
	  object NormedSpace extends NormedSpace[DenseMatrix[Real]] {
	    val ipSpace = InnerProductSpace
	    def norm(x: DenseMatrix[Real]) = norm(x)
	  }
	  
	  val MetricSpace = MetricSpaceFromNormedSpace(NormedSpace)
  }
}