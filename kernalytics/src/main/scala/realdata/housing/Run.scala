package realdata.housing

object Run {
  val rootFolder = "realdata/housing"
  
  def main {
    val res = exec.Learn.main(rootFolder)
    println(res)
  }
}