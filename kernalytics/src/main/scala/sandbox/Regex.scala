package sandbox

import scala.util.{Try, Success, Failure}

object Regex {
  def main = {
//    val GaussianPattern = raw"Gaussian\(([0-9.]+)\)".r
//    val ProductPattern = raw"Product\(()\)".r
//    
//    val str0 = "Gaussian(1.0)"
//    val str1 = "Product()"
//    
//    str1 match {
//      case GaussianPattern(c) => println("Gaussian, param: " + c)
//      case ProductPattern(c) => println("Product")
//    }
//    
//    println(GaussianPattern.findAllIn("Gaussian(1.0)").groupCount)
//    
    val paramPattern = raw"([a-zA-Z0-9]+)\((.+)\)".r
//    val m = paramPattern.findAllIn("KernelName(13.4, 12.2)")
//    println(m.group(0))
//    println(m.group(1))
//    println(m.group(2))
//
//    val t = Try(paramPattern.findAllIn("KernelName(13.4, 12.2)"))
    
    val t = Try({val t = paramPattern.findAllIn("KernelName(13.4, 12.2)"); (t.group(1), t.group(2))})
//    val t = Try({val t = paramPattern.findAllIn("toto"); (t.group(1), t.group(2))})
    
    t match {
      case Success(s) => println(s)
      case Failure(f) => println(f)
    }
  }
}