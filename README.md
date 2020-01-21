# Kernalytics: kernel methods for data analysis

Kernel methods are versatile, and Kernalytics exploits this property through a generic data analysis framework. It allows for easy extension: new algorithms, data types, kernels and optimizations are easily compatible with each other. This is possible through the Gram matrix computation which acts as an abstraction linking every module. Heterogeneous data sets are supported through linear combination of kernels.

Current implementations cover:

- algorithms: k-means, offline change point detection, regression, support vector machine, two sample test
- data types: real, vector, matrix
- kernel families: linear, polynomial, gaussian, laplacian
- Gram matrix optimization: direct computation, cached computation, low rank approximation

For easier use, Kernalytics is provided with kernalyzr, a (very shallow) R wrapper.

Kernalytics has been built and run on Windows, macOS and Linux.

## Licence

Kernalytics is distributed under the AGPL 3.0 licence.

## Credits

The following people contributed to the development of Kernalytics: Vincent Kubicki, Alain Celisse.

Copyright Inria - Université de Lille - CNRS

## Quick start for kernalyzr

Install sbt on your machine (see [Build instructions](doc/build.md)). Open the kernalyzr directory in RStudio, then press `CRTL + SHIFT + B`.

To create and run some working examples in the (preferably empty) current directory, type the following commands in the R console:

```R
library(kernalyzr) # load kernalyzr
copyTest() # generate the test cases
runTest() # run kernalyz algorithms on each test case
```

For more details on the data format, see the [documentation on input / output format](doc/io.md).

## Technology Readiness Level

Kernalytics is at the moment a proof of concept which has been used on a few real data sets. A lot of things are missing, as indicated in the list of [things to do](TODO.md). However, the modular architecture can be considered stable and functional.

The [Technology Readiness Level](https://en.wikipedia.org/wiki/Technology_readiness_level) could be considered 4 or 5: feasibility has been proven, and demonstration must be carried out.

## Internal links

- [Licence](LICENCE.md)
- [Build instructions](doc/build.md)
- [Numerical methods overview](doc/overview.md)
- [Complete run description](doc/algoDesc.md)
- [I/O file formats](doc/io.md)
- [General code edition](doc/ide.md)
- [Extension of the code](doc/extend.md)
- [Coding concepts used](doc/scala.md)
- [Things to do](TODO.md)

## External links

- [Sernel](https://github.com/vkubicki/Sernel): initial project on which Kernalytics is based
- [kernlab](https://cran.r-project.org/web/packages/kernlab/vignettes/kernlab.pdf): a package with similar goals, written in R
- [RScala](https://github.com/dbdahl/rscala): the gateway between Scala and R, used in kernalyr
  - [RScala vignette](https://dahl.byu.edu/public/rscala/rscala.pdf)
  - [Bamboo](https://github.com/dbdahl/bamboo), an example package using Rscala
  - [Shallot](https://github.com/dbdahl/shallot), another example package
