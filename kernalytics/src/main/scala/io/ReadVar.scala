package io

import breeze.linalg._
import scala.io.Source
import scala.util.{Try, Success, Failure}

import rkhs.{DataRoot, KerEval}
import various.Def
import various.TypeDef._

/**
 * Data column format:
 * - name
 * - type
 * - one line per observation
 */
object ReadVar {
  val headerSize = 2

  class ParsedVar(val name: String, val data: DataRoot) // type is not necessary, as data has been parsed into the correct type
 
  /**
   * Create the list of parsed vars. The convoluted syntax with foldLeft is just a mechanism to catch the various errors that can occur, variable by variable.
   */
  def readAndParseVars(fileName: String): Try[(Array[ParsedVar], Index)] =
    for {
      (names, nObs) <- readVars(fileName)
      parsedVars <- names.foldLeft[Try[List[ParsedVar]]](Success[List[ParsedVar]](Nil))((acc, e) => acc.flatMap(l => parseIndividualVar(e).map(r => r :: l))) // first error in parsing ends the parsing, because the flatMap passes the error on.
      _ <- checkUnicity(parsedVars)
    } yield (parsedVars.reverse.toArray, nObs)
    
  /**
   * Create the list of parsed vars. The convoluted syntax with foldLeft is just a mechanism to catch the various errors that can occur, variable by variable.
   */
  def readAndParseVars2Files(fileNameA: String, fileNameB: String): Try[(Array[ParsedVar], Index, Index)] =
    for {
      (strLearn, nObsLearn) <- readVars(fileNameA)
      (strPredict, nObsPredict) <- readVars(fileNameB)
      strMerged <- mergeFilesContents(strLearn, strPredict)
      parsedVars <- strMerged.foldLeft[Try[List[ParsedVar]]](Success[List[ParsedVar]](Nil))((acc, e) => acc.flatMap(l => parseIndividualVar(e).map(r => r :: l))) // first error in parsing ends the parsing, because the flatMap passes the error on.
      _ <- checkUnicity(parsedVars)
    } yield (parsedVars.reverse.toArray, nObsLearn, nObsPredict)
  
  /**
   * Format the data var by var, without parsing the individual values.
   */
  def readVars(fileName: String): Try[(Array[Array[String]], Index)] =
    for {
      arrStr <- Try(readNoParse(fileName))
      nObs <- checkObservationNumber(arrStr)
    } yield (arrStr, nObs)
        
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
   */
  def checkObservationNumber(data: Array[Array[String]]): Try[Index] = {
    val length = data.map(_.size)
    if (length.forall(_ == length(0)))
      Success(length(0) - headerSize)
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
    val typeParam = varType.drop(1) // if no options have been provided, the length will be 0
    
    typeName match {
      case "Real" => Try(new ParsedVar(varName, DataRoot.RealVal(DenseVector.tabulate(data.size)(i => data(i).toReal))))
      case "VectorReal" => ParseVectorReal.parse(varName, typeParam, data)
    }
  }

  /**
   * Check that all variables have different names.
   */
  def checkUnicity(data: List[ParsedVar]): Try[Unit] = {
    val allNames = data.map(_.name)
    
    if (allNames.toSet.size < allNames.size)
      Failure(new Exception("Variable names are not unique."))
    else
      Success()
  }
  
  /**
   * Merge learn and predict data to be used in a big KerEval. Can fail if both files contain different variables.
   */
  def mergeFilesContents(learnParsedVars: Array[Array[String]], predictParsedVars: Array[Array[String]]): Try[Array[Array[String]]] = {
    val learnSorted = learnParsedVars.sortBy(_.take(headerSize).mkString(Def.eol)) // sort variables by concatenated headers
    val predictSorted = predictParsedVars.sortBy(_.take(headerSize).mkString(Def.eol))
    
    val learnHeader = learnSorted.map(_.take(headerSize).mkString(Def.eol))
    val predictHeader = predictSorted.map(_.take(headerSize).mkString(Def.eol))
    
    return if (learnHeader.deep == predictHeader.deep) { // check equality of headers using deep comparison
      Success(mergeContent(learnSorted, predictSorted))
    } else {
      Failure(new Exception("Data files in learn and predict do not contain the same variables."))
    }
  }
  
  def mergeContent(fileContentA: Array[Array[String]], fileContentB: Array[Array[String]]): Array[Array[String]] =
    fileContentA
    .zip(fileContentB.map(_.drop(headerSize))) // drop headers of second file
    .map(p => p._1 ++ p._2) // concatenate corresponding variables
}