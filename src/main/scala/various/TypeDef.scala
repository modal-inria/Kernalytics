package various

/**
 * Implementation comes from https://stackoverflow.com/questions/49283384/implicit-conversion-not-performed-on-int
 */
object TypeDef {
  type Index = Int
  val Index = Int

  type Integer = Int
  val Integer = Int

  type Real = Double
  val Real = Double

  trait Convertible {
    def toIndex: Index
    def toInteger: Integer
    def toReal: Real
  }

  implicit class ConvertibleInt(i: Int) extends Convertible {
    def toIndex = i
    def toInteger = i
    def toReal = i.toDouble
  }

  implicit class ConvertibleDouble(d: Double) extends Convertible {
    def toIndex = d.toInt
    def toInteger = d.toInt
    def toReal = d
  }

  implicit class ConvertibleString(s: String) extends Convertible {
    def toIndex = s.toInt
    def toInteger = s.toInt
    def toReal = s.toDouble
  }
}