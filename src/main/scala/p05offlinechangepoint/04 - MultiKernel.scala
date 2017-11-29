package p05offlinechangepoint

import breeze.linalg.{csvwrite, linspace, max, DenseVector, DenseMatrix}
import breeze.numerics._
import breeze.plot._
import java.io.File
import p00rkhs.{Gram, KerEval, Kernel}
import p04various.TypeDef._

/**
 * Small tests to combine kernels.
 * 
 * https://medium.com/@sinisalouc/overcoming-type-erasure-in-scala-8f2422070d20
 */
object MultiKernel {
  val sd = 0.1
  val baseDir = "data/p05offlinechangepoint/04-MultiKernel"
  
  def detectDenseVectorType(
      data: Array[Any], // DenseVector is invariant in the generic type, therefore it is not possible to use data: DenseVector[Any]
      pos: Index,
      kStr: String)
  : (Index, Index) => Real = kStr match {
    case "product" => KerEval.generateKerEval(
        data(pos).asInstanceOf[DenseVector[Real]], // TODO: asInstanceOf is unsafe. Is there a more elegant approach to this ?
        Kernel.R.product,
        true)
    case "gaussian" => KerEval.generateKerEval(
        data(pos).asInstanceOf[DenseVector[Real]],
        Kernel.R.gaussian(_: Real, _: Real, sd),
        true)
  }
  
  def main {
    	val nPoints = 1000
		val kernelSD = 1.0
		val dMax = 8
		val interPoint = DenseVector[Real](0.0, 2.5, 5.0, 7.5, 10.0)
		
		val data = TestNormalSignal.expAndNormalData(nPoints, interPoint, baseDir)
		
		val kerEval0 = detectDenseVectorType(Array(data), 0, "gaussian")
		val kerEval1 = detectDenseVectorType(Array(data), 0, "product")
		
		val kerEval = KerEval.linearCombKerEval(Array(kerEval0, kerEval1), Array(0.8, 0.2))
    
    val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
    Segmentation.printAccumulator(res, "res")
    
    val bestPartition = Segmentation.bestPartition(res)
    Segmentation.printSegCost(bestPartition)
  }
}