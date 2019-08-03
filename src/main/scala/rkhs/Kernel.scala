package rkhs

import breeze.linalg._
import various.TypeDef._

/**
 * This is the last point in any algorithms where the data types have to be known. Beyond that, only the kernel is evaluated via KerEval.
 *
 * Bibliography:
 * - https://en.wikipedia.org/wiki/Reproducing_kernel_Hilbert_space#Examples
 * - https://en.wikipedia.org/wiki/Positive-definite_kernel , for the notations used in the kernels
 */
object Kernel {

  /** A linear kernel which does not use the algebraic system, to demonstrate the most direct way to define a kernel. */
  def dummyLinearKernel(x: Real, y: Real): Real = x * y

  object InnerProduct {
    /**
     * A useless example because the inner product could be called directly instead.
     */
    def linear[Data](
      x: Data,
      y: Data,
      i: Algebra.Traits.InnerProductSpace[Data]): Real = i.ip(x, y)

    def polynomial[Data](
      x: Data,
      y: Data,
      i: Algebra.Traits.InnerProductSpace[Data],
      c: Real,
      d: Integer): Real = math.pow(i.ip(x, y) + c, d)

    /**
     * Same as Metric.gaussian, except that ((_)^(1/2))^(1/2) is removed as it is useless and time-consuming.
     */
    def gaussian[Data](
      x: Data,
      y: Data,
      ip: Algebra.Traits.InnerProductSpace[Data],
      sd: Real): Real = {
      val diff = ip.-(x, y)
      math.exp(-ip.ip(diff, diff) / (2.0 * math.pow(sd, 2.0)))
    }
  }

  object Normed {}

  object Metric {
    def gaussian[Data](
      x: Data,
      y: Data,
      n: Algebra.Traits.MetricSpace[Data],
      sd: Real): Real = math.exp(-math.pow(n.distance(x, y), 2.0) / (2.0 * math.pow(sd, 2.0)))

    def laplacian[Data](
      x: Data,
      y: Data,
      n: Algebra.Traits.MetricSpace[Data],
      alpha: Real): Real = math.exp(-alpha * n.distance(x, y))
  }
}