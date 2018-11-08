package debug

import breeze.linalg._
import scala.util.{ Try, Success, Failure }

import various.TypeDef._

object Sandbox extends App {
  val start:Index = 0
  val stop:Index = 134
  val n:Index = 11

  val percents = (linspace(0.0, 1.0, n) * 100.0).toArray.map(math.round)
  val steps = (linspace(0.0, 1.0, n) * stop.toReal).toArray.map(math.round)
  val all = steps.zip(percents)
  
  var nextStep = 0
  
  for (i <- start to stop) {
    if (i == all(nextStep)._1) {
      println(s"Progress: ${all(nextStep)._2}%")
      nextStep += 1
    }
  }
}
