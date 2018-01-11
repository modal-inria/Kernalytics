package p07io

import scala.util.{Try, Success, Failure}

import p04various.TypeDef._

object CombineVarParam {
  /**
   * Generate a global KerEval from separate descriptions of data and parameters. The return type is a Try, because there
   * is the possibility that some parameters name do not match any variable name. A failure will also be returned
   * if no valid kernels are generated. 
   */
  def generateKerEval(data: Array[ReadVar.ParsedVar], param: Array[ReadParam.ParsedParam]): Try[(Index, Index) => Real] = {
    val arrayNames = data.map(_.name)
    val arrayData = data.map(_.data)
    
    val namesToData = arrayNames.zip(arrayData).toMap
    
    Failure(new Exception("generateKerEval not implemented yet."))    
  }
}
