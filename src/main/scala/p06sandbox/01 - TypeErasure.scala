package p06sandbox

import breeze.linalg._
import scala.reflect._
// val ct = classTag[String]

import p00rkhs.KerEval._
import p04various.TypeDef._

object TypeErasure {
//  def print(data: DenseVectorRoot) =
//    data match {
//      case DenseVectorMatrixReal(_) => println("Contains real matrices")
//      case DenseVectorReal(_) => println("Contains real scalars")
//    }

//  def print2(data: Array[_ <: Any]) =
//    data match {
//      case _: Array[Double] => println("Contains real matrices")
//      case _: Array[Int] => println("Contains real scalars")
//    }
  
//  def print2(data: DenseVector[_ <: Any]) =
//    data match {
//      case _: DenseVector[Double] => println("Contains real matrices")
//      case _: DenseVector[Int] => println("Contains real scalars")
//      case _ => println("Type argument detection failed")
//    }
  
  def main {
//    val a0 = Array(1.2, 1.5, 1.6)
//    val a1 = Array(3, 4, 5)
//    
//    val l = List(a0, a1)
//    l.map(print2)
//    
//    val v0 = DenseVector(1.2, 1.5, 1.6)
//    val v1 = DenseVector(3, 4, 5)
//    
//    val a = Array(v0, v1)
//    a.map(print2)
  }
}