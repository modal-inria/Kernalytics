package p05offlinechangepoint

import breeze.linalg.{csvwrite, linspace, max, DenseVector, DenseMatrix}
import breeze.numerics._
import breeze.plot._
import java.io.File
import p00rkhs.{Gram, KerEval, Kernel}
import p04various.TypeDef._

/**
 * Small tests to combine data types.
 */
object MultiKernel {
  sealed trait DenseVectorRoot // Definition of traits to encapsulate container types, and avoid type erasure in pattern matching (in function detectDenseVectorType for example)
  case class DenseVectorReal(val d: DenseVector[Real]) extends DenseVectorRoot
  
  sealed trait ParameterRoot
  case class ParameterGaussian(val sd: Real) extends ParameterRoot
  case class ParameterProduct() extends ParameterRoot
  
  val baseDir = "data/p05offlinechangepoint/04-MultiKernel"
  
  def detectDenseVectorType( // TODO: this function should return an Option to take into consideration the case when parameters type do not match data type
      data: DenseVectorRoot,
      param: ParameterRoot)
  : (Index, Index) => Real = (data, param) match {
    case (DenseVectorReal(d), ParameterProduct()) => KerEval.generateKerEval(
        d,
        Kernel.Legacy.R.product,
        true)
    case (DenseVectorReal(d), ParameterGaussian(sd)) => KerEval.generateKerEval(
        d,
        Kernel.Legacy.R.gaussian(_: Real, _: Real, sd),
        true)
  }
  
  def main {
    val nPoints = 1000
		val kernelSD = 1.0
		val dMax = 8
		val interPoint = DenseVector[Real](0.0, 2.5, 5.0, 7.5, 10.0)
		
		val data = DenseVectorReal(TestNormalSignal.expAndNormalData(nPoints, interPoint, baseDir))
		
		val kerEval0 = detectDenseVectorType(data, ParameterGaussian(kernelSD))
		val kerEval1 = detectDenseVectorType(data, ParameterProduct ()        )
		
		val kerEval = KerEval.linearCombKerEval(Array(kerEval0, kerEval1), DenseVector[Real](0.5, 0.5))
    
    val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
    Segmentation.printAccumulator(res, "res")
    
    val bestPartition = Segmentation.bestPartition(res)
    Segmentation.printSegCost(bestPartition)
  }
}