package offlinechangepoint

import breeze.linalg._
import breeze.numerics._
import breeze.plot._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.util.{ Try, Success, Failure }

import rkhs.{ KerEval }
import various.Def
import various.TypeDef._
import io.{ CombineVarParam, ReadVar, ReadParam, Write }

/**
 * Demo with data input / output from the hard drive.
 */
object DemoIO {
  val baseDir = "data/offlinechangepoint/DemoIO"
  val dataFile = baseDir + Def.folderSep + "data.csv"
  val descriptorFile = baseDir + Def.folderSep + "descriptor.csv"
  val dMax = 8

  def generateRealData(
    name: String,
    nPoints: Index,
    segPoints: Array[Index],
    sampleA: () => Real,
    sampleB: () => Real): Array[String] = {
    val sampleLawsStochastic = {
      Array[() => Real](
        sampleA,
        sampleB,
        sampleA,
        sampleB)
    }

    val x = linspace(0.0, 1.0, nPoints)
    val data = Test.generateData(sampleLawsStochastic, nPoints, segPoints)

    val f = Figure()
    val p = f.subplot(0)
    p += plot(x, data)
    p.title = name
    p.xlabel = "Time"
    p.ylabel = "Value"
    f.saveas(baseDir + Def.folderSep + name + ".png")

    return Array(name, "Real") ++ data.map(_.toString).toArray
  }

  def generateData = {
    val dataA = generateRealData(
      "GaussA",
      1000,
      Array(0, 250, 500, 750),
      breeze.stats.distributions.Gaussian(10.0, 0.1).sample,
      breeze.stats.distributions.Gaussian(7.0, 1.0).sample)

    val dataB = generateRealData(
      "GaussB",
      1000,
      Array(0, 250, 500, 750),
      breeze.stats.distributions.Gaussian(-3.0, 1.0).sample,
      breeze.stats.distributions.Gaussian(5.0, 1.0).sample)

    val allData =
      Array(dataA, dataB)
        .transpose
        .map(_.mkString(Def.csvSep))
        .mkString(Def.eol)

    FileUtils.writeStringToFile(new File(dataFile), allData, "UTF-8")
  }

  def generateParam = {
    val kerA = Array("GaussA", "0.25", "Gaussian(1.0)")
    val kerB = Array("GaussA", "0.25", "Linear")
    val kerC = Array("GaussB", "0.5", "Gaussian(0.7)")

    val str =
      Array(kerA, kerB, kerC)
        .transpose
        .map(_.mkString(Def.csvSep))
        .mkString(Def.eol)

    FileUtils.writeStringToFile(new File(descriptorFile), str, "UTF-8")
  }
}