package exec

import scala.util.{ Try, Success, Failure }
import io.{ CombineVarParam, ReadAlgo, ReadParam, ReadVar }
import rkhs.KerEval
import various.Def
import various.TypeDef._

object Exec {
  case class AlgoParam(
    val algo: Map[String, String],
    val kerEval: KerEval,
    val rootFolder: String)
    
  /**
   * @return string that is empty on success, or that contains a description of the problems.
   */
  def main(rootFolder: String): String = {
    val algoFile = rootFolder + Def.folderSep + "algo.csv"
    val dataFile = rootFolder + Def.folderSep + "dataLearn.csv" // the data used in the KerEval is always the data from the learning phase
    val descFile = rootFolder + Def.folderSep + "desc.csv"

    val readAll = for {
      algo <- ReadAlgo.readAndParseFile(algoFile)
      data <- ReadVar.readAndParseVars(dataFile)
      param <- ReadParam.readAndParseParam(descFile)
      kerEval <- CombineVarParam.generateAllKerEval(data(0).data.nPoint, data, param)
    } yield (AlgoParam(algo, kerEval, rootFolder))

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
    algoExistence(param).flatMap(a => a.algo("algo") match {
      case "offlinechangepoint" => OfflineChangePoint.main(a)
      case "twosampletest" => TwoSampleTest.main(a)
      case "kmeans" => KMeans.main(a)
      case "regression" => Regression.main(a)
      case "svm" => SVM.main(a)
      case _ => Failure(new Exception(s"Algorithm $a not implemented yet."))
    })

  def algoExistence(param: AlgoParam): Try[AlgoParam] = {
    if (param.algo.contains("algo"))
      Success(param)
    else
      Failure(new Exception("Algorithm name not found in algo.csv."))
  }
}