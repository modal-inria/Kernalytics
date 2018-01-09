package p07io

import breeze.linalg._
import scala.io.Source
import scala.util.{Try, Success, Failure}

import p00rkhs.KerEval
import p04various.Def
import p04various.TypeDef._

/**
 * Data column format:
 * - name
 * - type
 * - one line per observation
 * 
 * Descriptor column format:
 * - name
 * - weight
 * - kernel (including parameters)
 */
object Read {
  class ParsedVar(val name: String, val data: KerEval.DenseVectorRoot) // type is not necessary, as data has been parsed into the correct type
  class ParsedParam(val name: String, val weight: Real, val kernel: KerEval.ParameterRoot)
  
  def readNoParse(fileName: String): Array[Array[String]] =
    Source
    .fromFile(fileName)
    .getLines
    .toArray
    .map(_.split(Def.csvSep))
    .transpose
  
  def readAndParse(fileName: String): Try[Array[ParsedVar]] =
    readVars(fileName)
    .flatMap(a => a.foldLeft[Try[List[ParsedVar]]](Success[List[ParsedVar]](Nil))((acc, e) => acc.flatMap(l => parseIndividualVar(e).map(r => r :: l)))) // first error in parsing ends the parsing, because the flaMap passes the error on.
    .map(l => l.reverse.toArray) // map to reverse the list and transform it to an Array, iff all the parsing were correct
  
  /**
   * Format the data var by var, without parsing the individual values.
   */
  def readVars(fileName: String): Try[Array[Array[String]]] =
    Try(
        Source
        .fromFile(fileName)
        .getLines
        .toArray
        .map(_.split(Def.csvSep))
        .transpose)
  
  /**
   * Loop to parse all the vars.
   */
  def parseAllVars(data: Array[Array[String]]): Try[Array[ParsedVar]] =
    data
    .foldLeft[Try[List[ParsedVar]]](Success[List[ParsedVar]](Nil))((acc, e) => acc.flatMap(l => parseIndividualVar(e).map(r => r :: l))) // first error in parsing ends the parsing, because the flaMap passes the error on.
    .map(l => l.reverse.toArray) // flatMap to reverse the list and transform it to an Array, iff all the parsing were correct
    
  def parseIndividualVar(v: Array[String]): Try[ParsedVar] = {
    if (v.size < 3) return Failure(new Exception("In data file, all variables must have at least three lines: name, type, and at least one observation. This is not the case in current data."))
    
    val varName = v(0)
    val varType = v(1)
    val data = v.drop(2)
    
    Try({
      varType match {
        case "Real" => new ParsedVar(varName, KerEval.DenseVectorReal(DenseVector.tabulate(data.size)(i => parseReal(data(i)))))
      }
    })
  }
  
  def parseReal(data: String): Real =
    data.toDouble
}