# Code overview

This document provides a bird's eye vision of Kernalytics, highlighting the most important aspects. In particular it links kernel mathematics to implementations, hoping to to highlight the modular nature of the project.

## General description

- [io](/src/main/scala/io): 
- [KerEvalGenerator](/src/main/scala/rkhs/KerEvalGenerator.scala): the `generateKernelFromParamData` takes a combination of a datatype and a kernel name, and calls `KerEval.generateKerEvalFunc` with the proper two parameter function. This function is often obtained through a call to a [Kernel](/src/main/scala/rkhs/Kernel.scala) function.
- [Kernel](/src/main/scala/rkhs/Kernel.scala): kernels are grouped in families which are based on algebraic data structure.
- [KerEval](/src/main/scala/rkhs/KerEval.scala): basic abstraction for kernel evaluation. It is more than just a function, because it abstracts the underlying data type, and offer some computation optimization. For example, kernel evaluation can be computed on the fly or cached, through various specializations of the KerEval trait. All algorithms then can evaluate the kernel at data points, and still ignore the origin of the data.

## IO

Input / output is done in several steps.