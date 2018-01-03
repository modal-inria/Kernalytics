import breeze.linalg._
import breeze.stats.distributions._
import collection.mutable.Stack
import org.scalactic._
import org.scalatest._

import p00rkhs.{Algebra, Kernel}
import p04various.TypeDef._
import p05offlinechangepoint.Test

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
		
		val seg =
		  Test.segment(
		      sampleLawDeterministic,
		      kernel,
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
		
		val seg =
		  Test.segment(
		      sampleLawDeterministic,
		      kernel,
		      dMax,
		      nPoints,
		      segPoints)
		      
		(segPoints) should === (seg)
  }
}