package regression

import breeze.linalg._
import breeze.plot._
import breeze.stats.distributions._
import java.io.File
import org.apache.commons.io.FileUtils

import rkhs.{ Gram, Kernel }
import various.Def
import various.TypeDef._

object SimpleExample {
  def main {
    //    val kernel = Kernel.product(_, _)
    val kernel = (x: Double, y: Double) => Kernel.Legacy.R.gaussian(x, y, 1.0)
    val min = -3.0
    val max = 18.0

    val (xLearn, yLearn) = Data.oneDimFunction(
      DenseVector[Double](0.0, 10.0, 16.0), // pointsX
      DenseVector[Double](0.0, 10.0, 8.0), // pointsY
      1.0, // sd
      min, // min
      max, // max,
      100) // nPoints

    val uni = new Uniform(-3.0, 18.0)
    val xPredict = linspace(min, max, 100)

    val gram = Gram.generate(xLearn, kernel)

    val coefficients = EstimationBFGS.estimateCoefficient(gram, yLearn)

    val yPredict = PredictAlgorithm.evaluateMinimize(
      xPredict,
      coefficients,
      xLearn,
      kernel)
      
    println(s"coefficients: $coefficients")

    val fig = Figure()
    val plt = fig.subplot(0)
    plt += plot(xLearn, yLearn, '.')
    plt += plot(xPredict, yPredict, '.')
  }

  def writeData {
    val min = -3.0
    val max = 18.0
    val sd = 1.0
    val varName = "v1"
    val varType = "Real"
    val nPoints = 100
    val rootFolder = "data/regression/SimpleExample"

    val (xLearn, yLearn) = Data.oneDimFunction(
      DenseVector[Real](0.0, 10.0, 16.0), // pointsX
      DenseVector[Real](0.0, 10.0, 8.0), // pointsY
      1.0, // sd
      min, // min
      max, // max,
      100) // nPoints

    val xStr = varName + Def.eol + varType + Def.eol + xLearn.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "dataLearn.csv"), xStr, "UTF-8")
    
    val yStr = "y" + Def.eol + "Real" + Def.eol + yLearn.data.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "yLearn.csv"), yStr, "UTF-8")
  }
}