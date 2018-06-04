package regression

import breeze.linalg._
import breeze.optimize._
import java.io.File

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
    val mat = kerEval.getK + lambda * DenseMatrix.eye[Real](kerEval.nObsLearn)
    
//    val invMat = inv(mat)
//    println(lambda)
//    println(det(mat))
//    println(det(invMat))
//    csvwrite(new File("debug/k.csv"), kerEval.getK, separator=';')
//    csvwrite(new File("debug/mat.csv"), mat, separator=';')
//    csvwrite(new File("debug/invMat.csv"), invMat, separator=';')
    
//    inv((kerEval.getK + lambda * DenseMatrix.eye[Real](kerEval.nObs))) * y
    mat \ y
  }
}