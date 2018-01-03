# Short Term

- uncomment tests in p05offlinechangepoint and move them to unit test, non deterministic
- factor out the data generation in Test object, to be reused among all tests, including the two above
- Use frobeniusMatrix for deterministic unit test
- IO to be able to use data and parameters from disk.

# Long Term

- tests in p05offlinechangepoint.tests should be moved to ScalaTest, one way or another...
    - functional tests should be separated from unit tests and a metric should be used to compare the partitions. One seems to be provided in the article. In that case it should be implemented.
- in p00, p01, p02, p03, p04, use TypeDef Index and Real, instead of the underlying types
- Create new kernels from addition, product of kernels, to support heterogeneous multivariate data automatically

# Performances

- a Vector is used to enforce immutability when manipulating L.
    - Switching to a mutable matrix might improve the performances. Encapsulating this in a monad would preserve functional purity.
    - a better solution would be to use an Array[List[SegCost]], where each List contains all the values of mu for a given D. In terms of accesses, it is the transpose of the current implementation.
        - each new results is added using ::, and the computation of the best new segmentation can be obtained for excample by zipping the cost matrix column with the list
- the loop over D for a given tauP can be parallelized.
    - Will the gain outweight the overhead for thread spawning ?
    - That would depend on the value of DMax.
- Gram matrix symmetry should be exploited to reduce computational times.
    
# Architecture

- Real and Index types are defined to avoid setting the types directly. However, .toInt and .toDouble methods are used for conversion. Any mention to Int and Double should be removed.