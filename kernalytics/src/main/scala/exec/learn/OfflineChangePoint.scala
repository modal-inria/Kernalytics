package exec.learn

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.util.{ Try, Success}
import offlinechangepoint.{NumberSegmentSelection, Segmentation}
import various.Def
import various.TypeDef._
import exec.Learn
import exec.Param

object OfflineChangePoint {
  val tauFileName = "paramTau.csv"
  
  def main(param: Learn.AlgoParam): Try[Unit] = {
    val res = for {
      DMax <- getDMax(param)
      resSegmentation <- Success(Segmentation.loopOverTauP(param.kerEval.nObs, param.kerEval.k, DMax))
      resSelection <- Success(NumberSegmentSelection.optimalNumberSegments(resSegmentation, param.kerEval.nObs, None))
      resWrite <- writeResults(param.rootFolder, resSelection)
    } yield resWrite

    return res
  }

  /**
   * Check that the parameter DMax has been provided, is convertible and strictly positive.
   */
  def getDMax(param: Learn.AlgoParam): Try[Index] =
    Param.existence(param, "DMax")
      .flatMap(DMax => Try(param.algo("DMax").toIndex))
      .flatMap(Param.indexStricltyPositive(_, "DMax"))

  /**
   * Write the result the list of change points in the optimal solution.
   */
  def writeResults(rootFolder: String, res: Array[Index]): Try[Unit] = {
    val outFile = rootFolder + Def.folderSep + tauFileName

    val data = res.mkString(Def.eol)

    return Try(FileUtils.writeStringToFile(new File(outFile), data, "UTF-8"))
  }
}