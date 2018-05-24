package exec.learn

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils
import scala.util.{ Try, Success}
import kmeans.IO
import various.Def
import various.TypeDef._
import exec.Learn
import exec.Param

object KMeans {
  val alphaFileName = "paramAlpha.csv"
  val labelFileName = "learnLabels.csv"
  
  def main(param: Learn.AlgoParam): Try[Unit] = {
    val res = for {
      nClass <- getNClass(param)
      nIteration <- getNIteration(param)
      resAlgo <- Success(IO.runKMeans(param.kerEval, nClass, nIteration))
      resWrite <- writeResults(param.rootFolder, resAlgo)
    } yield resWrite

    return res
  }

  def getNClass(param: Learn.AlgoParam): Try[Index] =
    Param.existence(param, "nClass")
      .flatMap(C => Try(param.algo("nClass").toIndex))
      .flatMap(Param.indexStricltyPositive(_, "nClass"))

  def getNIteration(param: Learn.AlgoParam): Try[Index] =
    Param.existence(param, "nIteration")
      .flatMap(C => Try(param.algo("nIteration").toIndex))
      .flatMap(Param.indexStricltyPositive(_, "nIteration"))

  def writeResults(rootFolder: String, res: IO.ReturnValue): Try[Unit] = {
    val modelFile = rootFolder + Def.folderSep + alphaFileName
    val labelFile = rootFolder + Def.folderSep + labelFileName
    val data = res.labels.data.map(_.toString).mkString(Def.eol)

    return for {
      _ <- Try(csvwrite(new File(modelFile), res.model, separator = Def.csvSep(0)))
      _ <- Try(FileUtils.writeStringToFile(new File(labelFile), data, "UTF-8"))
    } yield ()
  }
}