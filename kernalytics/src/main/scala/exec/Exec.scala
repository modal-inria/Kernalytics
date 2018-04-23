package exec

import scala.util.{ Try, Success, Failure }

import io.{ CombineVarParam, ReadAlgo, ReadParam, ReadVar }
import various.Def
import various.TypeDef._

object Exec {
  case class AlgoParam(
    val algo: Map[String, String],
    val nObs: Index,
    val kerEval: (Index, Index) => Real,
    val rootFolder: String)
    
  /**
   * @return string that is empty on success, or that contains a description of the problems.
   */
  def main(workFolder: String): String = {
    val algoFile = workFolder + Def.folderSep + "algo.csv"
    val dataFile = workFolder + Def.folderSep + "data.csv"
    val descFile = workFolder + Def.folderSep + "desc.csv"

    val readAll = for {
      algo <- ReadAlgo.readAndParseFile(algoFile)
      data <- ReadVar.readAndParseVars(dataFile)
      param <- ReadParam.readAndParseParam(descFile)
      kerEval <- CombineVarParam.generateAllKerEval(data, param)
    } yield (AlgoParam(algo, data(0).data.nPoint, kerEval, workFolder))

    val res = readAll.flatMap(callAlgo)

    return res match {
      case Success(_) => ""
      case Failure(m) => m.toString
    }
  }

  /**
   * Call the correct algorithm.
   *
   * An algorithm will never return anything, but instead write its result on the hard drive. Thrown exception
   * are however captured and managed properly, hence the Try[Unit] return type.
   */
  def callAlgo(param: AlgoParam): Try[Unit] =
    algoExistence(param).flatMap(a => a match {
//      case _ if (a == "changepoint") => ???
//      case _ if (a == "diffdist") => ???
//      case _ if (a == "kmeans") => ???
//      case _ if (a == "regression") => ???
      case _ if (a == "svm") => ???
      case _ => Failure(new Exception(s"Algorithm $a not implemented yet."))
    })

  def algoExistence(param: AlgoParam): Try[AlgoParam] = {
    if (param.algo.contains("algo"))
      Success(param)
    else
      Failure(new Exception("Algorithm name not found in algo.csv."))
  }
}