package various

import scala.util.{ Try, Success, Failure }

object Error {
  def validate[A](a: A, b: Boolean, message: String): Try[A] =
    if (b)
      Success(a)
    else
      Failure(new Exception(message))
}