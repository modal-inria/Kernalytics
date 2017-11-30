# Short Term

- Distances of matrices.
- Selection of number of segments using penalized criterion.
- IO to be able to use data and parameters from disk.

# Long Term

- in p00, p01, p02, p03, p04, use TypeDef Index and Real, instead of the underlying types
- for small number of observations, compute the Gram matrix and include it in the closure of a "cached" kerEval
- Create new kernels from addition, product of kernels, to support heterogeneous multivariate data automatically
- Automate the choice of kernel using an error function to be minimized
    - the error function should be provided as a parameter
    - the kernels could be provided as a list of kernels

# Performances

- a Vector is used to enforce immutability when manipulating L. Switching to a mutable matrix might improve the performances. Encapsulating this in a monad would preserve functional purity.
- the loop over D for a given tauP can be parallelized.
    - Will the gain outweight the overhead for thread spawning ?
    - That would depend on the value of DMax.
- Gram matrix symmetry should be exploited to reduce computation times