package p05offlinechangepoint

import breeze.linalg.{linspace, max, DenseVector, DenseMatrix}
import breeze.numerics._
import breeze.plot._
import p00rkhs.{Gram, Kernel}
import p04various.TypeDef._

/**
 * Simple data generation for the first tests of the algorithm.
 */
object TestNormalSignal {
  def expAndNormalData(nPoints: Index, interPoint: DenseVector[Real]): DenseVector[Real] = {
    val xVal = linspace(interPoint(0), interPoint(interPoint.size - 1), nPoints)
    
    val exportData = true

    val lawA = breeze.stats.distributions.Gaussian( 10.0, 0.1)
    val lawB = breeze.stats.distributions.Gaussian(-10.0, 0.1)
    
    val data = DenseVector.tabulate(nPoints)(i => {
    	val x = i.toDouble / (nPoints - 1).toDouble * 10.0
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
    		f.saveas("lines.png")
    }
    
    data
  }
  
  def expAndNormal {
	  val nPoints = 100
		val kernelSD = 1.0
		val dMax = 8
		val interPoint = DenseVector[Real](0.0, 2.5, 5.0, 7.5, 10.0)

		val data = expAndNormalData(nPoints, interPoint)
		val kernel = Kernel.R.gaussian(_: Real, _: Real, kernelSD)
    
		val kerEval = Gram.generateKerEval(data, kernel, false)
    
    val res = Segmentation.loopOverTauP(nPoints, kerEval, dMax)
    Segmentation.printAccumulator(res, "res")
    
    val bestPartition = Segmentation.bestPartition(res)
    Segmentation.printSegCost(bestPartition)
  }
  
  def compareCostMatrix {
    val nPoints = 10
    val kernelSD = 1.0
		val interPoint = DenseVector[Real](0.0, 2.5, 5.0, 7.5, 10.0)

		val data = expAndNormalData(nPoints, interPoint)
		
		def kerEval(i: Index, j: Index): Real = { // kerEval is defined here with a direct evaluation. An alternative would be to precompute the Gram matrix and then to access its elements. The function that generates a "cached" version of kerEval from a data set should be in p00rkhs
      Kernel.R.gaussian(data(i), data(j), kernelSD)
    }
		
		val costDirect = CostMatrix.completeCostMatrix(data, Kernel.R.gaussian(_: Real, _: Real, kernelSD))
		println("Cost with direct computation")
		println(costDirect)
		
		val costIterate = CostMatrix.completeMatrixViaColumn(nPoints, kerEval)
		println("Cost with iterated computation")
		println(costIterate)
		
		val maxRelativeError = max(abs(costIterate - costDirect))
		println(s"maxRelativeError: $maxRelativeError")
  }
}