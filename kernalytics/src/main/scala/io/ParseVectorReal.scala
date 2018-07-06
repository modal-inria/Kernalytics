package io

import breeze.linalg._
import scala.io.Source
import scala.util.{ Try, Success, Failure }

import rkhs.{ DataRoot, KerEval }
import various.Def
import various.TypeDef._

object ParseVectorReal {
  /**
   * Parse vector of Real values, with fixed size.
   */
  def parse(varName: String, typeParam: Array[String], data: Array[String]): Try[ReadVar.ParsedVar] =
    for {
      _ <- checkNumberOfParameters(varName, typeParam, 1)
      nCoeff <- Try(typeParam(0).toIndex)
      convertedData <- convertData(data)
      _ <- checkAllCorrectSize(convertedData, nCoeff)
    } yield (new ReadVar.ParsedVar(varName, DataRoot.VectorReal(DenseVector[DenseVector[Real]](convertedData))))

  def checkNumberOfParameters(varName: String, typeParam: Array[String], n: Index): Try[Unit] =
    if (typeParam.length == n)
      Success()
    else
      Failure(new Exception(s"Type for variable $varName should contain $n parameters."))

  def convertData(data: Array[String]): Try[Array[DenseVector[Real]]] = {
    Try(
      data
        .map(_.split(Def.optionSep))
        .map(_.map(_.toReal))
        .map(new DenseVector[Real](_)))
  }

  def checkAllCorrectSize(data: Array[DenseVector[Real]], nCoeff: Index): Try[Unit] = {
    val allCorrectSize = data.forall(o => o.length == nCoeff)

    if (allCorrectSize)
      Success()
    else
      Failure(new Exception(s"VectorReal elements do not all have $nCoeff elements as required."))
  }
}