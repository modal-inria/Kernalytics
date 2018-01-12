package p05offlinechangepoint

import breeze.linalg._
import breeze.numerics._
import breeze.plot._

import p00rkhs.{KerEval}
import p04various.Def
import p04various.TypeDef._

/**
 * Runs similar to what is found in ScalaTest, except that outputs are generated, and stochastic data generation tends to be activated.
 */
object Demo {
  val baseDir = "data/p05offlinechangepoint/06 - Demo"
  
  def floatSignal = {
    val dMax = 8
    val nPoints = 1000
    val segPoints = Array(0, 250, 500, 750)
    
    val sampleLawsStochastic = {
      val lawA = breeze.stats.distributions.Gaussian(10.0, 0.1)
      val lawB = breeze.stats.distributions.Gaussian(10.0, 1.0)
      
      Array[() => Real](
          () => lawA.sample,
          () => lawB.sample,
          () => lawA.sample,
          () => lawB.sample)
    }
    
    val x = linspace(0.0, 1.0, nPoints)
    val data = Test.generateData(sampleLawsStochastic, nPoints, segPoints)
    
    val f = Figure()
    val p = f.subplot(0)
    p += plot(x, data)
    p.title = "Data"
    p.xlabel = "Time"
    p.ylabel = "Value"
    f.saveas(baseDir + Def.folderSep + "data.png")
    
    val kerEval = KerEval.paramToKerEval(KerEval.DenseVectorReal(data), KerEval.ParameterGaussian(0.5)).get
    val seg = Test.segment(kerEval, dMax, nPoints, true, baseDir)
  }
}