package exec

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.io.Source
import scala.util.{ Try, Success, Failure }

import kmeans.IO
import rkhs.KerEval
import various.Def
import various.TypeDef._

object KMeans {
  def main(param: Exec.AlgoParam): Try[Unit] = {
    val res = for {
      nClass <- getNClass(param)
      nIteration <- getNIteration(param)
      resAlgo <- Success(IO.runKMeans(param.kerEval, nClass, nIteration))
      resWrite <- writeResults(param.rootFolder, resAlgo)
    } yield resWrite

    return res
  }

  def getNClass(param: Exec.AlgoParam): Try[Index] =
    Param.existence(param, "nClass")
      .flatMap(C => Try(param.algo("nClass").toIndex))
      .flatMap(Param.indexStricltyPositive(_, "nClass"))

  def getNIteration(param: Exec.AlgoParam): Try[Index] =
    Param.existence(param, "nIteration")
      .flatMap(C => Try(param.algo("nIteration").toIndex))
      .flatMap(Param.indexStricltyPositive(_, "nIteration"))

  def writeResults(rootFolder: String, res: IO.ReturnValue): Try[Unit] = {
    val modelFile = rootFolder + Def.folderSep + "alpha.csv"
    val labelFile = rootFolder + Def.folderSep + "label.csv"
    val data = res.labels.data.map(_.toString).mkString(Def.eol)

    return for {
      _ <- Try(csvwrite(new File(modelFile), res.model, separator = Def.csvSep(0)))
      _ <- Try(FileUtils.writeStringToFile(new File(labelFile), data, "UTF-8"))
    } yield ()
  }
}