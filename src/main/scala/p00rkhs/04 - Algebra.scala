package p00rkhs

import breeze.linalg._
import p04various.TypeDef._

/**
 * Definition of algebraic structures.
 */
object Algebra {
	/**
	 * The substraction is defined here to avoid having to pass it as an argument each time it is required. This is the case for the computation of
	 * ||x - y|| for example, when a distance is deduced from a norm.
	 */
	trait VectorSpace[T]{
		def -(x: T, y: T): T
	}

	implicit object SubReal extends VectorSpace[Real] {
		def -(x: Real, y: Real): Real = x - y
	}

	implicit object SubDenseMatrixReal extends VectorSpace[DenseMatrix[Real]] {
		def -(x: DenseMatrix[Real], y: DenseMatrix[Real]): DenseMatrix[Real] = x - y
	}
}