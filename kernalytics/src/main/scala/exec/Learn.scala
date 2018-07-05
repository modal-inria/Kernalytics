package exec

import scala.util.{ Try, Success, Failure }
import io.{ CombineVarParam, ReadAlgo, ReadParam, ReadVar }
import rkhs.{ GramOpti, KerEval }
import various.Def
import various.TypeDef._
import exec.learn.KMeans
import exec.learn.OfflineChangePoint
import exec.learn.Regression
import exec.learn.SVM
import exec.learn.TwoSampleTest

object Learn {
  val algoFileName = "algo.csv"
  val dataFileName = "learnData.csv"
  val descFileName = "desc.csv"

  val gramOptiName = "gramOpti"

  case class AlgoParam(
    val algo: Map[String, String],
    val kerEval: KerEval,
    val rootFolder: String)

  /**
   * @return string that is empty on success, or that contains a description of the problems.
   */
  def main(rootFolder: String): String = {
    val algoFile = rootFolder + Def.folderSep + algoFileName
    val dataFile = rootFolder + Def.folderSep + dataFileName // the data used in the KerEval is always the data from the learning phase
    val descFile = rootFolder + Def.folderSep + descFileName

    val readAll = for {
      algo <- ReadAlgo.readAndParseFile(algoFile)
      cg <- cacheGram(algo)
      (data, nObs) <- ReadVar.readAndParseVars(dataFile)
      param <- ReadParam.readAndParseParam(descFile)
      kerEval <- CombineVarParam.generateGlobalKerEval(nObs, 0, data, param, cg) // the assumption here is that every algorithm need the complete Gram matrix
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
      case _ => Failure(new Exception(s"Learn mode not available for algorithm $a."))
    })

  def algoExistence(param: AlgoParam): Try[AlgoParam] = {
    if (param.algo.contains("algo"))
      Success(param)
    else
      Failure(new Exception(s"Algorithm name not found in $algoFileName."))
  }

  def parseParam(str: String): Try[(String, String)] = {
    val paramPattern = raw"([a-zA-Z0-9]+)\((.*)\)".r
    val t = Try({ val t = paramPattern.findAllIn(str); (t.group(1), t.group(2)) })

    t match {
      case Success(_) => t
      case Failure(_) => Failure(new Exception(str + " is not a valid parameter String")) // default exception for pattern matching is not expressive enough
    }
  }

  def cacheGram(param: Map[String, String]): Try[GramOpti] = {
    val paramPattern = raw"([a-zA-Z0-9]+)\((.*)\)".r

    if (param.contains(gramOptiName)) {
      val rawStr = param(gramOptiName)
      val t = Try({ val t = paramPattern.findAllIn(rawStr); (t.group(1), t.group(2)) })
      t match {
        case Success(("Direct", "")) => GramOpti.Direct()
        case Success(("Cache", "")) => GramOpti.Cache()
        case Success(("LowRank", m)) => Try(m.toIndex).map(mIndex => GramOpti.LowRank(mIndex))
        case _ => Failure(new Exception(s"Could not parse $gramOptiName entry: "))
      }
    } else
      Failure(new Exception(s"$algoFileName must define cacheGram"))
  }
}