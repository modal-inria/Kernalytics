package io

import scala.util.{ Try, Success, Failure }

import rkhs.KerEval
import various.TypeDef._

object CombineVarParam {
  /**
   * The handling of heterogeneous data in Kernalytics is performed through a kerEval function (Index, Index) => Real.
   * generateAllKerEval is therefore called once all individual vars and kernel descriptions have been parsed.
   *
   * Generate a global kerEval from separate descriptions of data and parameters. The return type is a Try, because there
   * is the possibility that some parameters name do not match any variable name. A failure will also be returned
   * if no valid kernels are generated.
   */
  def generateGlobalKerEval(nObsLearn: Index, nObsPredict: Index, parsedVars: Array[ReadVar.ParsedVar], parsedParams: Array[ReadParam.ParsedParam]): Try[KerEval] = {
    val names = parsedVars.map(_.name)
    val data = parsedVars.map(_.data)
    val dict = names.zip(data).toMap

    parsedParams
      .reverse
      .foldLeft[Try[List[KerEval.KerEvalFuncDescription]]](Success(Nil))((acc, e) =>
        acc.flatMap(l => linkParamToData(dict, e).map(k => k :: l)))
      .flatMap(KerEval.multivariateKerEval(_))
      .map(kerEval => new KerEval(nObsLearn, nObsPredict, kerEval))
  }

  /**
   * Can fail if the parameter does not match any variable.
   */
  def linkParamToData(dict:Map[String, KerEval.DataRoot], param: ReadParam.ParsedParam): Try[KerEval.KerEvalFuncDescription] =
    for {
      data <- Try(dict(param.name))
    } yield new KerEval.KerEvalFuncDescription(param.weight, data, param.kernel, param.param)
}
