package kmeans

import breeze.linalg._
import rkhs.{ Gram, KerEval }

object Base {
  /**
   * Evolution of the computation, updated across iterations.
   *
   * Note that the original data points are not carried out during the computation, as the Gram
   * matrix contains all the relevant information. This simplifies the algorithm and help
   * factorize it over multiple data types.
   *
   * @param nIteration number of iterations previously carried out
   * @param gram Gram matrix for the observed data
   * @param param position of center param(i, k) indicates the coefficients of the center k
   * @param zik contains 1 if observation i belong to class k, 0 otherwise
   */
  class ComputationState(
    val nIteration: Int,
    val gram: DenseMatrix[Double],
    val param: DenseMatrix[Double],
    val zik: DenseMatrix[Double])

  /**
   * Computation of the distance between the observation K_x_i and the center of class C_k, where C_k is a linear combination of all the observations, as per the representer theorem.
   *
   * ||K_x_i - C_k||^2 = ||K_x_i||^2 + ||C_k||^2 - 2 <K_x_i, C_k>
   * C_k = \sum_j a_{j, k} K_x_j, and this decomposition will be used multiple times here
   * ||K_x_i||^2 = <K_x_i, K_x_i> which is a diagonal element of the Gram matrix
   * ||C_k||^2 can be computed and reused multiple times. Since (K_x_1, ..., K_x_n) are not orthogonal, ||C_k||^2 can not be expressed using only <K_x_i, K_x_i> elements.
   * <K_x_i, C_k> = \sum_j a_{j, k} <K_x_i, K_x_j> where a_{j, k} is the corresponding element of initialState.param
   * \sum_j a_{j, k} <K_x_i, K_x_j> is the scalar product between a row of initialState.param and the row corresponding to i in the Gram matrix
   *
   * Is it the same to directly compute K_x_i - C_k in the (K_x_1, ..., K_x_n) basis and compute the scalar product with itself ?
   * Yes, but bilinearity of scalar product means there are n^2 terms to compute for each of the n observation (n^3 in total). But most of them will be computed each time.
   * This could easily be tested numerically...
   */
  def eStep(initialState: ComputationState): ComputationState = {
    val nObs = initialState.zik.rows
    val nClass = initialState.zik.cols

    val squaredNormCk = initialState
      .param(::, *)
      .map(c => Gram.scalarProduct(initialState.gram, c, c)) // compute the norm of each center of class (each column of param) as it is a term used multiple times in the computation of the distance
      .t

    val dik = DenseMatrix.tabulate[Double](nObs, nClass)(
      (i, k) => initialState.gram(i, i) // element {i, k} of dik is ||K_x_i - C_k||^2
        + squaredNormCk(k)
        - 2.0 * initialState.param(::, k).dot(initialState.gram(::, i)))

    val minLoc = dik(*, ::).map(r => argmin(r)) // line by line, find the min, get the column index

    val zik = DenseMatrix.tabulate[Double](nObs, nClass)((i, k) => if (minLoc(i) == k) 1.0 else 0.0)

    return new ComputationState( // create new zik matrix with 1 at each min, and 0 everywhere else
      initialState.nIteration,
      initialState.gram,
      initialState.param,
      zik)
  }

  /**
   * Similar to eStep, except that the prediction is made for a single observation which
   * was not part of the learning sample. This means that the gram matrix can not be used,
   * and the kernel has to be called. This pattern is often seen in prediction. This is similar
   * to what happens in p01regression when the regression has to be called at some random sample points.
   */
  def predict(
    kernel: (DenseVector[Double], DenseVector[Double]) => Double,
    param: DenseMatrix[Double],
    squaredNormCk: DenseVector[Double],
    learnObs: DenseVector[DenseVector[Double]],
    obs: DenseVector[Double]): Int = {
    val nClass = param.cols
    val nObsLearning = param.rows

    val dk = DenseVector.tabulate[Double](nClass)(
      k => kernel(obs, obs) // element {i, k} of dik is ||K_x_i - C_k||^2
        + squaredNormCk(k)
        - 2.0 * sum(DenseVector.tabulate[Double](nObsLearning)(i => param(i, k) * kernel(obs, learnObs(i)))))

    return argmin(dk)
  }

  def mStep(initialState: ComputationState): ComputationState = {
    val nObs = initialState.zik.rows
    val nClass = initialState.zik.cols

    val colSum = initialState.zik(::, *).map(c => sum(c)) // zik compute columns sum
    val param = DenseMatrix.tabulate[Double](nObs, nClass)((i, k) => initialState.zik(i, k) / colSum(k)) // take the zik matrix, and normalize each column to get param
    return new ComputationState(
      initialState.nIteration,
      initialState.gram,
      param,
      initialState.zik)
  }

  def emIteration(initialState: ComputationState): ComputationState = {
    val emRes = mStep(eStep(initialState))
    return new ComputationState(
      emRes.nIteration + 1,
      emRes.gram,
      emRes.param,
      emRes.zik)
  }

  /**
   * Initialization of params, one element is chosen randomly to represent each class.
   */
  def init(nObs: Int, nClass: Int): DenseMatrix[Double] = {
    val rand = new scala.util.Random()
    val rep = rand.shuffle(0 to nObs - 1).take(nClass).toArray // this ensures that no individual represents two classes
    val res = DenseMatrix.tabulate[Double](nObs, nClass)((i, k) => if (i == rep(k)) 1.0 else 0.0)

    return res
  }
}