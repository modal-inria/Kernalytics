package exec.learn

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.util.{ Try, Success }
import algo.offlinechangepoint.{ Segmentation }
import algo.offlinechangepoint.NumberSegmentSelection
import various.Def
import various.TypeDef._
import exec.Learn
import exec.Param

object OfflineChangePoint {
  val tauFileName = "paramTau.csv"
  val costFileName = "learnCost.csv"

  def main(param: Learn.AlgoParam): Try[Unit] = {
    val res = for {
      DMax <- getDMax(param)
      resSegmentation <- Success(Segmentation.loopOverTauP(param.kerEval.nObs, param.kerEval.k, DMax))
      resSelection <- Success(NumberSegmentSelection.optimalNumberSegments(resSegmentation, param.kerEval.nObs))
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
  def writeResults(rootFolder: String, res: NumberSegmentSelection.optimalNumberSegmentsReturn): Try[Unit] = {
    val outFile = rootFolder + Def.folderSep + tauFileName
    val costFile = rootFolder + Def.folderSep + costFileName

    val segPointsStr = res.segPoints.mkString(Def.eol)

    val header = Array("raw", "regressed", "penalized").mkString(Def.csvSep)

    val rawData = res.rawCost.map(_.toString)
    val regData = res.regCost.toArray.map(_.toString)
    val penData = res.penCost.toArray.map(_.toString)

    val allData = header + Def.eol + Array(rawData, regData, penData).transpose.map(_.mkString(Def.csvSep)).mkString(Def.eol)

    return Try(FileUtils.writeStringToFile(new File(outFile), segPointsStr, "UTF-8"))
      .flatMap(_ => Try(FileUtils.writeStringToFile(new File(costFile), allData, "UTF-8")))
  }
}