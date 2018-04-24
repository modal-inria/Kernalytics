package various

import java.io.File
import util.Properties

object Def {
  val eol = Properties.lineSeparator
  val folderSep = File.separator
  val csvSep = ";"
  val optionSep = ","
  val cacheGram = true
  
  val epsilon = 1e-8
}