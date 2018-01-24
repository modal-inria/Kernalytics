package various

object TypeDef {
  type Index = Int
  val Index = Int
  
  type Integer = Int
  val Integer = Int
  
  type Real = Double
  val Real = Double // to have companion object of Double also be the companion object of Real
}