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
  def generateGlobalKerEval(nObsRow: Index, nObsCol: Index, dataRow: Array[ReadVar.ParsedVar], dataCol: Array[ReadVar.ParsedVar], param: Array[ReadParam.ParsedParam]): Try[KerEval] = {
    val rowNames = dataRow.map(_.name)
    val rowData = dataRow.map(_.data)
    val dictRow = rowNames.zip(rowData).toMap

    val colNames = dataCol.map(_.name)
    val colData = dataRow.map(_.data)
    val dictCol = colNames.zip(colData).toMap

    param
      .reverse
      .foldLeft[Try[List[KerEval.VarDescription]]](Success(Nil))((acc, e) =>
        acc.flatMap(l => linkParamToData(dictRow, dictCol, e).map(k => k :: l)))
      .flatMap(KerEval.multivariateKerEval(_))
      .map(kerEval => new KerEval(nObsRow, nObsCol, kerEval))
  }

  /**
   * Can fail if the parameter does not match any variable.
   */
  def linkParamToData(dictRow: Map[String, KerEval.DataRoot], dictCol: Map[String, KerEval.DataRoot], param: ReadParam.ParsedParam): Try[KerEval.VarDescription] =
    for {
      dataRow <- Try(dictRow(param.name))
      dataCol <- Try(dictCol(param.name))
    } yield new KerEval.VarDescription(param.weight, dataRow, dataCol, param.kernel, param.param)
}
