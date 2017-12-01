package p05offlinechangepoint

import breeze.linalg.{csvwrite, linspace, max, DenseVector, DenseMatrix}
import breeze.numerics._
import breeze.plot._
import java.io.File
import p00rkhs.{Gram, Kernel}
import p04various.TypeDef._

/**
 * Read multivariate external data from csv, instead of generating it.
 */
object ExternalData {
  def readData(fileName: String): Array[String] = {
    return Array[String]()
  }
}