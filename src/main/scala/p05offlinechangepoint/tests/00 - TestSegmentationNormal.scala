package p05offlinechangepoint.tests

import breeze.linalg.{csvwrite, linspace, DenseVector}
import breeze.numerics._
import breeze.plot._
import java.io.File
import p00rkhs.{KerEval, Kernel}
import p04various.TypeDef._
import p05offlinechangepoint.Segmentation

/**
 * Simple data generation for the first tests of the algorithm.
 */
object TestSegmentationNormal {
  val baseDir = "data/p05offlinechangepoint/00-TestSegmentationNormal"
  
  def expAndNormalData(
      nPoints: Index,
      interPoint: DenseVector[Real],
      baseDir: String): DenseVector[Real] = {
    val firstPoint = interPoint(0)
    val lastPoint = interPoint(interPoint.size - 1)
    
    val xVal = linspace(firstPoint, lastPoint, nPoints)
    
    val exportData = true

//    // different mean, same variance
//    val lawA = breeze.stats.distributions.Gaussian( 10.0, 0.1)
//    val lawB = breeze.stats.distributions.Gaussian(-10.0, 0.1)
    
    // same mean, different variance
    val lawA = breeze.stats.distributions.Gaussian(10.0, 0.1)
    val lawB = breeze.stats.distributions.Gaussian(10.0, 1.0)
    
    val data = DenseVector.tabulate(nPoints)(i => {
    	val x = i.toDouble / (nPoints - 1).toDouble * lastPoint // TODO: is there a way to replace toDouble with something that only depends on Real
    	x match {
    			case x if x <= interPoint(1)                       => lawA.sample()
    			case x if interPoint(1) <= x  && x < interPoint(2) => lawB.sample()
    			case x if interPoint(2) <= x  && x < interPoint(3) => lawA.sample()
    			case x if interPoint(3) <= x                       => lawB.sample()
    	}})
    
    if (exportData) {
    	  val f = Figure()
    	  val p = f.subplot(0)
    		p += plot(xVal, data)
    		p.xlabel = "time"
    		p.ylabel = "value"
    		f.saveas(baseDir + "/lines.png")
    		
    		csvwrite(new File(baseDir + "/data.csv"), data.asDenseMatrix.t)
    }
    
    return data
  }
  
  def main {
	  val nPoints = 1000
		val kernelSD = 0.5
		val dMax = 8
		val interPoint = DenseVector[Real](0.0, 2.5, 5.0, 7.5, 10.0)

		val data = expAndNormalData(nPoints, interPoint, baseDir)
		
		val kernel = Kernel.InnerProduct.gaussian(
		    _: Real,
		    _: Real,
		    Kernel.InnerProduct.R,
		    kernelSD)
		 
	  // same kernel as previous one except that the distance is deduced from the scala product.
//		val kernel = Kernel.Metric.gaussian(
//		    _: Real,
//		    _: Real,
//		    Kernel.Metric.InnerProductToMetric(Kernel.InnerProduct.R),
//		    kernelSD)
    
		val kerEval = KerEval.generateKerEval(data, kernel, false)
    
    val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
    Segmentation.printAccumulator(res, "res")
    
    val bestPartition = Segmentation.bestPartition(res)
    Segmentation.printSegCost(bestPartition)
  }
}