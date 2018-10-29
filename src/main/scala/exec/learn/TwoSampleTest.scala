package exec.learn

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.util.{ Try, Success}
import algo.twosampletest.IO
import various.Def
import various.TypeDef._
import exec.Learn
import exec.Param

object TwoSampleTest {
  val nullDataFile = "learnNull.csv"
  
  def main(param: Learn.AlgoParam): Try[Unit] = {
    val res = for {
      nA <- getNA(param)
      alpha <- getAlpha(param)
      resAlgo <- Success(IO.runTest(param.kerEval, nA, alpha))
      resWrite <- writeResults(param.rootFolder, resAlgo)
    } yield resWrite

    return res
  }

  def getNA(param: Learn.AlgoParam): Try[Index] =
    Param.existence(param, "nA")
      .flatMap(C => Try(param.algo("nA").toIndex))
      .flatMap(Param.indexStricltyPositive(_, "nA"))

  def getAlpha(param: Learn.AlgoParam): Try[Real] =
    Param.existence(param, "alpha")
      .flatMap(C => Try(param.algo("alpha").toReal))
      .flatMap(Param.realBounds(_, 0.0, 1.0, "alpha"))

  def writeResults(rootFolder: String, res: Boolean): Try[Unit] = {
    val modelFile = rootFolder + Def.folderSep + nullDataFile
    return Try(FileUtils.writeStringToFile(new File(modelFile), res.toString, "UTF-8"))
  }
}