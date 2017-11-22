package p05offlinechangepoint

import breeze.linalg._
import breeze.plot._
import p04various.TypeDef._

/**
 * Simple data generation for the first tests of the algorithm.
 */
object DataGeneration {
  def expAndNormal {
    val nPoints = 100
    val mean = 10.0
    val xVal = linspace(0.0, 10.0, nPoints)
    val interPoint = DenseVector[Double](2.5, 5.0, 7.5)
    val exportData = true
    val kernelSD = 1.0
    val dMax = 8
    
    val lawA = breeze.stats.distributions.Gaussian(10.0, 0.1)
    val lawB = breeze.stats.distributions.Gaussian(-10.0, 0.1)
    
    val data = DenseVector.tabulate(nPoints)(i => {
    	val x = i.toDouble / (nPoints - 1).toDouble * 10.0
    	x match {
    			case x if x <= interPoint(0)                       => lawA.sample()
    			case x if interPoint(0) <= x  && x < interPoint(1) => lawB.sample()
    			case x if interPoint(1) <= x  && x < interPoint(2) => lawA.sample()
    			case x if interPoint(2) <= x                       => lawB.sample()
    	}})
    
    if (exportData) {
    	  val f = Figure()
    	  val p = f.subplot(0)
    		p += plot(xVal, data)
    		p.xlabel = "time"
    		p.ylabel = "value"
    		f.saveas("lines.png")
    }

    def kerEval(i: Index, j: Index): Real = { // kerEval is defined here with a direct evaluation. An alternative would be to precompute the Gram matrix and then to access its elements. The function that generates a "cached" version of kerEval from a data set should be in p00rkhs
      p00rkhs.Kernel.R.gaussian(data(i), data(j), kernelSD)
    }
    
    val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
    Segmentation.printAccumulator(res, "res")
    
    val bestPartition = Segmentation.bestPartition(res)
    Segmentation.printSegCost(bestPartition)
  }
}