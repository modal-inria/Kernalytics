# About

Kernel-powered analytics.

Kernel methods are versatile, and creating a generic computational engine that exploit them seems natural.

# How to add a Kernel

- Implement the (X, X) => R function anywhere, as long as it is reachable.
- Add a corresponding case in rkhs.IO.generateKernel.