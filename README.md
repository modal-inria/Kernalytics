# Kernel-powered analytics.

Kernel methods are versatile, and Kernalytics exploits this property as a generic data analysis framework. It allows for easy extension: new algorithms, data types, kernels and optimizations are immediately compatible with each other. This is possible through the Gram matrix computation used as a powerful abstraction linking every module. Heterogeneous data sets are supported through linear combination of kernels.

Current implementations cover:

- algorithms: k-means, offline change point detection, regression, support vector machine, two sample test
- data types: real, vector, matrix
- kernel families: linear, polynomial, gaussian, laplacian
- Gram matrix optimization: direct computation, cached computation, low rank approximation

For easier use, Kernalytics is provided with kernalyzr, a light R wrapper.

## Quick start for kernalyzr

Install sbt on your machine (see [Build instructions](doc/build.md)). Open the kernalyzr directory in RStudio, then press `CRTL + SHIFT + B`.

To create and run some working examples in the (preferably empty) current directory, type the following commands in the R console:

```R
library(kernalyzr) # load kernalyz
copyTest() # generate the test cases
runTest() # run kernalyz algorithms on each test case
```

## Internal links

- [License](LICENSE)
- [Build instructions](doc/build.md)
- [Use of Scala](doc/scala.md)
- [Algorithm description](doc/algoDesc.md)
- [Things to do](TODO.md)
- [External links](doc/links.md)

## External links

- [RScala](https://github.com/dbdahl/rscala)
  - [RScala vignette](https://dahl.byu.edu/public/rscala/rscala.pdf)
  - https://github.com/dbdahl/bamboo
  - https://github.com/dbdahl/shallot
