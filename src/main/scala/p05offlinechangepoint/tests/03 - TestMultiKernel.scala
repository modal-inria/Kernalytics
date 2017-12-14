package p05offlinechangepoint.tests

import breeze.linalg.{csvwrite, linspace, max, DenseVector, DenseMatrix}
import breeze.numerics._
import breeze.plot._
import java.io.File
import p00rkhs.{Gram, KerEval, Kernel}
import p04various.TypeDef._
import p05offlinechangepoint.{Segmentation, MultiKernel}

object TestMultiKernel {
//  val baseDir = "data/p05offlinechangepoint/tests/03 - TestMultiKernel"
//  
//	def main {
//		val nPoints = 1000
//		val kernelSD = 1.0
//		val dMax = 8
//		val interPoint = DenseVector[Real](0.0, 2.5, 5.0, 7.5, 10.0)
//
//		val data = MultiKernel.DenseVectorReal(TestSegmentationNormal.expAndNormalData(nPoints, interPoint, baseDir))
//
//		val kerEval0 = MultiKernel.detectDenseVectorType(data, MultiKernel.ParameterGaussian(kernelSD)).get // TODO: manage None return
//		val kerEval1 = MultiKernel.detectDenseVectorType(data, MultiKernel.ParameterProduct ()        ).get
//
//		val kerEval = KerEval.linearCombKerEval(Array(kerEval0, kerEval1), DenseVector[Real](0.5, 0.5))
//
//		val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
//		Segmentation.printAccumulator(res, "res")
//
//		val bestPartition = Segmentation.bestPartition(res)
//		Segmentation.printSegCost(bestPartition)
//	}
}
