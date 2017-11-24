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
  sealed trait DenseVectorRoot
  case class DenseVectorReal(val d: DenseVector[Real]) extends DenseVectorRoot
  
  val sd = 1.0
  val baseDir = "data/p05offlinechangepoint/04-MultiKernel"
  
  def detectDenseVectorType(
      data: Array[DenseVectorRoot],
      pos: Index,
      kStr: String)
  : (Index, Index) => Real = data(pos) match {
    case DenseVectorReal(d) if kStr == "product" => KerEval.generateKerEval(
        d,
        Kernel.R.product,
        true)
    case DenseVectorReal(d) if kStr == "gaussian" => KerEval.generateKerEval(
        d,
        Kernel.R.gaussian(_: Real, _: Real, sd),
        true)
  }
  
  def main {
    val nPoints = 1000
		val kernelSD = 1.0
		val dMax = 8
		val interPoint = DenseVector[Real](0.0, 2.5, 5.0, 7.5, 10.0)
		
		val data = TestNormalSignal.expAndNormalData(nPoints, interPoint, baseDir)
		
		val kerEval0 = detectDenseVectorType(Array(DenseVectorReal(data)), 0, "gaussian")
		val kerEval1 = detectDenseVectorType(Array(DenseVectorReal(data)), 0, "product")
		
		val kerEval = KerEval.linearCombKerEval(Array(kerEval0, kerEval1), DenseVector[Real](0.5, 0.5))
    
    val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
    Segmentation.printAccumulator(res, "res")
    
    val bestPartition = Segmentation.bestPartition(res)
    Segmentation.printSegCost(bestPartition)
  }
}