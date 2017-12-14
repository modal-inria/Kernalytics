package p05offlinechangepoint

import breeze.linalg._
import p00rkhs.{Algebra, KerEval, Kernel}
import p04various.TypeDef._
import scala.reflect.ClassTag

/**
 * Generic functions for easy testing of kernels.
 * 
 * @param dataGenerator one function per segment that 
 */
object Test {
  def segment[A: ClassTag]( // TODO: understand why ClassTag is needed
      sampleLaws: Array[() => A],
      kernel: (A, A) => Real,
      dMax: Index,
      nPoints: Index,
      segPoints: Array[Index])
   : Array[Index] = {
    val data =
      DenseVector.tabulate[A](nPoints)(i => {
        val seg = (segPoints.size - 1 to 0 by -1).find(segPoints(_) <= i).get
        sampleLaws(seg)()
      })
    
    val kerEval = KerEval.generateKerEval(data, kernel, false)
    
		val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
		Segmentation.printAccumulator(res, "res")

		val bestPartition = Segmentation.bestPartition(res)
		Segmentation.printSegCost(bestPartition)
		
		val costs = res.L.last.map(_.cost)
    val bestD = NumberSegmentSelection.optimalNumberSegments(costs, nPoints, false, "")
    
    return res
    .L
    .last(bestD)
    .seg
    .reverse
    .toArray
  }
  
  def testNormal {
    val sampleLawsDeterministic = Array[() => Real]( // TODO: use stochastic laws, once the setting of random seed is possible
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
		  segment(
		      sampleLawsStochastic,
		      kernel,
		      dMax,
		      nPoints,
		      segPoints)
		      
		println(seg.mkString(", "))
  }
  
  def testMatrix {
	  val unitMat = DenseMatrix.ones[Real](3, 3)

		val sampleLawDeterministic = Array[() => DenseMatrix[Real]]( // TODO: use stochastic laws, once the setting of random seed is possible
				() => -10.0 :* unitMat,
				() => 10.0 :* unitMat,
				() => -10.0 :* unitMat,
				() => 10.0 :* unitMat)
				
		val sampleLawStochastic = {
	    val muLaw = breeze.stats.distributions.Uniform(-10.0, 10.0)
	    val sdLaw = breeze.stats.distributions.Uniform(  0.1,  0.1)

	    def matLaw = DenseMatrix.tabulate[breeze.stats.distributions.Gaussian](3, 3)((i, j) => breeze.stats.distributions.Gaussian(muLaw.sample, sdLaw.sample))

	    Array
	    .fill(4)(matLaw)
	    .map(s => () => s.map(_.sample))
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
		  segment(
		      sampleLawStochastic,
		      kernel,
		      dMax,
		      nPoints,
		      segPoints)
		      
		println(seg.mkString(", "))
  }
}