package p05offlinechangepoint

import breeze.linalg._
import breeze.plot._
import java.io.File
import p04various.TypeDef._
import p04various.{Def, Math}

object NumberSegmentSelection {
  /**
   * Take the risk for every value of D, compute the penalized risk using a slope heuristic, then return an oracle
   * estimation of the optimal number of segments.
   */
  def optimalNumberSegments(
      cost: Array[Real],
      n: Index,
      visualOutput: Boolean,
      baseDir: String)
  : Index = {
    val DMax = cost.size - 1
    val DMin: Index = (0.6 * DMax.toDouble).toInt // TODO: should use Real and Index types
    
    val funcs =
      Array[Index => Real](
        D => D.toDouble / n.toDouble,
        D => Math.logBinomial(n - 1, D - 1),
        D => 1.0)
        
    val x =
      DenseMatrix.tabulate[Real](DMax - DMin + 1, 3)((i, j) => {
    	    val D = i + DMin
        funcs(j)(D)
      })
    
    val y =
      DenseVector.tabulate[Real](DMax - DMin + 1)(i => {
        val D = i + DMin;
        cost(D)
      })
    
    val beta = Math.linearRegression(x, y)
    val C = DenseVector.tabulate[Real](3)(i => if (i < 2) -2.0 * beta(i) else 0.0)

    val penalizedCost =
      DenseVector.tabulate[Real](cost.size)(D => {
        val penalty = DenseVector(funcs.map(_(D))).dot(C)
        cost(D) + penalty
      })
      
    if (visualOutput) {
      val regressedCost =
        DenseVector.tabulate[Real](cost.size)(D => {
          DenseVector(funcs.map(_(D))).dot(beta)
        })      
      
      println(s"cost: ${cost.mkString(", ")}")
      println(s"regressedCost: $regressedCost")
      println(s"penalizedCost: $penalizedCost")
      
      val f = Figure()
    	  val p = f.subplot(0)
    	  
    		p += plot(
    		    (1 to DMax).map(_.toDouble),
    		    penalizedCost(1 to DMax),
    		    name = "Penalized Cost") // TODO: why NaN and -Infinity for D = 0 and D = 1 ?
    		    
    		p += plot(
    		    (1 to DMax).map(_.toDouble),
    		    cost.slice(1, DMax + 1),
    		    name = "Cost")
    		    
    		p += plot(
    		    (1 to DMax).map(_.toDouble),
    		    regressedCost(1 to DMax),
    		    name = "Regressed Cost")
    		    
    		p.xlabel = "D"
    		p.ylabel = "Cost"
    		f.saveas(baseDir + "/lines.png")
    		
//    		csvwrite(new File(baseDir + Def.sep +"penalizedCost.csv"), penalizedCost.asDenseMatrix.t)
    }

    return argmin(penalizedCost)
  }
}