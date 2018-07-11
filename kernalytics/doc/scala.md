Here is an overview of the features of Scala that are used, and what should be expected of a programmer.

# Why not use C++ ?

# Functional programming

Most of the code is written in a functional style, using immutable collections. However, the pseudo code algorithms in the articles are usually written in an imperative style. Translating them to functional style will make the code difficult to read, and might reduce performances. Hence, the original author recommendations on those matters is to use functional programming for overall architecture, and use imperative programming, mutable collections and var for local computations, inside a specialized method.

An example of misuse of functional programming would be [Segmentation.scala](kernalytics/src/main/scala/algo/offlinechangepoint/Segmentation.scala). The loops present in the pseudo code in the article are absent here, replaced by various folds. While it benefits from purity, this code is less readable for someone who expects the same syntax than in the article.

Compare this with the implementation of the Incomplete Cholesky Decomposition in [IncompleteCholesky.scala](kernalytics/src/main/scala/linalg/IncompleteCholesky.scala). Here an imperative style is used and the code is much closer to the article pseudocode.

Functional programming allows code to be more predictable, but its formulation differs from imperative programming and performances can be affected. For example, there is no way to implement Quick Sort in a functional language and get the same performances than with imperative programming and mutable states. Hence the general advice would be to use functional programming to structure the code globally, and imperative programming for local computations.

The main advantage of using functional programming and immutable data structure, is that the code is predictable. It removes all errors that can be found in codes where data is not initialized, where global states are abused and the program is in invalid states.

# Expected

You should understand the following methods for basic collections:
- map
- filter
- flatMap
- foreach
- reduce
- fold, foldLeft

# The Try Monad

The use of this monad is the base of the error management code. It allows to write the code in a very sparse and concise manner. Understanding the concepts might not be immediate for a new programmer.

# for / yield construct

Composition of multiple maps and flatMap are not easy to Understanding => donner example de code, version map / flatMap vs for yield.

The pattern is always the same, a chain a flatMap and a final Map.
