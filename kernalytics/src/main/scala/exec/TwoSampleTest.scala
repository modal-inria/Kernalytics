package exec

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.io.Source
import scala.util.{ Try, Success, Failure }

import twosampletest.IO
import rkhs.KerEval
import various.Def
import various.TypeDef._

object TwoSampleTest {
  val nullDataFile = "learnNull.csv"
  
  def main(param: Exec.AlgoParam): Try[Unit] = {
    val res = for {
      nA <- getNA(param)
      alpha <- getAlpha(param)
      resAlgo <- Success(IO.runTest(param.kerEval, nA, alpha))
      resWrite <- writeResults(param.rootFolder, resAlgo)
    } yield resWrite

    return res
  }

  def getNA(param: Exec.AlgoParam): Try[Index] =
    Param.existence(param, "nA")
      .flatMap(C => Try(param.algo("nA").toIndex))
      .flatMap(Param.indexStricltyPositive(_, "nA"))

  def getAlpha(param: Exec.AlgoParam): Try[Real] =
    Param.existence(param, "alpha")
      .flatMap(C => Try(param.algo("alpha").toReal))
      .flatMap(Param.realBounds(_, 0.0, 1.0, "alpha"))

  def writeResults(rootFolder: String, res: Boolean): Try[Unit] = {
    val modelFile = rootFolder + Def.folderSep + nullDataFile
    return Try(FileUtils.writeStringToFile(new File(modelFile), res.toString, "UTF-8"))
  }
}