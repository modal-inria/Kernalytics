# Offline change point detection

## Short Term

## Long Term

- in p00, p01, p02, p03, p04, use TypeDef Index and Real, instead of the underlying types
- for small number of observations, compute the Gram matrix and include it in the closure of a "cached" kerEval
- Create new kernels from addition, product of kernels, to support heterogeneous multivariate data automatically
- Automate the choice of kernel using an error function to be minimized
    - the error function should be provided as a parameter
    - the kernels could be provided as a list of kernels
- BFGS might not be the best algorithm to exploit the quadratic nature of the Representer Theorem optimization problem.
    - A more specialized algorithm might help enhance performances.
    - There is a quadratic optimizer in Scala Breeze
- Get a better understanding of how Breeze optimization package works
	- Termination conditions

## Performances

- a Vector is used to enforce immutability when manipulating L. Switching to a mutable matrix might improve the performances. Encapsulating this in a monad would preserve functional purity.
- the loop over D for a given tauP can be parallelized.
    - Will the gain outweight the overhead for thread spawning ?
    - That would depend on the value of DMax.
- Gram matrix symmetry should be exploited to reduce computation times

# Kernel K-Means

## Performances

- take should not be used, as this might imply saving the results of the first iterations. Should drop and next be used instead ?
