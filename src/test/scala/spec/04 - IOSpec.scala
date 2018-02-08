package spec;
import breeze.linalg._
import breeze.numerics._
import breeze.stats.distributions._
import org.scalactic._
import org.scalatest._
import scala.util.{Try, Success, Failure}

import rkhs.{Algebra, KerEval, Kernel}
import various.TypeDef._
import offlinechangepointlegacy.{CostMatrix, Test}
import io.ReadVar

/**
 * Test IO, using data present on file.
 */
class IOSpec extends FlatSpec with Matchers {
	"parseData" should "read correctly formatted data on drive" in {
	  val fileName = "data/io/00 - 1VarReal.csv"
	  
	  val data = ReadVar.readAndParseVars(fileName)
	  val zeroVec = DenseVector.zeros[Real](4)
	  
	  val (nameString, dataVector) = data match {
	    case Failure(m) => (m.getMessage, zeroVec)
	    case Success(s) => (
	        s(0).name,
	        s(0).data match {
	          case KerEval.DenseVectorReal(vec) => vec
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
	
	"parseData" should "detect a malformed value" in {
	  val fileName = "data/io/01 - 1VarRealFormatError.csv"
	  
	  val data = ReadVar.readAndParseVars(fileName)
	  val zeroVec = DenseVector.zeros[Real](4)
	  
	  val (nameString, dataVector) = data match {
	    case Failure(m) => (m.toString, zeroVec)
	    case Success(s) => (
	        s(0).name,
	        s(0).data match {
	          case KerEval.DenseVectorReal(vec) => vec
	          case _ => zeroVec
	        })
	    }
	  
	  nameString should === ("java.lang.NumberFormatException: For input string: \"toto\"")
	}
	
  "parseData" should "detect a non existing data type" in {
	  val fileName = "data/io/02 - 1VarRealTypeError.csv"
	  
	  val data = ReadVar.readAndParseVars(fileName)
	  val zeroVec = DenseVector.zeros[Real](4)
	  
	  val (nameString, dataVector) = data match {
	    case Failure(m) => (m.toString, zeroVec)
	    case Success(s) => (
	        s(0).name,
	        s(0).data match {
	          case KerEval.DenseVectorReal(vec) => vec
	          case _ => zeroVec
	        })
	    }
	  
	  nameString should === ("scala.MatchError: NonExistingType (of class java.lang.String)")
  }

  "parseData" should "parse multivariate data" in {
	  val fileName = "data/io/03 - 2VarsReal.csv"
	  
	  val readData = ReadVar.readAndParseVars(fileName)
	  val errorData = Array[ReadVar.ParsedVar]()
	  val zeroVec = DenseVector.zeros[Real](4)
	  
	  val (errorMessage, parsedData) = readData match {
	    case Failure(m) => (m.toString, errorData)
	    case Success(s) => ("", s)
	  }
	  
	  errorMessage should === ("")
	  
    parsedData(0).name should === ("GaussData1")
    val parsedVec0 = parsedData(0).data match {
	    case KerEval.DenseVectorReal(vec) => vec
	    case _ => zeroVec
	  }
	  val expected0 = DenseVector[Real](
			  3.0,
			  5.6,
			  2.453453,
			  7.9856)
		norm(parsedVec0 - expected0) should === (0.0 +- 1.0e-8)
		
    parsedData(1).name should === ("GaussData2")
    val parsedVec1 = parsedData(1).data match {
	    case KerEval.DenseVectorReal(vec) => vec
	    case _ => zeroVec
	  }
	  val expected1 = DenseVector[Real](
			  12.0,
			  653.24,
			  -232.56,
			  1243.54)
		norm(parsedVec1 - expected1) should === (0.0 +- 1.0e-8)
  }
  
  "parseData" should "detect a type error in multivariate data" in {
	  val fileName = "data/io/04 - 2VarsRealTypeError.csv"
	  
	  val readData = ReadVar.readAndParseVars(fileName)
	  val errorData = Array[ReadVar.ParsedVar]()
	  val zeroVec = DenseVector.zeros[Real](4)
	  
	  val (errorMessage, parsedData) = readData match {
	    case Failure(m) => (m.toString, errorData)
	    case Success(s) => ("", s)
	  }
	  
	  errorMessage should === ("scala.MatchError: Rael (of class java.lang.String)")
  }
  
  "parseData" should "detect when multiple variables do not have the same number of observations" in {
	  val fileName = "data/io/05 - 2VarsUnevenObs.csv"
	  
	  val readData = ReadVar.readAndParseVars(fileName)
	  val errorData = Array[ReadVar.ParsedVar]()
	  val zeroVec = DenseVector.zeros[Real](4)
	  
	  val (errorMessage, parsedData) = readData match {
	    case Failure(m) => (m.toString, errorData)
	    case Success(s) => ("", s)
	  }
	  
	  errorMessage should === ("java.lang.Exception: All data must have the same number of observations.")
  }
  
  "parseData" should "detect when multiple variables have the same name" in {
	  val fileName = "data/io/06 - 2VarsSameName.csv"
	  
	  val readData = ReadVar.readAndParseVars(fileName)
	  val errorData = Array[ReadVar.ParsedVar]()
	  val zeroVec = DenseVector.zeros[Real](4)
	  
	  val (errorMessage, parsedData) = readData match {
	    case Failure(m) => (m.toString, errorData)
	    case Success(s) => ("", s)
	  }
	  
	  errorMessage should === ("java.lang.Exception: Variable names are not unique.")
  }
}