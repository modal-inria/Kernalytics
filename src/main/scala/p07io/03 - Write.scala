package p07io

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils

import p04various.Def
import p04various.TypeDef._

object Write {
  def csv(
      fileName: String,
      name: String,
      data: DenseVector[Real]) = {
    val outputStr = Array(name, "Real", data.toArray.mkString(Def.eol)).mkString(Def.eol)
    FileUtils.writeStringToFile(new File(fileName), outputStr, "UTF-8")
  }
}