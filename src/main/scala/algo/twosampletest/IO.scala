package algo.twosampletest

import breeze.linalg._
import rkhs.KerEval
import various.TypeDef._

object IO {
  def runTest(kerEval: KerEval, nA: Index, alpha: Real): Boolean = {
    val gram = kerEval.getK
    
    val nB = kerEval.nObs - nA

    val part = new Base.Partition((0 to nA - 1).toArray, (nA to nA + nB - 1).toArray) // real partition of the data
    val k = Base.permutationTestCriticalValue(kerEval.nObs, nA, nB, alpha, gram) // get critical value for the hypothesis testing
    val p = Base.mmdUnbiasedEstimator(gram, part)
    
    return k < p
  }
}