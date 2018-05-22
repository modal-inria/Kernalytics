package io

import breeze.linalg._
import scala.io.Source
import scala.util.{Try, Success, Failure}

import rkhs.KerEval
import various.Def
import various.TypeDef._

/**
 * Descriptor column format:
 * - name
 * - weight
 * - kernel (including parameters)
 */
object ReadParam {
  class ParsedParam(val name: String, val weight: Real, val kernel: String, val param: String)
  
  def readAndParseParam(fileName: String): Try[Array[ParsedParam]] =
    readParams(fileName)
    .flatMap(a => a.foldLeft[Try[List[ParsedParam]]](Success[List[ParsedParam]](Nil))((acc, e) => acc.flatMap(l => parseIndividualParam(e).map(r => r :: l)))) // first error in parsing ends the parsing, because the flaMap passes the error on.
    .map(l => l.reverse.toArray) // map to reverse the list and transform it to an Array, iff all the parsing were correct
    
  /**
   * Format the params var by var, without parsing the individual values.
   */
  def readParams(fileName: String): Try[Array[Array[String]]] =
    Try(
        Source
        .fromFile(fileName)
        .getLines
        .toArray
        .map(_.split(Def.csvSep))
        .transpose)
  
  def parseIndividualParam(v: Array[String]): Try[ParsedParam] =
    for {
      _ <- checkSize(v)
      w <- Try(v(1).toReal)
      (kernelStr, paramStr) <- parseParam(v(2))
    } yield new ParsedParam(v(0), w, kernelStr, paramStr)
  
  def checkSize(v: Array[String]): Try[Array[String]] =
    if (v.size != 3)
      Failure(new Exception("In descriptor file, all variables must have three lines: name, weight and kernel."))
    else
      Success(v)
  
  /**
   * Parse the parameter string to extract both the kernel name and the parameters
   */
  def parseParam(str: String): Try[(String, String)] = {
    val paramPattern = raw"([a-zA-Z0-9]+)\((.*)\)".r
    val t = Try({ val t = paramPattern.findAllIn(str); (t.group(1), t.group(2)) })

    t match {
      case Success(_) => t
      case Failure(_) => Failure(new Exception(str + " is not a valid parameter String")) // default exception for pattern matching is not expressive enough
    }
  }
}