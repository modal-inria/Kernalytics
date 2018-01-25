package various

import breeze.linalg._
import breeze.numerics._
import various.TypeDef._

object Math {
  def factorial(i: TypeDef.Integer): TypeDef.Integer =
    math.round(exp(lgamma(i + 1).toFloat)) // TODO: is the toFloat really needed ?
  
  def binomial(n: TypeDef.Integer, k: TypeDef.Integer): TypeDef.Integer =
    factorial(n) / (factorial(k) * factorial(n - k))
  
  /**
   * For debug purposes only.
   */
  def logBinomialExact(nInt: TypeDef.Integer, mInt: TypeDef.Integer): TypeDef.Real =
    math.log(binomial(nInt, mInt))
  
  /**
   * This version computes the log of the binomial coefficients, and using the Stirling approximation avoids the numerical overflow of integers.
   * Source: https://math.stackexchange.com/questions/64716/approximating-the-logarithm-of-the-binomial-coefficient
   * 
   * log((n, m)) = n log(n) − m log (m) − (n − m) log(n − m)
   */
  def logBinomial(nInt: TypeDef.Integer, mInt: TypeDef.Integer): TypeDef.Real =
    if (mInt == 0) 0.0
    else {
      val n = nInt.toDouble
      val m = mInt.toDouble
      
  //    n * math.log(n) - m * math.log(m) - (n - m) * math.log(n - m)
      (n + 0.5) * math.log(n) - (m + 0.5) * math.log(m) - (n - m + 0.5) * math.log(n - m) - 0.5 * math.log(2.0 * math.Pi)
    }
  
  def linearRegression(x: DenseMatrix[TypeDef.Real], y: DenseVector[TypeDef.Real]): DenseVector[TypeDef.Real] = {
    val xt = x.t
    return inv(xt * x) * xt * y
  }
  
  def frobeniusInnerProduct(x: DenseMatrix[TypeDef.Real], y: DenseMatrix[TypeDef.Real]): TypeDef.Real =
    sum(x *:* y)
    
  /**
   * Segmentation matrix as defined in section 4.3.2 of the article. Note that the straightforard implementation is not functional and
   * use a mutated matrix.
   * 
   * The segmentation must be provided in reverse, just like it is output from the algorithm.
   * 
   * TODO: provide an immutable implementation of this computation
   */
  def segmentationMatrix(seg: List[Index], nObs: Index): DenseMatrix[Real] = {
    val nSeg = seg.size
    val totalSeg = (nObs :: seg).reverse // add first point outside data as boundary for last segment
    
    val bounds = totalSeg.zip(totalSeg.tail)
    
    val segMat = DenseMatrix.zeros[Real](nObs, nObs)
    
    for (currBounds <- bounds) {
      val segSize = (currBounds._2 - currBounds._1).toDouble
      segMat(currBounds._1 to currBounds._2 - 1, currBounds._1 to currBounds._2 - 1) += 1.0 / segSize
    }
    
    return segMat
  }
}