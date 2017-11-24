package p00rkhs

import breeze.linalg._
import p04various.TypeDef._

/**
 * Note that only here are the data types known. In the Gram package, everything occurs in the RKHS, and the data types need to to be nown since
 * only the kernel evaluations are required.
 * Bibliography:
 * -	https://en.wikipedia.org/wiki/Reproducing_kernel_Hilbert_space#Examples
 */
object Kernel {
  object R {
	  def product(x: Real, y: Real): Real = x * y
	  def gaussian(x: Real, y: Real, sd: Real): Real = math.exp(- math.pow(x - y, 2.0) / (2.0 * math.pow(sd, 2.0)))
  }

  object Rn {
    def linear(x: DenseVector[Real], y: DenseVector[Real]): Real = x dot y
  }
}