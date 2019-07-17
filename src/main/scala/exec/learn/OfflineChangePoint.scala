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

  def main(param: Learn.AlgoParam): Try[Unit] = {
    val res = for {
      DMax <- getDMax(param)
      _ <- Segmentation.inputCheck(param.kerEval.nObs, DMax)
      resSegmentation <- Success(Segmentation.loops(param.kerEval.nObs, param.kerEval.k, DMax, true))
      resSelection <- Success(NumberSegmentSelection.penalizedCostComputation(resSegmentation, param.kerEval.nObs))
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
  def writeResults(rootFolder: String, res: NumberSegmentSelection.penalizedCosts): Try[Unit] = {
    val DMax = res.segPoints.length // no - 1, because the case D = 0 has been ignored
    
    val outFile = rootFolder + Def.folderSep + tauFileName

    val header = Array("D", "tau", "raw cost", "regressed cost", "penalized cost").mkString(Def.csvSep)

    val rawData = res.rawCost.map(_.toString)
    val regData = res.regCost.toArray.map(_.toString)
    val penData = res.penCost.toArray.map(_.toString)
    
    val DStr = (1 to DMax).map(_.toString).toArray
    
    val tauStr = res.segPoints.map(_.mkString(","))

    val allData = header + Def.eol + Array(DStr, tauStr, rawData, regData, penData).transpose.map(_.mkString(Def.csvSep)).mkString(Def.eol)

    return Try(FileUtils.writeStringToFile(new File(outFile), allData, "UTF-8"))
  }
}