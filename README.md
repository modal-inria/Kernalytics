# About

Kernel-powered analytics.

Kernel methods are versatile, and creating a generic computational engine that exploit them seems natural.

# How to add a Kernel

- Implement the (X, X) => R function
- Extend ParameterRoot with its parameter description
- In ReadParam.parseIndividualParam add the function to parse the descriptor file
- In KerEval.paramToKerEval, generate the correct kernel
