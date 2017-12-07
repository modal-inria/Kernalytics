import collection.mutable.Stack
import org.scalatest._
import p04various.Math
import p04various.TypeDef._

//class ExampleSpec2 extends FlatSpec with Matchers {
//  "factorial" should "compute factorial values correctly" in {
//    val expectedAnswer: List[Integer] = List(1, 1, 2, 6, 24, 120)
//    val computedAnswer: List[Integer] = (0 to 5).toList.map(Math.factorial)
//
//    (computedAnswer) should be (expectedAnswer)
//  }
//  
//  "binomial" should "compute binomial"  in {
//    (Math.binomial(5, 0)) should be (1)
//    (Math.binomial(3, 7) + Math.binomial(3, 8)) should be (Math.binomial(4, 8))
//  }
//}

class ExampleSpec extends FlatSpec with Matchers {

  "A Stack" should "pop values in last-in-first-out order" in {
    val stack = new Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be (2)
    stack.pop() should be (1)
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new Stack[Int]
    a [NoSuchElementException] should be thrownBy {
      emptyStack.pop()
    } 
  }
}