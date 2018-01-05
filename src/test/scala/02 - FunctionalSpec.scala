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
    val dMax = 8
    val nPoints = 1000
    val segPoints = Array(0, 250, 500, 750)
    
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

		val data = Test.generateData(sampleLawDeterministic, nPoints, segPoints)		
		val kerEval = KerEval.paramToKerEval(KerEval.DenseVectorReal(data), KerEval.ParameterGaussian(0.5)).get
		val seg = Test.segment(kerEval, dMax, nPoints, segPoints, false, "")
		      
		 (segPoints) should === (seg)
  }
  
  "matrix" should "compute segmentation for a univariate float signal"  in {
    val dMax = 8
    val nPoints = 1000
    val segPoints = Array(0, 250, 500, 750)
	  
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

		val data = Test.generateData(sampleLawDeterministic, nPoints, segPoints)		
		val kerEval = KerEval.paramToKerEval(KerEval.DenseVectorMatrixReal(data), KerEval.ParameterGaussian(0.5)).get
		val seg = Test.segment(kerEval, dMax, nPoints, segPoints, false, "")
		      
		(segPoints) should === (seg)
  }
  
	"multiKernel" should "perform a segmentation" in { // TODO: rewrite using Test.segment, like the other test
	  val dMax = 8
    val nPoints = 1000
    val segPoints = Array(0, 250, 500, 750)
    
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

		val data = Test.generateData(sampleLawDeterministic, nPoints, segPoints)		
		val kerEval0 = KerEval.paramToKerEval(KerEval.DenseVectorReal(data), KerEval.ParameterGaussian(0.5)).get
		val kerEval1 = KerEval.paramToKerEval(KerEval.DenseVectorReal(data), KerEval.ParameterProduct ()   ).get		
		val kerEval = KerEval.linearCombKerEval(Array(kerEval0, kerEval1), DenseVector[Real](0.5, 0.5))
		val seg = Test.segment(kerEval, dMax, nPoints, segPoints, false, "")
		
		(segPoints) should === (seg)
	}
	
	"multiVariable" should "perform a segmentation" in { // TODO: rewrite using Test.segment, like the other test
	  val dMax = 8
    val nPoints = 1000
    val segPoints = Array(0, 250, 500, 750)
    
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

		val data0 = Test.generateData(sampleLawDeterministic, nPoints, segPoints)
		val data1 = Test.generateData(sampleLawDeterministic, nPoints, segPoints)
		
		val varDescription =
		  Array(
		      new KerEval.VarDescription(0.5, KerEval.DenseVectorReal(data0), KerEval.ParameterGaussian(0.5)),
		      new KerEval.VarDescription(0.5, KerEval.DenseVectorReal(data1), KerEval.ParameterProduct ()   ))
		val kerEval = KerEval.multivariateKerEval(varDescription)
		val seg = Test.segment(kerEval, dMax, nPoints, segPoints, false, "")
		
		(segPoints) should === (seg)
	}
}