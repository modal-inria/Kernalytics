package regression

import breeze.linalg._
import breeze.optimize._

import rkhs.KerEval
import various.Def
import various.TypeDef._

/**
 * Ridge regression, with direct, analytical optimizer.
 *
 * Links: http://www.gatsby.ucl.ac.uk/~gretton/coursefiles/lecture4_introToRKHS.pdf
 */

object EstimationRidge {
  def estimate(kerEval: KerEval, y: DenseVector[Real], lambda: Real): DenseVector[Real] = {
    ???
  }
}