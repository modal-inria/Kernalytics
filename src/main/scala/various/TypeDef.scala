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

  implicit class ConvertibleInt(i: Int)
    extends Convertible {
    override def toIndex = i
    override def toInteger = i
    override def toReal = i.toDouble
  }

  implicit class ConvertibleDouble(d: Double)
    extends Convertible {
    override def toIndex = d.toInt
    override def toInteger = d.toInt
    override def toReal = d
  }

  implicit class ConvertibleString(s: String)
    extends Convertible {
    override def toIndex = s.toInt
    override def toInteger = s.toInt
    override def toReal = s.toDouble
  }
}