package various

object TypeDef {
  type Index = Int
  val Index = Int

  type Integer = Int
  val Integer = Int

  type Real = Double
  val Real = Double // to have companion object of Double also be the companion object of Real

  trait Convertible[A] {
    def toIndex(a: A): Index
    def toInteger(a: A): Integer
    def toReal(a: A): Real
  }

  implicit val ConvertibleIndex: Convertible[Index] = new Convertible[Index] {
    def toIndex(i: Index) = i
    def toInteger(i: Index) = i
    def toReal(i: Index) = i.toDouble
  }

  implicit val ConvertibleInteger: Convertible[Integer] = new Convertible[Integer] {
    def toIndex(i: Integer) = i
    def toInteger(i: Integer) = i
    def toReal(i: Integer) = i.toDouble
  }

  implicit val ConvertibleReal: Convertible[Real] = new Convertible[Real] {
    def toIndex(r: Real) = r.toInt
    def toInteger(r: Real) = r.toInt
    def toReal(r: Real) = r
  }

  implicit val ConvertibleString: Convertible[String] = new Convertible[String] {
    def toIndex(s: String) = s.toInt
    def toInteger(s: String) = s.toInt
    def toReal(s: String) = s.toReal
  }

  implicit class ConvertibleSyntax[A](a: A)(implicit val c: Convertible[A]) {
    def toIndex = c.toIndex(a)
    def toInteger = c.toInteger(a)
    def toReal = c.toReal(a)
  }
}