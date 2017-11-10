package p04various

object TypeDef {
  type Index = Int
  val Index = Int
  
  type Real = Double
  val Real = Double // to have companion object of Double also be the companion object of Real
}