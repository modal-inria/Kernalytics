package rkhs

import breeze.linalg._

import various.TypeDef._

/**
 * Definition of traits to encapsulate container types, and avoid type erasure in pattern matching (in function detectDenseVectorType for example).
 * Note that any type of containers could be used, not just DenseVector, because the data container is not specified in DataRoot, but in the derived
 * types.
 */
sealed trait DataRoot {
  val typeName: String
  def nPoint: Index
}

case class DenseVectorReal(val data: DenseVector[Real]) extends DataRoot {
  val typeName = "Real"
  def nPoint: Index = data.size
}

case class DenseVectorDenseVectorReal(val data: DenseVector[DenseVector[Real]]) extends DataRoot {
  val typeName = "VectorReal"
  def nPoint: Index = data.size
}

case class DenseVectorDenseMatrixReal(val data: DenseVector[DenseMatrix[Real]]) extends DataRoot {
  val typeName = "Matrix of Real"
  def nPoint: Index = data.size
}