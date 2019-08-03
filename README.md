# Kernel-powered analytics.

Kernel methods are versatile, and Kernalytics exploits this property as a generic data analysis framework. It allows for easy extension: new algorithms, data types, kernels and optimizations are immediately compatible with each other. This is possible through the Gram matrix computation used as a powerful abstraction linking every module. Heterogeneous data sets are supported through linear combination of kernels.

Current implementations cover:

- algorithms: k-means, offline change point detection, regression, support vector machine, two sample test
- data types: real, vector, matrix
- kernel families: linear, polynomial, gaussian, laplacian
- Gram matrix optimization: direct computation, cached computation, low rank approximation

For easier use, Kernalytics is provided with kernalyzr, a light R wrapper.

## Credits

The following people contributed to the development of Kernalytics: Vincent Kubicki, Alain Célisse.

Copyrigth Inria - Université de Lille - CNRS

## Quick start for kernalyzr

Install sbt on your machine (see [Build instructions](doc/build.md)). Open the kernalyzr directory in RStudio, then press `CRTL + SHIFT + B`.

To create and run some working examples in the (preferably empty) current directory, type the following commands in the R console:

```R
library(kernalyzr) # load kernalyz
copyTest() # generate the test cases
runTest() # run kernalyz algorithms on each test case
```

For more details on the data format, see the [documentation on input / output format](doc/io.md).

## Internal links

- [License](LICENCE.md)
- [Build instructions](doc/build.md)
- [Numerical methods overview](doc/overview.md)
- [Complete run description](doc/algoDesc.md)
- [I/O file formats](doc/io.md)
- [General code edition](doc/ide.md)
- [Extension of the code](doc/extend.md)
- [Coding concepts used](doc/scala.md)
- [Things to do](TODO.md)

## External links

- [kernlab](https://cran.r-project.org/web/packages/kernlab/vignettes/kernlab.pdf): a package with similar goals, written in R
- [RScala](https://github.com/dbdahl/rscala): the gateway between Scala and R, used in kernalyr
  - [RScala vignette](https://dahl.byu.edu/public/rscala/rscala.pdf)
  - [Bamboo](https://github.com/dbdahl/bamboo), an example package using Rscala
  - [Shallot](https://github.com/dbdahl/shallot), another example package
