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
import p00rkhs.KerEval.DenseVectorReal

/**
 * Test IO, using data present on file.
 */
class IOSpec extends FlatSpec with Matchers {
	"parseData" should "read correctly formatted data on drive" in {
	  val fileName = "data/p07io/00 - 1VarReal.csv"
	  
	  val data = Read.readAndParse(fileName)
	  val zeroVec = DenseVector.zeros[Real](4)
	  
	  val (nameString, dataVector) = data match {
	    case Failure(m) => (m.getMessage, zeroVec)
	    case Success(s) => (
	        s(0).name,
	        s(0).data match {
	          case DenseVectorReal(vec) => vec
	          case _ => zeroVec
	        })
	    }
	  
	  	val expectedVec = DenseVector[Real](
	  			3.0,
	  			5.6,
	  			2.453453,
	  			7.9856)
	  
	  nameString should === ("MyGaussianData")
	  norm(expectedVec - dataVector) should === (0.0 +- 1.0e-8)
	}
}