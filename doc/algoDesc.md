# Algorithm description

This file contains a description of the learn and predict algorithms, in particular the initialization which can look convoluted. A lot of steps and verifications are needed before an actual computation can be run.

## General overview

A run of Kernalytics is composed of the following steps.

### Reading the data

This is not method specific. Every numerical method read the data the same way, the objective is to generate the Gram matrix. There are only two files to describe this, [Learn](/src/main/scala/exec/Learn.scala) and [Predict](/src/main/scala/exec/Predict.scala). Upon parsing the method name, a specific method is called that will read the parameters and run the numerical method on the data;

### Reading the parameters and running the method

This is similar but different across numerical methods. What differs is the parameters description. Each method (regression, segmentation,...) has its own set of parameters. This is described in the [learn](/src/main/scala/exec/learn) and [predict](/src/main/scala/exec/predict) directories. Once the parameters have been parsed, the numerical method main function is called with the data and parameters. The result of the analysis is returned.

### Running the numerical method

See [overview](overview.md).
    
### Exporting the results

Write everything in the form of csv files in the root folder of the analysis case. This is done in the [learn](/src/main/scala/exec/learn) and [predict](/src/main/scala/exec/predict) directories.

## Examples

You can find examples for various algorithms in [Examples.scala](/src/main/scala/exec/Examples.scala). They all follow the same structure, a root folder is defined (located in [exec](/data/exec)), and either [Learn](/src/main/scala/exec/Learn.scala) or [Predict](/src/main/scala/exec/Predict.scala) is called.
 
Kernalytics only works with structured data in the form of a collection of csv files. Even the rscala packages is just a thin wrapper to transmit the root folder location. The root folder is the place where they should be found. 

## Detailed call sequence

A description of [Learn](/src/main/scala/exec/Learn.scala), and the differences with [Predict](/src/main/scala/exec/Predict.scala) will then be highlighted.

### exec.Learn.main

- io.ReadAlgo.readAndParseFile: read the algo.csv file, check the content and generate a map key => value for each row.
- io.ReadVar.readAndParseVars: read the learnData.csv or learnPredict.csv file, and generate a tuple (Array[ParsedVar], Index). First element contains the parsed variable, the second the number of observations.
- exec.Learn.cacheGram: parse the gramOpti option in algo.csv
- io.ReadParam.readAndParseParam: read the desc.csv file, parse each column and generate an Array[ParsedParam]
- io.CombineVarParam.generateGlobalKerEval: from the Array[ParsedVar] and Array[ParsedParam] previously generated, generate the KerEval object
    - io.CombineVarParam.linkParamToData: link each kernel to a variable, in a rkhs.KerEval.KerEvalFuncDescription, which merges kernel and data information
    - rkhs.KerEval.multivariateKerEval: from the list of rkhs.KerEval.KerEvalFuncDescription, generate the kernel function (Index, Index) => Real
        - rkhs.KerEvalGenerator.generateKernelFromParamData: for each rkhs.KerEval.KerEvalFuncDescription, generate a function (Index, Index) => Real
        - rkhs.KerEval.linearCombKerEvalFunc, aggregate the individual (Index, Index) => Real in a global (Index, Index) => Real
    - then use the gramOpti option to generate a KerEval object which uses the cache method selected by the user in algo.csv
- exec.Learn.callAlgo: parse the algo entry in algo.csv to launch the corresponding algo, for the example it is KMeans here
    - exec.learn.KMeans.main: read and write parameters and data that are specific for the algorithm. For KMeans, it is the number of classes asked and the number of iterations the algorithm must be run. Then call the main algorithm.
        - getNClass: number of classes asked for
        - getNIteration: number of iterations
        - algo.kmeans.IO.runKMeans: code of the main algorithm. It assumes that all the data provided have been read and validated.
        - writeResults: write the results on the disk.

### exec.Predict.main

Essentially similar to `exec.Learn.main`. The main differences are:

- the gramOpti parameter in algo.csv is ignored. Direct() is always used instead.
    - in prediction, the gram matrix has dimension (nObsLearn + nObsPredict) x (nObsLearn + nObsPredict), and usually each coefficient is used only once
- io.ReadVar.readAndParseVars2Files is called instead of io.ReadVar.readAndParseVars, in order to generate a Gram matrix that combines learn and prediction data.
