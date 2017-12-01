package p05offlinechangepoint

import breeze.linalg.{csvwrite, linspace, max, DenseVector, DenseMatrix}
import breeze.numerics._
import breeze.plot._
import java.io.File
import p00rkhs.{Gram, KerEval, Kernel}
import p04various.TypeDef._
import p05offlinechangepoint.tests.TestSegmentationNormal

/**
 * Small tests to combine data types.
 */
object MultiKernel {
  sealed trait DenseVectorRoot // Definition of traits to encapsulate container types, and avoid type erasure in pattern matching (in function detectDenseVectorType for example)
  case class DenseVectorReal(val d: DenseVector[Real]) extends DenseVectorRoot
  
  sealed trait ParameterRoot
  case class ParameterGaussian(val sd: Real) extends ParameterRoot
  case class ParameterProduct() extends ParameterRoot
  
  def detectDenseVectorType(
      data: DenseVectorRoot,
      param: ParameterRoot)
  : Option[(Index, Index) => Real] = (data, param) match {
    case (DenseVectorReal(d), ParameterProduct()) => Some(KerEval.generateKerEval(
        d,
        Kernel.InnerProduct.R,
        true))
    case (DenseVectorReal(d), ParameterGaussian(sd)) => Some(KerEval.generateKerEval(
        d,
        Kernel.InnerProduct.gaussian(
        		_: Real,
        		_: Real,
        		Kernel.InnerProduct.R,
        		sd),
        true))
    case _ => None
  }
}