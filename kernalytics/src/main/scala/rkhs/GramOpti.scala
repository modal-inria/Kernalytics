package rkhs

import various.TypeDef._

/**
 * Algebraic type to record the various ways to optimize the Gram matrix.
 */
case class GramOpti()

class GramOptiDirect extends GramOpti
class GramOptiCache extends GramOpti
class GramOptiLowRank(val m: Index) extends GramOpti

object GramOpti {
  /** k(x, y) recomputed each time it is called. */
  class Direct() extends GramOpti
  /** k(x, y) computed once, then call from cache. */
  class Cache() extends GramOpti
  /** A reduced rank matrix is computed on a subset of indices to approximate the rank matrix. */
  class LowRank(val m: Index) extends GramOpti
}