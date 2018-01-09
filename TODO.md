# Short Term

# Long Term

- R interface via rscala
- IO to be able to use data and parameters from disk.
- in p00, p01, p02, p03, p04, use TypeDef Index and Real, instead of the underlying types

# Performances

- a Vector is used to enforce immutability when manipulating L.
    - Switching to a mutable matrix might improve the performances. Encapsulating this in a monad would preserve functional purity.
    - a better solution would be to use an Array[List[SegCost]], where each List contains all the values of mu for a given D. In terms of accesses, it is the transpose of the current implementation.
        - each new results is added using ::, and the computation of the best new segmentation can be obtained for excample by zipping the cost matrix column with the list
    - in any cases, keep the old implementation
- the loop over D for a given tauP can be parallelized.
    - Will the gain outweight the overhead for thread spawning ?
    - That would depend on the value of DMax.
- Gram matrix symmetry should be exploited to reduce computational times.
    
# Architecture

- Real and Index types are defined to avoid setting the types directly. However, .toInt and .toDouble methods are used for conversion. Any mention to Int and Double should be removed.
- Use TypeTag instead of encapsulating types in KerEval, for example
- Better management of Option, when .get is used