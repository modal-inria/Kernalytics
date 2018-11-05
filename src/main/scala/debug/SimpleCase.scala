package debug

import breeze.linalg._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._
import rkhs.{ DataRoot, KerEvalGenerator, KerEval }
import various.TypeDef._
import algo.offlinechangepoint.SegmentationMutable
import algo.offlinechangepoint.examples.Test
import org.scalactic.source.Position.apply

object SimpleCase extends App {
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
  val kerEval = KerEvalGenerator.generateKernelFromParamData("Gaussian", "0.5", DataRoot.RealVal(data)).get
  val seg = SegmentationMutable.segment(kerEval, dMax, nPoints, None)

  println(seg.mkString(" "))
}