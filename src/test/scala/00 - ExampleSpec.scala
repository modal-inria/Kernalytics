import collection.mutable.Stack
import org.scalactic._
import org.scalatest._
import p04various.Math
import p04various.TypeDef._

// http://www.scalatest.org/user_guide/using_matchers

class ExampleSpec extends FlatSpec with Matchers {
  "factorial" should "compute factorial values correctly" in {
    (0 to 5).toList.map(Math.factorial) should === (List(1, 1, 2, 6, 24, 120))
  }
  
  "binomial" should "compute binomial"  in {
    (Math.binomial(5, 0)) should === (1)
    (Math.binomial(7, 3) + Math.binomial(7, 4)) should === (Math.binomial(8, 4))
  }
}