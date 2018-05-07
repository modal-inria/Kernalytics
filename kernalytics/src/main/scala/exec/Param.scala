package exec

import scala.util.{ Try, Success, Failure }

import various.Def
import various.TypeDef._

object Param {
  def existence(param: Exec.AlgoParam, name: String): Try[Exec.AlgoParam] = {
    if (param.algo.contains(name))
      Success(param)
    else
      Failure(new Exception(s"$name parameter not found in algo.csv."))
  }

  def realPositive(C: Real, name: String): Try[Real] = {
    if (0.0 <= C)
      Success(C)
    else
      Failure(new Exception(s"$name must be positive."))
  }

  def realStricltyPositive(C: Real, name: String): Try[Real] = {
    if (Def.epsilon < C)
      Success(C)
    else
      Failure(new Exception(s"$name must be strictly positive."))
  }

  def indexStricltyPositive(DMax: Index, name: String): Try[Index] = {
    if (0 < DMax)
      Success(DMax)
    else
      Failure(new Exception(s"$name must be strictly positive."))
  }
}