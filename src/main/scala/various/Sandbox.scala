package various

object Sandbox {  
  def main {
    (0 to 10).map(i => (i, Math.factorial(i))).foreach(println)
  }
}