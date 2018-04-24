package sandbox

object Scan {
  def main {
    val data = Array(1, 3, 5)
    val res = data.scanRight(0)(_ + _).dropRight(1).reverse
    println(res.mkString(" "))
  }
}