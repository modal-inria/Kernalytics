package spec.rkhsspec

import org.scalatest._
import scala.util.{ Try, Success, Failure }

import rkhs.IO

object IOSpec {
  class IOSpec extends FlatSpec with Matchers {
    "parseParam" should "parse a correct parameter string." in {
      val validStr = "Gaussian(12.3)"

      val res = IO.parseParam(validStr)

      res should ===(Success(("Gaussian", "12.3")))
    }

    "parseParam" should "reject an incorrectly formatted string." in {
      val str = "toto"

      val res = IO.parseParam(str)

      val (errorMessage, parsedData) = res match {
        case Failure(m) => (m.toString, (-1, -1))
        case Success(s) => ("", s)
      }

      errorMessage should ===("java.lang.Exception: toto is not a valid parameter String")
    }
  }
}