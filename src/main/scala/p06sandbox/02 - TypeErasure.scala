//package p06sandbox
//
//import breeze.linalg._
//import scala.reflect._
//import scala.reflect.runtime.universe._
//
//import p00rkhs.KerEval._
//import p04various.TypeDef._
//
///**
// * Inspiration for this architecture comes from: https://stackoverflow.com/questions/48169051/how-to-avoid-type-argument-erasure
// * A detailed example of type classes for (simple) numerical computations: http://danielwestheide.com/blog/2013/02/06/the-neophytes-guide-to-scala-part-12-type-classes.html
// */
//object TypeClasses {
//  trait DenseContent[T] {
//    def compute(v: DenseVector[T]): String
////    def fromString(s: String): DenseVector[T]
//  }
//  
//  object DenseContent {
//    implicit object _Real extends DenseContent[Real] {
//      def compute(v: DenseVector[Real]) = "real"
//    }
//    implicit object _Int extends DenseContent[Int] {
//      def compute(v: DenseVector[Int]) = "int"
//    }
//    // etc ...
//  }
//  
//  def print2[T : DenseContent](data: DenseVector[T]) = println(
//     implicitly[DenseContent[T]].compute(data)
//  )
//}
//
////def printType[A: TypeTag](a: List[A]): Unit = 
////  println(if(typeTag[A] == typeTag[Double]) "Double" else "other")
////
////printType(List(1.0))
////printType(List(1))
//
//object TypeErasure {
////  def print(data: DenseVectorRoot) =
////    data match {
////      case DenseVectorMatrixReal(_) => println("Contains real matrices")
////      case DenseVectorReal(_) => println("Contains real scalars")
////    }
//
////  def print2(data: Array[_ <: Any]) =
////    data match {
////      case _: Array[Double] => println("Contains real matrices")
////      case _: Array[Int] => println("Contains real scalars")
////    }
//  
////  def print2(data: DenseVector[_ <: Any]) =
////    data match {
////      case _: DenseVector[Double] => println("Contains real matrices")
////      case _: DenseVector[Int] => println("Contains real scalars")
////      case _ => println("Type argument detection failed")
////    }
//  
//  def main {
////    val a0 = Array(1.2, 1.5, 1.6)
////    val a1 = Array(3, 4, 5)
////    
////    val l = List(a0, a1)
////    l.map(print2)
//    
//    val v0 = DenseVector(1.2, 1.5, 1.6)
//    val v1 = DenseVector(3, 4, 5)
//    
//    val a = Array(v0, v1)
////    a.map(TypeClasses.print2)
//  }
//}