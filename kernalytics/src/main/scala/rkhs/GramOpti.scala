package rkhs

import various.TypeDef._

/**
 * Algebraic type to record the various ways to optimize the Gram matrix.
 */
sealed trait GramOpti

object GramOpti {
  /** k(x, y) recomputed each time it is called. */
  case class Direct() extends GramOpti
  /** k(x, y) computed once, then call from cache. */
  case class Cache() extends GramOpti
  /** A reduced rank matrix is computed on a subset of indices to approximate the rank matrix. */
  case class LowRank(val m: Index) extends GramOpti
}