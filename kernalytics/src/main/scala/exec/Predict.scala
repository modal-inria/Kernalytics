package exec

import scala.util.{ Try, Success, Failure }
import io.{ CombineVarParam, ReadAlgo, ReadParam, ReadVar }
import rkhs.KerEval
import various.Def
import various.TypeDef._
import exec.predict.Regression

/**
 * TODO: merge learn and predict observations in a bigger KerEval.
 */
object Predict {
  val algoFileName = "algo.csv"
  val dataFileLearnName = "learnData.csv"
  val dataFilePredictName = "predictData.csv"
  val descFileName = "desc.csv"
  
  case class AlgoParam(
    val algo: Map[String, String],
    val kerEval: KerEval,
    val rootFolder: String)
    
  /**
   * @return string that is empty on success, or that contains a description of the problems.
   */
  def main(rootFolder: String): String = {
    val algoFile = rootFolder + Def.folderSep + algoFileName
    val dataLearnFile = rootFolder + Def.folderSep + dataFileLearnName // the data used in the KerEval is always the data from the learning phase
    val dataPredictFile = rootFolder + Def.folderSep + dataFilePredictName // the data used in the KerEval is always the data from the learning phase
    val descFile = rootFolder + Def.folderSep + descFileName

    val readAll = for {
      algo <- ReadAlgo.readAndParseFile(algoFile)
      data <- ReadVar.readAndParseVars2Files(dataLearnFile, dataPredictFile)
      param <- ReadParam.readAndParseParam(descFile)
      nPoint <- Success(data(0).data.nPoint)
      kerEval <- CombineVarParam.generateGlobalKerEval(nPoint, data, param) // the assumption here is that every algorithm need the complete Gram matrix
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
//      case "offlinechangepoint" => OfflineChangePoint.main(a)
//      case "twosampletest" => TwoSampleTest.main(a)
//      case "kmeans" => KMeans.main(a)
      case "regression" => Regression.main(a)
//      case "svm" => SVM.main(a)
      case _ => Failure(new Exception(s"Prediction mode not available for algorithm $a."))
    })

  def algoExistence(param: AlgoParam): Try[AlgoParam] = {
    if (param.algo.contains("algo"))
      Success(param)
    else
      Failure(new Exception(s"Algorithm name not found in $algoFileName."))
  }
}