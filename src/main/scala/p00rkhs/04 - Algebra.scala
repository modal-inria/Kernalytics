package p00rkhs

import breeze.linalg._
import p04various.TypeDef._

/**
 * Definition of algebraic structures as traits and objects to be used as implicit parameters.
 */
object Algebra {
	/**
	 * This should implement all the operations between vectors and scalars. However it is not necessary to implement everything at once.
	 * New operations could be implemented when needed. For example the substraction was necessary to get a distance from a norm (as ||x - y||), hence the early implementation.
	 */
	trait VectorSpace[T]{ // TODO: should be templated on both the vector and scalar spaces. Here the scalar space is always implied and unique, generally R.
		def -(x: T, y: T): T
	}

	implicit object VectorSpaceReal extends VectorSpace[Real] {
		def -(x: Real, y: Real): Real = x - y
	}

	implicit object VectorSpaceDenseMatrixReal extends VectorSpace[DenseMatrix[Real]] {
		def -(x: DenseMatrix[Real], y: DenseMatrix[Real]): DenseMatrix[Real] = x - y
	}
}