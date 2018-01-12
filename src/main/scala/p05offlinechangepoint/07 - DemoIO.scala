package p05offlinechangepoint

import breeze.linalg._
import breeze.numerics._
import breeze.plot._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.util.{Try, Success, Failure}

import p00rkhs.{KerEval}
import p04various.Def
import p04various.TypeDef._
import p07io.{CombineVarParam, ReadVar, ReadParam, Write}

/**
 * Demo with data input / output from the hard drive.
 */
object DemoIO {
  val baseDir = "data/p05offlinechangepoint/07 - DemoIO"
  val dataFile = baseDir + Def.folderSep + "data.csv"
  val descriptorFile = baseDir + Def.folderSep + "descriptor.csv"
  
  def generateData = {
    val nPoints = 1000
    val segPoints = Array(0, 250, 500, 750)
    
    val sampleLawsStochastic = {
      val lawA = breeze.stats.distributions.Gaussian(10.0, 0.1)
      val lawB = breeze.stats.distributions.Gaussian(7.0, 1.0)
      
      Array[() => Real](
          () => lawA.sample,
          () => lawB.sample,
          () => lawA.sample,
          () => lawB.sample)
    }
    
    val x = linspace(0.0, 1.0, nPoints)
    val data = Test.generateData(sampleLawsStochastic, nPoints, segPoints)
    
    Write.csv(dataFile, "Gaussian", data)
  }
  
  def generateParam = {
    val str = Array("Gaussian", "1.0", "Gaussian(1.0)").mkString(Def.eol)
    FileUtils.writeStringToFile(new File(descriptorFile), str, "UTF-8")
  }
  
  def segmentData = {
    val dMax = 8

    val kerEval =
      for {
        data <- ReadVar.readAndParseVars(dataFile)
        param <- ReadParam.readAndParseParam(descriptorFile)
        kerEval <- CombineVarParam.generateAllKerEval(data, param)
      } yield (data(0).data.nPoint, kerEval)
    
    val seg =
      kerEval.map(k => Test.segment(k._2, dMax, k._1, true, baseDir))
      
    seg match {
        case Success(a) => println(a.mkString(","))
        case Failure(m) => println(m)
      }
  }
}