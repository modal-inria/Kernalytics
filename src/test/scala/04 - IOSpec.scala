import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._
import scala.util.{Try, Success, Failure}

import p00rkhs.{Algebra, KerEval, Kernel}
import p04various.TypeDef._
import p05offlinechangepoint.{CostMatrix, Test}
import p07io.Read

/**
 * Test IO, using data present on file.
 */
class IOSpec extends FlatSpec with Matchers {
	"parseData" should "read correctly formatted data on drive" in {
	  val fileName = "data/p07io/00 - 1VarReal.csv"
	  
//	  val dataNoParse = Read.readNoParse(fileName)
//	  println(s"dataNoParse, size: ${dataNoParse.size}.")
	  
	  val data = Read.readAndParse(fileName)
	  
	  val varName =
	    data match {
	    case Failure(m) => println(m)
	    case Success(s) => println(s"Success, size: ${s.size}.")
	  }
	}
}