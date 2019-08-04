# Why Scala and functional programming for Kernalytics ?

Here is an overview of the features of Scala that are used, and what should be expected of a programmer.

## Why not R ?

- scala is statically typed and compiled, which helps a lot when writing code. A lot of validation is performed before anything is run.
- it is faster
- language is much more coherent

## Why not C++ ?

### External opinions

- https://www.slant.co/versus/116/127/~scala_vs_c
- https://www.quora.com/What-are-the-advantages-of-Scala-over-C++-and-Haskell
- https://stackshare.io/stackups/cplusplus-vs-rust-vs-scala

### Specific arguments

- automatic object management
- use of immutable collections
- everything passed by reference
- being able to manipulate functions directly to structure the code
- avoid global states, uninitialized variable, uncontrolled states
- pattern matching in Scala is very useful
- learning functional construct will be useful to parallelize code or move it to Spark for example

## Functional vs imperative programming

Most of the code is written in a functional style, using immutable collections. It removes all errors that can be found in codes where data is not initialized, where global states are abused and the program is in invalid states. However, the pseudo code algorithms in the articles are usually written in an imperative style. Translating them to functional style will make the code difficult to read, and might reduce performances. Hence, the current recommendation is:

- To use functional programming for the overall architecture. See for example the [KernelGenerator](/src/main/scala/rkhs/KernelGenerator.scala) where functions are passed as arguments, or [Learn](/src/main/scala/exec/Learn.scala), where the Try monad helps manage the erros.eeee
- To use imperative programming, mutable collections and var for local computations. For example the Incomplete Cholesky Decomposition in [IncompleteCholesky.scala](/src/main/scala/linalg/IncompleteCholesky.scala). Here an imperative style is used and the code is much closer to the article pseudocode.

## Basics to learn to understand the Kernalytics code:

A beginner should grasp the basics of Scala from the introduction on the Scala website: https://docs.scala-lang.org/

The following methods for basic collections should be understood:

- map
- filter
- flatMap
- foreach
- reduce
- fold, foldLeft

As well as those constructs:

- patten matching using case classes, similar to the end of this tutorial: https://www.tutorialspoint.com/scala/scala_pattern_matching.htm
- monads (described below)
- for / yield (described below)

## The Try Monad

The use of this monad is the base of the error management code. It allows to write the code in a very sparse and concise manner, by providing mechanisms for the composition of error-prone computations.

There are formal introductions available online, such as [Demystifying the Monad in Scala](https://medium.com/@sinisalouc/demystifying-the-monad-in-scala-cc716bb6f534)

In this section the minimum requirerments to understand the Kernalytics code wil be described.

The `Try[A]` class (A being a parameter class) has two children classes, Success and Failure. Success[A] is a wrapper around an A object, while Failure is a wrapper around a throwable object. The main idea is that every function during initialization returns a `Try[_]` of some sort. When an error occurs, it will become the general result.

For a given object Try[A], the map needs a function A => B, while the flatMap method needs a method A => Try[B]. A function A => B is a method that can not raise an exception, while a function A => Try[B] can potentially fail and raise an exception.

Here is a simple example:

```scala
val a = 9.0
val b = Try(math.sqrt(a)) // if
val c = b.flatMap(computeInverse) // see definition of computeInverse below
val d = c.map(addTwo)

def computeInverse(x: Double): Try[Double] = Try(1.0 / x)
def addTwo(x: Double): Double = x + 2.0
```

- If a < 0, b will be a Failure, otherwise, it will be a `Success[Double](3.0)`.
- If b is a `Success[Double](0.0)`, then c is a failure. This is not the case here, and c is equal to Success(0.33333333)
- d is equal to Success(2.333333333). Since addTwo does not return a Try[Double], but simply a Double, it can not fail, this is why c.map has been called, and not c.flatMap

The advantage of this syntax is that composition is very simple. All the tests are implicit, but not written by the programmer. The Try Monad takes care of everything, the programmer just provides the functions to be applied.

The for / yield syntax is syntaxic sugar, when multi-level composition of flatMap and map becomes difficult to read. This articles explains it using the flatMap method from List. `List[_]` is a monad just the way `Try[_]` is. Consider a List[A], its flatMap method takes as argument a function A => List[B], and map takes a function A => B. Therefore the explanation is relevant to the current documentation.

http://debasishg.blogspot.com/2008/03/monads-another-way-to-abstract.html

## Additional resources

- https://www.manning.com/books/functional-programming-in-scala
- https://underscore.io/books/scala-with-cats/
- https://www.coursera.org/learn/progfun1
