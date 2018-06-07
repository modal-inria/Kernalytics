package algo.svm.examples

import breeze.linalg._
import java.io.File
import org.apache.commons.io.FileUtils

import various.Def
import various.TypeDef._

/**
 * A simple two dimensional case linear case, with a few vectors, and for which the analytical solution is known.
 * See http://axon.cs.byu.edu/Dan/678/miscellaneous/SVM.example.pdf
 */
object SimpleCase2D {
  val rootFolder = "data/exec/svm"
  val varName = "v1"
  val varType = "VectorReal,2"

  def writeData {
    val nPoints = 1000
    val xLim = (0.0, 10.0)
    val yLim = (0.0, 10.0)

    val C: Real = 1000 // large value to penalize non compliance with margins

    val data = Array.fill[Array[Real]](nPoints)({
      ???
    })

    val y = data.map(computeY)

    val dataStr = data.map(a => a.mkString(Def.optionSep)).mkString(Def.eol)
    val xLearnStr = varName + Def.eol + varType + Def.eol + dataStr
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "learnData.csv"), xLearnStr, "UTF-8")

    val yLearnStr = y.mkString(Def.eol)
    FileUtils.writeStringToFile(new File(rootFolder + Def.folderSep + "learnY.csv"), yLearnStr, "UTF-8")
  }

  def computeY(point: Array[Real]): Real = {
    ???
  }

  def writeConfig {

  }
}