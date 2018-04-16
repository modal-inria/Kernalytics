package io

import breeze.linalg._
import scala.io.Source
import scala.util.{Try, Success, Failure}

import rkhs.KerEval
import various.Def
import various.TypeDef._

/**
 * Data column format:
 * - name
 * - type
 * - one line per observation
 */
object ReadVar {
  class ParsedVar(val name: String, val data: KerEval.DataRoot) // type is not necessary, as data has been parsed into the correct type
 
  /**
   * Create the list of parsed vars. The convoluted syntax with foldLeft is just a mechanism to catch the various errors that can occur.
   */
  def readAndParseVars(fileName: String): Try[Array[ParsedVar]] =
    readVars(fileName)
    .flatMap(_.foldLeft[Try[List[ParsedVar]]](Success[List[ParsedVar]](Nil))((acc, e) => acc.flatMap(l => parseIndividualVar(e).map(r => r :: l)))) // first error in parsing ends the parsing, because the flaMap passes the error on.
    .flatMap(checkUnicity)
    .map(_.reverse.toArray) // map to reverse the list and transform it to an Array, iff all the parsing were correct
  
  /**
   * Format the data var by var, without parsing the individual values.
   */
  def readVars(fileName: String): Try[Array[Array[String]]] =
    Try(readNoParse(fileName))
     .flatMap(checkObservationNumber)
        
  /**
   * Generate a raw 2D array of string, without any processing. dataStr(var)(obs).
   */
  def readNoParse(fileName: String): Array[Array[String]] =
    Source
    .fromFile(fileName)
    .getLines
    .toArray
    .map(_.split(Def.csvSep))
    .transpose
     
  /**
   * Check that all the variables have the same number of observations.
   * 
   * TODO: isn't this enforced by the transpose, which might only take into account the lower number of data in all the vars ?
   */
  def checkObservationNumber(data: Array[Array[String]]): Try[Array[Array[String]]] = {
    val length = data.map(_.size)
    if (length.forall(_ == length(0)))
      Success(data)
    else
      Failure(new Exception("All data must have the same number of observations."))
  }
    
  /**
   * Generates the ParsedVar object.
   */
  def parseIndividualVar(v: Array[String]): Try[ParsedVar] = {
    if (v.size < 3) return Failure(new Exception("In data file, all variables must have at least three lines: name, type, and at least one observation. This is not the case in current data."))
    
    val varName = v(0)
    val varType = v(1).split(Def.optionSep)
    val data = v.drop(2)
    
    val typeName = varType(0)
    val typeParam = varType.drop(1)
    
    typeName match {
      case "Real" => parseReal(varName,typeParam, data)
      case "VectorReal" => parseVectorReal(varName, typeParam, data)
    }
  }
  
  /**
   * Parse Real values.
   */
  def parseReal(varName: String, typeParam: Array[String], data: Array[String]): Try[ParsedVar] = {
    Try(new ParsedVar(varName, KerEval.DenseVectorReal(DenseVector.tabulate(data.size)(i => data(i).toReal))))
  }
    
  /**
   * Parse vector of Real values, with fixed size.
   */
  def parseVectorReal(varName: String, typeParam: Array[String], data: Array[String]): Try[ParsedVar] = {
    Try({
      val nCoeff = typeParam(0).toIndex
      
      val convertedData =
        data
        .map(_.split(Def.optionSep))
        .map(_.map(_.toReal))
        .map(new DenseVector[Real](_))
        
      val allCorrectSize = data.forall(o => o.length == nCoeff)
        
      if (allCorrectSize)
        new ParsedVar(varName, KerEval.DenseVectorDenseVectorReal(DenseVector[DenseVector[Real]](convertedData)))
      else
        throw new Exception(s"VectorReal elements do not all have $nCoeff elements as required.")
    })
  }
  
  /**
   * Check that all variables have different names.
   */
  def checkUnicity(data: List[ParsedVar]): Try[List[ParsedVar]] = {
    val allNames = data.map(_.name)
    
    if (allNames.toSet.size < allNames.size)
      Failure(new Exception("Variable names are not unique."))
    else
      Success(data)
  }
}