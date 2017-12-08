package p04various

import breeze.linalg._
import breeze.numerics._

object Math {
  def factorial(i: TypeDef.Integer): TypeDef.Integer = math.round(exp(lgamma(i + 1).toFloat)) // TODO: is the toFloat really needed ?
  
  def binomial(n: TypeDef.Integer, k: TypeDef.Integer): TypeDef.Integer = factorial(n) / (factorial(k) * factorial(n - k))
  
  def linearRegression(x: DenseMatrix[TypeDef.Real], y: DenseVector[TypeDef.Real]): DenseVector[TypeDef.Real] = {
    val xt = x.t
    return inv(xt * x) * xt * y
  }
}