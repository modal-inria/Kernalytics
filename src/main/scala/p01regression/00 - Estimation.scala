package p01regression

import breeze.linalg._
import breeze.optimize._

/**
 * Links:
 * - https://github.com/scalanlp/breeze/wiki/Quickstart#breezeoptimize
 * - https://github.com/scalanlp/breeze/blob/master/math/src/test/scala/breeze/optimize/LBFGSTest.scala
 * */

object Estimation {
  /**
   * Using closure, generate a Quadratic error computation, as a function. Note how the Representer Theorem allows for
   * a finite space optimization. See optimizationTest for a concise example of the optimizer usage.
   * 
   * @param gram gram matrix
   * @param y output vector
   * @return \sum_i\left(\sum_j a_j <K_{x_j}, K_{x_i}> - y_i \right)^2
   * */
  def regressionResidual(gram: DenseMatrix[Double], y: DenseVector[Double]): DenseVector[Double] => Double = {
    val nObs = gram.rows
    
    /**
     * @param a vector of coefficient
     * */
    def f(a: DenseVector[Double]): Double = sum(DenseVector.tabulate[Double](nObs)(i => math.pow(a.dot(gram(::, i)) - y(i), 2)))

    return f
  }
  
  /**
   * Estimate the regression coefficients by reducing the quadratic error
   * */
  def estimateCoefficient(gram: DenseMatrix[Double], y: DenseVector[Double]): DenseVector[Double] = {
    val nObs = gram.rows
    
    val lbfgs = new LBFGS[DenseVector[Double]](100, 4) // TODO: adjust parameters properly
    
    val init = DenseVector.zeros[Double](nObs) // initial solution
    
    val f = regressionResidual(gram, y)
    
    val df = computeDf(f)
    
    val fNew = new DiffFunction[DenseVector[Double]] { // new code with automatic gradient computation
      def calculate(x: DenseVector[Double]) = {
        (f(x), df(x))
      }
    }
    
    return lbfgs.minimize(fNew, init) 
  }
  
  def computeDf(g: DenseVector[Double] => Double): DenseVector[Double] => DenseVector[Double] = { // using the ApproximateGradientFunction as a closure might prevent the code to recompute it at each call
    val diffg = new ApproximateGradientFunction(g)
    return x => diffg.gradientAt(x)
  }
  
  /**
   * Simple optimization example, in preparation of the real optimization in the RKHS.
   * */
  def optimizationTest {
    val lbfgs = new LBFGS[DenseVector[Double]](100, 4)
    
    val init = DenseVector.fill[Double](100)(0.0) // to be filled with initialize position

    val fOrg = new DiffFunction[DenseVector[Double]] { // original code found in the example
      def calculate(x: DenseVector[Double]) = {
        (norm((x - 3.0) ^:^ 2.0, 1),
         (x *:* 2.0) - 6.0) // result if of the form (norm 1 of error, and... gradient ?)
      }
    }
    
    def f(x: DenseVector[Double]): Double = norm((x - 3.0) ^:^ 2.0, 1)
    
    def df(x: DenseVector[Double]): DenseVector[Double] = computeDf(f)(x)
    
    val fNew = new DiffFunction[DenseVector[Double]] { // new code with automatic gradient computation
      def calculate(x: DenseVector[Double]) = {
        (f(x), df(x))
      }
    }

    val result = lbfgs.minimize(fNew, init) 
    println(norm(result - 3.0, 2) < 1E-10)
  }
}