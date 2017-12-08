# Short Term

- Selection of number of segments using penalized criterion.
- IO to be able to use data and parameters from disk.
- The whole algebra structures should be objects.
    - replace the higher order functions on operations with functions on algebra objects. For example create a function that transforms a normed space to a metric space.

# Long Term

- tests in p05offlinechangepoint.tests should be moved to ScalaTest, one way or another...
	- functional tests should be separated from unit tests and a metric should be used to compare the partitions. One seems to be provided in the article. In that case it should be implemented.
- in p00, p01, p02, p03, p04, use TypeDef Index and Real, instead of the underlying types
- Create new kernels from addition, product of kernels, to support heterogeneous multivariate data automatically

# Performances

- a Vector is used to enforce immutability when manipulating L. Switching to a mutable matrix might improve the performances. Encapsulating this in a monad would preserve functional purity.
- the loop over D for a given tauP can be parallelized.
    - Will the gain outweight the overhead for thread spawning ?
    - That would depend on the value of DMax.
- Gram matrix symmetry should be exploited to reduce computation times