import breeze.linalg._
import breeze.stats.distributions._
import collection.mutable.Stack
import org.scalactic._
import org.scalatest._
import p04various.Math
import p04various.TypeDef._

object Staging extends App {  
  val n = 10
  val k = DenseVector[Index](0, 1, 2, 3)
  
  val direct = k.map(Math.logBinomialExact(n, _))
  val approximate = k.map(Math.logBinomial(n, _))
  
  println(s"direct: $direct")
  println(s"approximate: $approximate")
}