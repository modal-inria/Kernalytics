# About

Kernel-powered analytics.

Kernel methods are versatile, and creating a computational engine that exploit seems natural.

# To Do

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

# Bugs
