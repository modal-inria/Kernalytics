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
  def generateAllKerEval(nObs: Index, data: Array[ReadVar.ParsedVar], param: Array[ReadParam.ParsedParam]): Try[KerEval] = {
    val arrayNames = data.map(_.name)
    val arrayData = data.map(_.data)

    val namesToData = arrayNames.zip(arrayData).toMap

    param 
      .reverse
      .foldLeft[Try[List[KerEval.VarDescription]]](Success(Nil))((acc, e) =>
        acc.flatMap(l => generateIndividualKerEval(namesToData, e).map(k => k :: l)))
      .map(_.toArray)
      .map(KerEval.multivariateKerEval(_))
      .map(kerEval => new KerEval(nObs, kerEval))
  }

  /**
   * Can fail if the parameter does not match any variable.
   */
  def generateIndividualKerEval(dict: Map[String, KerEval.DataRoot], param: ReadParam.ParsedParam): Try[KerEval.VarDescription] =
    Try(dict(param.name))
      .map(data => new KerEval.VarDescription(param.weight, data, param.kernel))
}
