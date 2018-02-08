package offlinechangepoint

import breeze.linalg.{csvwrite, linspace, max, DenseVector, DenseMatrix}
import breeze.numerics._
import breeze.plot._
import java.io.File
import rkhs.{Gram, Kernel}
import various.TypeDef._

/**
 * Read multivariate external data from csv, instead of generating it.
 */
object ExternalData {
  def writeData(fileName: String,
     ): Array[String] = {
    return Array[String]()
  }
}