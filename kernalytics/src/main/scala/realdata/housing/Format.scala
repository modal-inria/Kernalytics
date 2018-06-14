package realdata.housing

import scala.util.matching.Regex

import various.TypeDef._

object Format {
  val rootFolder = "realdata/housing/raw"
  val sampleLine = " 0.00632  18.00   2.310  0  0.5380  6.5750  65.20  4.0900   1  296.0  15.30 396.90   4.98  24.00"
  
  def parseLine(line: String): Array[String] = {
    val pattern = new Regex("[0-9.]+")
    pattern.findAllIn(line).toArray
  }
  
  def main {
    println("toto")
    parseLine(sampleLine).foreach(println)
  }
}