package io

import breeze.linalg._

import scala.util.{ Try, Success, Failure }

import linalg.IncompleteCholesky
import various.TypeDef._
import rkhs.{ DataRoot, GramOpti, KerEval }

object CombineVarParam {
  /**
   * The handling of heterogeneous data in Kernalytics is performed through a kerEval function (Index, Index) => Real.
   * generateAllKerEval is therefore called once all individual vars and kernel descriptions have been parsed.
   *
   * Generate a global kerEval from separate descriptions of data and parameters. The return type is a Try, because there
   * is the possibility that some parameters name do not match any variable name. A failure will also be returned
   * if no valid kernels are generated.
   */
  def generateGlobalKerEval(nObsLearn: Index, nObsPredict: Index, parsedVars: Array[ReadVar.ParsedVar], parsedParams: Array[ReadParam.ParsedParam], cacheGram: GramOpti): Try[KerEval] = {
    val names = parsedVars.map(_.name)
    val data = parsedVars.map(_.data)
    val dict = names.zip(data).toMap

    parsedParams
      .reverse
      .foldLeft[Try[List[KerEval.KerEvalFuncDescription]]](Success(Nil))((acc, e) =>
        acc.flatMap(l => linkParamToData(dict, e).map(k => k :: l)))
      .flatMap(KerEval.multivariateKerEval)
      .flatMap(kerEvalFunc => {
        val nObs = nObsLearn + nObsPredict
//        eigenValuesDebug(nObs, kerEvalFunc)
        cacheGram match {
          case GramOpti.Direct() => Success(new KerEval.Direct(nObsLearn, nObsPredict, kerEvalFunc))
          case GramOpti.Cache() => Success(new KerEval.Cache(nObsLearn, nObsPredict, kerEvalFunc))
          case GramOpti.LowRank(m) => {
            for {
              _ <- checkBoundary(m, nObs)
              gt <- IncompleteCholesky.icd(nObs, kerEvalFunc, m).map(_.t) // will return Failure if eigenvalues are too low regarding the required rank
            } yield (new KerEval.LowRank(nObsLearn, nObsPredict, kerEvalFunc, gt, m))
          }
        }
      })
  }

  def eigenValuesDebug(nObs: Index, kerEvalFunc: (Index, Index) => Real) {
    val eps = 1.0e-4
    val k = DenseMatrix.tabulate[Real](nObs, nObs)((i, j) => kerEvalFunc(i, j))
    val ev = eigSym(k).eigenvalues
    val nSignificant = ev.data.filter(eps < _).size
    println(ev)
    println(println(s"nSignificant: $nSignificant, nObs: $nObs"))
  }

  def checkBoundary(m: Index, nObs: Index): Try[Unit] = {
    if (0 < m && m <= nObs) Success()
    else Failure(new Exception(s"rank for low rank must be comprised in [1, nObs]"))
  }

  /**
   * Can fail if the parameter does not match any variable.
   */
  def linkParamToData(dict: Map[String, DataRoot], param: ReadParam.ParsedParam): Try[KerEval.KerEvalFuncDescription] =
    for {
      data <- Try(dict(param.name))
    } yield new KerEval.KerEvalFuncDescription(param.weight, data, param.kernel, param.param)
}
