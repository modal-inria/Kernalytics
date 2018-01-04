import breeze.linalg._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._
import p00rkhs.{Algebra, KerEval, Kernel}
import p04various.TypeDef._
import p05offlinechangepoint.{Test}

/**
 * This class contains functional tests. This means that those tests check entire segmentations.
 */
class FunctionalSpec extends FlatSpec with Matchers {
  "normal" should "compute segmentation for a univariate float signal" in {
    val sampleLawDeterministic = Array[() => Real]( // TODO: use stochastic laws, once the setting of random seed is possible
        () => -10.0,
        () => 10.0,
        () => -10.0,
        () => 10.0)
        
    val sampleLawsStochastic = {
      val lawA = breeze.stats.distributions.Gaussian(10.0, 0.1)
      val lawB = breeze.stats.distributions.Gaussian(10.0, 1.0)
      
      Array[() => Real](
          () => lawA.sample,
          () => lawB.sample,
          () => lawA.sample,
          () => lawB.sample)
    }
        
    val kernel =
		  Kernel.Metric.gaussian(
		    _: Real,
		    _: Real,
		    Algebra.R.MetricSpace,
		    0.5)
		    
		val dMax = 8
		
		val nPoints = 1000
		
		val segPoints = Array(0, 250, 500, 750)
		
		val data = Test.generateData(sampleLawDeterministic, nPoints, segPoints)
		
		val kerEval =
		  KerEval.generateKerEval(
		      data,
		      kernel,
		      true)
		
		val seg =
		  Test.segment(
		      kerEval,
		      dMax,
		      nPoints,
		      segPoints)
		      
		 (segPoints) should === (seg)
  }
  
  "matrix" should "compute segmentation for a univariate float signal"  in {
	  val unitMat = DenseMatrix.ones[Real](3, 3)

		val sampleLawDeterministic = Array[() => DenseMatrix[Real]]( // TODO: use stochastic laws, once the setting of random seed is possible
				() => -10.0 *:* unitMat, // *:* is the element-wise product
				() => 10.0 *:* unitMat,
				() => -10.0 *:* unitMat,
				() => 10.0 *:* unitMat)
				
		val sampleLawStochastic = {
	    val muLaw = breeze.stats.distributions.Uniform(-10.0, 10.0)
	    val sdLaw = breeze.stats.distributions.Uniform(  0.1,  0.1)

	    def matLaw = DenseMatrix.tabulate[breeze.stats.distributions.Gaussian](3, 3)((i, j) => breeze.stats.distributions.Gaussian(muLaw.sample, sdLaw.sample))

	    Array
	    .fill(4)(matLaw)
	    .map(s => () => s.map(_.sample)) // for each segment, generate the function that sample every element of the corresponding matLaw
	  }
				
		val kernel =
		  Kernel.Metric.gaussian(
		    _: DenseMatrix[Real],
		    _: DenseMatrix[Real],
		    Algebra.DenseMatrixReal.MetricSpace,
		    0.5)
		    
		val dMax = 8
		
		val nPoints = 1000
		
		val segPoints = Array(0, 250, 500, 750)
		
		val data = Test.generateData(sampleLawDeterministic, nPoints, segPoints)
		
		val kerEval =
    		KerEval.generateKerEval(
    				data,
    				kernel,
    				true)
		
		val seg =
		  Test.segment(
		      kerEval,
		      dMax,
		      nPoints,
		      segPoints)
		      
		(segPoints) should === (seg)
  }
  
//	"multiKernel" should "perform a segmentation" in { // TODO: rewrite using Test.segment, like the other tests
//		val nPoints = 1000
//		val kernelSD = 1.0
//		val dMax = 8
//		val interPoint = DenseVector[Real](0.0, 2.5, 5.0, 7.5, 10.0)
//
//		val data = KernelManagement.DenseVectorReal(TestSegmentationNormal.expAndNormalData(nPoints, interPoint, baseDir))
//
//		val kerEval0 = KernelManagement.detectDenseVectorType(data, KernelManagement.ParameterGaussian(kernelSD)).get // TODO: manage None return
//		val kerEval1 = KernelManagement.detectDenseVectorType(data, KernelManagement.ParameterProduct ()        ).get
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