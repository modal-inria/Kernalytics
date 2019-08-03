# Code overview

This document provides a bird's eye vision of Kernalytics, highlighting the most important aspects. In particular it links kernel mathematics to implementations, hoping to to highlight the modular nature of the project. Note that a more detailed description of the algorithm is provided in the [algorithm description](/doc/algoDesc.md).

## General description

- [KerEvalGenerator](/src/main/scala/rkhs/KerEvalGenerator.scala): the `generateKernelFromParamData` takes a combination of a datatype and a kernel name, and calls `KerEval.generateKerEvalFunc` with the proper two parameter function. This function is often obtained through a call to a [Kernel](/src/main/scala/rkhs/Kernel.scala) function.
- [Kernel](/src/main/scala/rkhs/Kernel.scala): kernels are grouped in families which are based on algebraic data structure. It makes use of [Algebra](/src/main/scala/rkhs/Algebra.scala) to leverage factorization provided by algebraic structures. This is where the proper, mathematical (x, y) => Real functions are written. Everywhere else are wrappers and interfaces.
- [Algebra](/src/main/scala/rkhs/Algebra.scala): Some kernels are defined on various types, and require only small changes. For example, the linear family of kernels only needs a scalar product to be defined. Therefor, a `InnerProductSpace` trait is defined, which requires the definition of the subtract and inner product operations. For the space of real numbers, this is done in the object `R`. The subtract of the pair (x, y) being x - y and their inner product x * y. An example of use of this inner product space is `Kernel.InnerProduct.linear`, which is called in `KerEval.generateKerEvalFunc`. The other algebraic structures are all connected in a similar fashion. This help decouple them from the data types and factorizes code.
- [KerEval](/src/main/scala/rkhs/KerEval.scala): basic abstraction for kernel evaluation. It is more than just a function, because it abstracts the underlying data type, and offer some computation optimization. For example, kernel evaluation can be computed on the fly or cached, through various specializations of the KerEval trait. All algorithms then can evaluate the kernel at data points, and still ignore the origin of the data.

## IO

Input / output is done in several steps.