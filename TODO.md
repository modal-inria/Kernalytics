# Roadmap

- Iterative computation of the cost matrix, as a function that associates a column to a column
- Iterative algorithm 3
- Base tests with segments having two different distributions, for example exponential and gaussian with same expected value.

# Long Term

- in p00, p01, p02, p03, p04, use TypeDef Index and Real, instead of the underlying types

# Legacy Sernel

## Short term

- In the kernel kmeans, take should not be used, as this might imply saving the results of the first iterations. Should drop and next be used instead ?

## Medium term

- Gram matrix symmetry should be exploited to reduce computation times
- BFGS might not be the best algorithm to exploit the quadratic nature of the Representer Theorem optimisation problem.
    - A more specialized algorithm might help enhance performances.
    - There is a quadratic optimizer in Scala Breeze
- Get a better understanding of how Breeze optimization package works
	- Termination conditions

## Long term

- Use the existing framework to implement other kernel methods:
    - k-means
    - SVM
    - kernel embeddings: https://en.wikipedia.org/wiki/Kernel_embedding_of_distributions
        - kernel two-sample test: http://jmlr.org/papers/volume13/gretton12a/gretton12a.pdf
- Create new kernels from addition, product of kernels, to support heterogeneous multivariate data automatically
- Automate the choice of kernel using an error function to be minimized
    - the error function should be provided as a parameter
    - the kernels could be provided as a list of kernels