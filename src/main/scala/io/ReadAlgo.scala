package io

import java.io.File
import scala.collection.immutable.Map
import scala.io.Source
import scala.util.{ Try, Success, Failure }

import various.Def

object ReadAlgo {
  /**
   * Parse as a dictionary String -> String (first line -> second line).
   * All check beyond that will be specific to each algorithm.
   */
  def readAndParseFile(fileName: String): Try[Map[String, String]] = {
    Try(Source.fromFile(new File(fileName)))
      .map(_.getLines.map(_.split(Def.csvSep)).toArray.transpose)
      .flatMap(checkNElements)
      .flatMap(checkUnicity)
      .map(readAndParseTable)
  }

  /**
   * Check that all columns have two elements.
   */
  def checkNElements(data: Array[Array[String]]): Try[Array[Array[String]]] = {
    if (data.forall(_.length == 2))
      Success(data)
    else
      Failure(new Exception("Algorithm description file must have two rows."))
  }

  /**
   * Check key unicity.
   */
  def checkUnicity(data: Array[Array[String]]): Try[Array[Array[String]]] = {
    if (data.size == data.map(_.head).distinct.size)
      Success(data)
    else
      Failure(new Exception("Duplicate entry on first line in algorithm description file."))
  }

  /**
   * Convert descriptor to dictionary.
   */
  def readAndParseTable(data: Array[Array[String]]): Map[String, String] = {
    data
      .map(c => (c(0), c(1)))
      .toMap
  }
}