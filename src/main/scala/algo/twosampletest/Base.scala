package algo.twosampletest

import breeze.linalg._
import various.Def
import scala.util.Random

/**
 * https://en.wikipedia.org/wiki/Kernel_embedding_of_distributions#Kernel_two_sample_test
 * http://www.jmlr.org/papers/volume13/gretton12a/gretton12a.pdf
 * http://www.stat.cmu.edu/~ryantibs/journalclub/mmd.pdf
 * http://www.shogun-toolbox.org/notebook/latest/mmd_two_sample_testing.html
 * https://en.wikipedia.org/wiki/Resampling_(statistics)#Permutation_tests
 */

object Base {
  def testSamplePartition = {
    println(samplePartition(3, 2))
  }

  /**
   * Use permutations to generate values of the MMD under the null hypothesis that distributions from the two
   * initial subsamples are sampled from the same distribution. Then compute the critical value for the test
   * at the provided level of significance alpha.
   */
  def permutationTestCriticalValue(
    nSample: Int,
    nA: Int,
    nB: Int,
    alpha: Double,
    gram: DenseMatrix[Double]): Double = {
    val mmdSample = Array.fill[Double](nSample)(mmdUnbiasedEstimator(gram, samplePartition(nA, nB))).sorted
    return mmdSample((nSample.toDouble * (1.0 - alpha)).toInt)
  }

  class Partition(val sampleA: Array[Int], val sampleB: Array[Int]) {
    override def toString = s"sampleA: $sampleA" + Def.eol + s"sampleB: $sampleB"
  }

  /**
   * Generate a random partition by providing the cardinality of each part.
   */
  def samplePartition(nA: Int, nB: Int): Partition = {
    val all = Random.shuffle(0 to nA + nB - 1)
    return new Partition(all.take(nA).toArray, all.takeRight(nB).toArray)
  }

  /**
   * Unbiased estimator of the MMD
   * http://www.shogun-toolbox.org/notebook/latest/mmd_two_sample_testing.html#Quadratic-Time-MMD
   */
  def mmdUnbiasedEstimator(gram: DenseMatrix[Double], part: Partition): Double = {
    val m = part.sampleA.length
    val n = part.sampleB.length

    val sumXX = sum(DenseMatrix.tabulate[Double](m, m)((i, j) => if (i != j) gram(part.sampleA(i), part.sampleA(j)) else 0.0))
    val sumYY = sum(DenseMatrix.tabulate[Double](n, n)((i, j) => if (i != j) gram(part.sampleB(i), part.sampleB(j)) else 0.0))
    val sumXY = sum(DenseMatrix.tabulate[Double](m, n)((i, j) => if (i != j) gram(part.sampleA(i), part.sampleB(j)) else 0.0))

    return 1.0 / (m * (m - 1.0)) * sumXX + 1.0 / (n * (n - 1.0)) * sumYY - 2.0 * m * n * sumXY
  }
}