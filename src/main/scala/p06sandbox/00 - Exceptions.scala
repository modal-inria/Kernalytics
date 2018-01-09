package p06sandbox

import scala.io.Source
import scala.util.{Try, Success, Failure}

import p04various.TypeDef._

/**
 * Some code to understand Scala exceptions handling.
 */
object Exceptions {
	def readTextFile(filename: String): Try[List[String]] = {
	  Try(Source.fromFile(filename).getLines.toList)
	}
	
	def div(data: List[String]): Try[Index] =
//	  Try(1 / 0)
	  Try(10 / 2)
//	  Try(- 10 / 2)

	def mySqrt(i: Index): Try[Real] =
	  if (0 <= i) Success(math.sqrt(i)) else Failure(new Exception("Can not compute square of negative int"))

	def mapAccess(i: Index): Try[Index] = {
	  val typeMap: Map[Index, Index] = Map(25 -> 12)
	  return Try(typeMap(i))
	}
	  
	def main {
//	  val filename = "/file/which/does/not/exists"
	  	val filename = "data/p06sandbox/00 - Exceptions/data.txt"
	  
	  val throwable =
	    readTextFile(filename)
	    .flatMap(div)
	    .flatMap(mapAccess)
	    .flatMap(mySqrt)
	    .map(_ * -10.0)
	    
	  throwable match {
	    case Success(l) => println(l)
	    case Failure(f) => println(f)
	  }
	}
}