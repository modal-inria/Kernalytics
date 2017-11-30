package p01regression

import breeze.linalg._
import breeze.plot._
import breeze.stats.distributions._
import p00rkhs.{Gram, Kernel, Predict}

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
    
    val coefficients = Estimation.estimateCoefficient(gram, yLearn)
    
    val yPredict = Predict.evaluateMinimize(
        xPredict,
        coefficients,
        xLearn,
        kernel)
        
    val fig = Figure()
    val plt = fig.subplot(0)
    plt += plot(xLearn, yLearn, '.')
    plt += plot(xPredict, yPredict, '.')
  }
}