package p06sandbox

object Regex {
  def main = {
    val GaussianPattern = raw"Gaussian\(([0-9.]+)\)".r
    val ProductPattern = raw"Product\(()\)".r
    
    val str0 = "Gaussian(1.0)"
    val str1 = "Product()"
    
    str1 match {
      case GaussianPattern(c) => println("Gaussian, param: " + c)
      case ProductPattern(c) => println("Product")
    }
  }
}