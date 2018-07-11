# About

Kernel-powered analytics.

Kernel methods are versatile, Kernalytics provides a generic computational engine to take advantage of those properties.

# Documentation

- [Build instructions](./kernalytics/doc/build.md)
- [Algorithm description](./kernalytics/doc/algoDesc.md)
- [Things to do](TODO.md)

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
