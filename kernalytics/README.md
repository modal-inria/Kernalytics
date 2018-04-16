# About

Kernel-powered analytics.

Kernel methods are versatile, and creating a generic computational engine that exploit them seems natural.

# How to add new data types and kernel

The current way to handle data types and kernels is to use local pattern-matching. This is not optimal, as adding a new type or kernel implies modifying code scattered all over the package. Ideally, everything should be centralized so that all the logic could be contained in a single object for each type. This chapter contains links to the various locations that must be modified to add a new data type in the current implementation.

## How to add a new data type

- Add its String name in the pattern matching in ReadVar.parseIndividualVar
- Add the implementation of the parsing in a specific function in ReadVar
- Add a subtype to DataRoot in KerEval
	- Note that there is a String here, which is similar to the string in ReadVar.parseIndividualVar. This could be levered when refactoring the data types.
- If the data type has inner product / norm / distance, implement them in Algebra.
	- This allows families of kernels to be immediately accessible for this kernel.

## How to add a Kernel

- Implement the (X, X) => R function directly
	- This could take an inner product / norm / distance as a parameter for example
- Add a corresponding case in rkhs.IO.generateKernel.

## Hints for centralized implementation

- It is not necessary to have the data be described as a DenseVector.
	- and it should not be necessary to depend on an external library at such a high level in data, because it is just a storage, without any computation
- The only places where the data is described as a DenseVector are:
	- ReadVar.parseIndividualVar for "packing"
	- KerEvel.generateKerEval for "unpacking" to a kerEval
- Prototype of central object:
	- Constructed from an Array of String obtained from basic parsing of the data
	- Should provide an inner product / norm / distance (as an Option, because those are not definite on every data type)
	- Can store the data the way it wants, but must provide a function Index => Data so that the kerEval can be evaluated
