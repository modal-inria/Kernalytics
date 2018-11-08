package exec

import java.io.File
import scala.util.{ Try, Success, Failure }
import org.apache.commons.io.FileUtils

import io.{ CombineVarParam, ReadAlgo, ReadParam, ReadVar }
import rkhs.KerEval
import various.Def
import various.TypeDef._
import exec.predict.{ Regression, SVM }
import rkhs.GramOpti

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
   * Note that cacheGram is not parsed at all in prediction.
   * 
   * @return string that is empty on success, or that contains a description of the problems.
   */
  def main(rootFolder: String) = {
    val algoFile = rootFolder + Def.folderSep + algoFileName
    val dataLearnFile = rootFolder + Def.folderSep + dataFileLearnName // the data used in the KerEval is always the data from the learning phase
    val dataPredictFile = rootFolder + Def.folderSep + dataFilePredictName // the data used in the KerEval is always the data from the learning phase
    val descFile = rootFolder + Def.folderSep + descFileName

    val readAll = for {
      algo <- ReadAlgo.readAndParseFile(algoFile)
      (data, nObsLearn, nObsPredict) <- ReadVar.readAndParseVars2Files(dataLearnFile, dataPredictFile)
      param <- ReadParam.readAndParseParam(descFile)
      kerEval <- CombineVarParam.generateGlobalKerEval(nObsLearn, nObsPredict, data, param, GramOpti.Direct()) // the assumption here is that every algorithm need the complete Gram matrix
    } yield (AlgoParam(algo, kerEval, rootFolder))

    val res = readAll.flatMap(callAlgo)

    res match {
      case Success(_) => {}
      case Failure(m) => FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "error.txt"), m.toString, "UTF-8")
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
      case "svm" => SVM.main(a)
      case _ => Failure(new Exception(s"Prediction mode not available for algorithm $a."))
    })

  def algoExistence(param: AlgoParam): Try[AlgoParam] = {
    if (param.algo.contains("algo"))
      Success(param)
    else
      Failure(new Exception(s"Algorithm name not found in $algoFileName."))
  }
}