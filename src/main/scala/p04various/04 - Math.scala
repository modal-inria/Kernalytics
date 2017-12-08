package p04various

import breeze.linalg._
import breeze.numerics._

object Math {
  def factorial(i: TypeDef.Integer): TypeDef.Integer = math.round(exp(lgamma(i + 1).toFloat)) // TODO: is the toFloat really needed ?
  
  def binomial(n: TypeDef.Integer, k: TypeDef.Integer): TypeDef.Integer = factorial(n) / (factorial(k) * factorial(n - k))
  
  /**
   * This version computes the log of the binomial coefficients, and using the Stirling approximation avoids the numerical overflow of integers.
   * Source: https://math.stackexchange.com/questions/64716/approximating-the-logarithm-of-the-binomial-coefficient
   * 
   * log((n, m)) = n log(n) − m log (m) − (n − m) log(n − m)
   */
  def logBinomial(nInt: TypeDef.Integer, mInt: TypeDef.Integer): TypeDef.Real = {
    val n = nInt.toDouble
    val m = mInt.toDouble
    
    println("log(n!) " + math.log(factorial(nInt)) + ", n log(n) - n: " + (n * math.log(n) - n))
    println("log(m!) " + math.log(factorial(mInt)) + ", m log(m) - m: " + (m * math.log(m) - m))
    
    return n * math.log(n) - m * math.log(m) - (n - m) * math.log(n - m)
  }
  
  def linearRegression(x: DenseMatrix[TypeDef.Real], y: DenseVector[TypeDef.Real]): DenseVector[TypeDef.Real] = {
    val xt = x.t
    return inv(xt * x) * xt * y
  }
}