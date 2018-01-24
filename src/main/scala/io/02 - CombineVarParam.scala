package io

import scala.util.{Try, Success, Failure}

import rkhs.KerEval
import various.TypeDef._

object CombineVarParam {
  /**
   * Generate a global KerEval from separate descriptions of data and parameters. The return type is a Try, because there
   * is the possibility that some parameters name do not match any variable name. A failure will also be returned
   * if no valid kernels are generated.
   */
  def generateAllKerEval(data: Array[ReadVar.ParsedVar], param: Array[ReadParam.ParsedParam]): Try[(Index, Index) => Real] = {
    val arrayNames = data.map(_.name)
    val arrayData = data.map(_.data)
    
    val namesToData = arrayNames.zip(arrayData).toMap
    
    param
      .reverse
      .foldLeft[Try[List[KerEval.VarDescription]]](Success(Nil))((acc, e) =>
        acc.flatMap(l => generateIndividualKerEval(namesToData, e).map(k => k :: l)))
      .map(_.toArray)
      .map(KerEval.multivariateKerEval(_))
  }
  
  /**
   * Can fail if the parameter do not match any variable.
   */
  def generateIndividualKerEval(dict: Map[String, KerEval.DenseVectorRoot], param: ReadParam.ParsedParam): Try[KerEval.VarDescription] =
    Try(dict(param.name))
    .map(data => new KerEval.VarDescription(param.weight, data, param.kernel))
}
