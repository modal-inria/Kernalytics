# How to extend Kernalytics

This chapter contains links to the various locations that must be modified to add a new data type or a new kernel.

## How to add a new data type

Adding a new data type is a bit involved, as it requires added a data container, but also a parser and a few other operations.

### Pattern-matching of the kernel name

Add its name as a case in the pattern matching of [parseIndividualVar](/src/main/scala/io/ReadVar.scala). Then call the proper parser to transform the raw data in `Array[String]` to a `ParsedVar` which is a named [DataRoot](/src/main/scala/rkhs/DataRoot.scala).

### Parser

Add the implementation of the parsing in a specific function in io.ReadVar, or in an external file, like [ParseVectorReal](/src/main/scala/io/ParseVectorReal.scala).

The parser must transform an `Array[String]` to a [DataRoot](/src/main/scala/rkhs/DataRoot.scala).

### DataRoot subtyping

Since the data type is new, it might be necessary to store it in a new type of container. The current containers used are Breeze container (such as `DenseMatrix[Real]`s), but anything could be used. Each variable is contained in an instance of a subclass of [DataRoot](/src/main/scala/rkhs/DataRoot.scala). The modification is to subtype [DataRoot](/src/main/scala/rkhs/DataRoot.scala). 

### Algebraic system

If the data type has inner product / norm / distance, implement them in [Algebra](/src/main/scala/rkhs/Algebra.scala), as this will allow families of kernels to be quickly generated for this kernel.

## How to add a new kernel on an existing data type

A kernel simply is a two arguments function from a couple of the X data type to Real. Its integration in Kernalytics in not very hard.

There are two ways to implement this function:
1. Directly as a (X, X) => Real function directly in [Kernel](/src/main/scala/rkhs/Kernel.scala). See the `dummyLinearKernel` for example.
2. Indirectly as an algebraic object, to be used as an argument for another function in [Kernel](/src/main/scala/rkhs/Kernel.scala) , like `InnerProduct.linear` or `Metric.gaussian`. The algebraic system is discussed in more details in the [overview](overview.md).

You must then add the corresponding kernel and its data type as a case in [generateKernelFromParamData](/src/main/scala/rkhs/KerEvalGenerator.scala)

## Notes

### Architecture

The current way to handle mixing data types and kernels is to use local pattern-matching in [KerEvalGenerator](/src/main/scala/rkhs/KerEvalGenerator.scala).

The current implementation in [KerEvalGenerator](/src/main/scala/rkhs/KerEvalGenerator.scala) is not satisfying, as it relies on pattern-matching against a set of predefined combination of data types and kernel names. This is not optimal, as adding a new type or kernel implies modifying code scattered all over Kernalytics. Ideally, everything should be centralized so that all the logic could be contained in a single object for each type.

Note that there is a `typeName: String` here, which is similar to the string in [parseIndividualVar](/src/main/scala/io/ReadVar.scala). This could be leveraged when reworking the data types management.

### Unit testing

Kernalytics implements unit testing via the ScalaTest library. Developers are strongly advise to add / run unit tests as often as possible. They are located in the [test](/src/test/scala) directory.
