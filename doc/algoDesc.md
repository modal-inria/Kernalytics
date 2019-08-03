# Algorithm description

This file contains a description of the learn and predict algorithms, in particular the initialization which can look convoluted. A lot of steps and verifications are needed before an actual computation can be run.

## General overview

A run of Kernalytics is composed of the following steps.

### Reading the data

This is not method specific. Every numerical method read the data the same way, the objective is to generate the Gram matrix. There are only two files to describe this, [Learn](/src/main/scala/exec/Learn.scala) and [Predict](/src/main/scala/exec/Predict.scala). Upon parsing the method name, a specific method is called that will read the parameters and run the numerical method on the data;

### Reading the parameters and running the method

This is similar but different across numerical methods. What differs is the parameters description. Each method (regression, segmentation,...) has its own set of parameters. This is described in the [learn](/src/main/scala/exec/learn) and [predict](/src/main/scala/exec/predict) directories. Once the parameters have been parsed, the numerical method main function is called with the data and parameters. The result of the analysis is returned.

The method run is described in the [overview](overview.md).

The last step is to write all the results as csv files in the root folder of the analysis case.
   
## Examples

You can find examples for various algorithms in [Examples.scala](/src/main/scala/exec/Examples.scala). They all follow the same structure, a root folder is defined (located in [exec](/data/exec)), and either [Learn](/src/main/scala/exec/Learn.scala) or [Predict](/src/main/scala/exec/Predict.scala) is called.
 
Kernalytics only works with structured data in the form of a collection of csv files. Even the rscala packages is just a thin wrapper to transmit the root folder location. The root folder is the place where they should be found. 

## Detailed call sequence

A description of [Learn](/src/main/scala/exec/Learn.scala), and the differences with [Predict](/src/main/scala/exec/Predict.scala) will then be highlighted.

### exec.Learn.main

- [readAndParseFile](/src/main/scala/io/ReadAlgo.scala): read the `algo.csv` file, check the content and generate a map `key => value` with an entry for each column.
- [readAndParseVars](/src/main/scala/io/ReadVar.scala): read the `learnData.csv` or `learnPredict.csv` file, and generate a tuple `(Array[ParsedVar], Index)`. First element contains the parsed variable, the second the number of observations.
- [cacheGram](/src/main/scala/exec/Learn.scala): parse the `gramOpti` option in `algo.csv`.
- [readAndParseParam](/src/main/scala/io/ReadParam.scala): read the `desc.csv` file, parse each column and generate an `Array[ParsedParam]`.
- [generateGlobalKerEval](/src/main/scala/io/CombineVarParam.scala): from the `Array[ParsedVar]` and `Array[ParsedParam]` previously generated, generate the `KerEval` object
    - [linkParamToData](/src/main/scala/io/CombineVarParam.scala): link each kernel to a variable, in a [KerEvalFuncDescription](/src/main/scala/rkhs/KerEval.scala), which merges kernel and data information.
    - [multivariateKerEval](/src/main/scala/rkhs/KerEval.scala): from the list of [KerEvalFuncDescription](/src/main/scala/rkhs/KerEval.scala), generate the kernel function `(Index, Index) => Real`
        - [generateKernelFromParamData](/src/main/scala/rkhs/KernelGenerator.scala): for each [KerEvalFuncDescription](/src/main/scala/rkhs/KerEval.scala), generate a function `(Index, Index) => Real`
        - [linearCombKerEvalFunc](/src/main/scala/rkhs/KerEval.scala), aggregate the individual `(Index, Index) => Real` in a global `(Index, Index) => Real`
    - then use the `gramOpti option` to generate a `KerEval` object which uses the cache method provided by the user in `algo.csv`
- [callAlgo](/src/main/scala/exec/Learn.scala): parse the `algo` entry in `algo.csv` to launch the corresponding numerical method, for example KMeans in the next line
    - [main](/src/main/scala/exec/learn/KMeans.scala): read and write parameters and data that are specific for the algorithm. For KMeans, it is the number of classes asked and the number of iterations the algorithm must be run. Then call the main function in the numerical method.
        - `getNClass`: number of classes asked for
        - `getNIteration`: number of iterations
        - [runKMeans](/src/main/scala/algo/kmeans/IO.scala): code of the main algorithm. It assumes that all the data provided have been read and validated.
        - `writeResults`: write the results on the disk.

### exec.Predict.main

Essentially similar to [exec.Learn.main](/src/main/scala/exec/Learn.scala). The main differences are:

- the `gramOpti` parameter in `algo.csv` is ignored. `Direct()` is always used instead.
    - in prediction, the gram matrix has dimension `(nObsLearn + nObsPredict) x (nObsLearn + nObsPredict)`, and usually each coefficient is used only once
- [readAndParseVars2Files](/src/main/scala/io/ReadVar.scala) is called instead of [readAndParseVars](/src/main/scala/io/ReadVar.scala), in order to generate a Gram matrix that combines learn and prediction data.
