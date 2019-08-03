# Algorithm description

This file contains a description of the algorithms, in particular the initialization which can look convoluted as a lot of steps and verifications are needed before an actual computation can be run.

Running an algorithm implies a few steps.
- *Reading the parameters and data.**
    - a part of the code is shared among several algorithms
    - each algo can also run its specific code for its peculiar data.
- *Generating the kernel.*
    - Generate the KerEval object, that is essentially the kernel evaluator object.
    - Transform the data to an nObs x nObs Gram matrix that contains everything needed to run the algorithm.
- *Running the algorithm*
    - The actual implementation of the algorithm.
    - This is the less constrained part of the code.
    - As long as it takes a KerEval object as argument, it can be coded anyway the coder wants.
- *Exporting the results*
    - write everything in the form of csv files in the root folder of the analysis case.

You can find examples for various algorithms in [Examples.scala](/src/main/scala/exec/Examples.scala). They all follow the same structure, a root folder is defined, and either Learn.main or Predict.main is called. Kernalytics only works with structured data in the form of a collection of csv files. Even the rscala packages is just a thin wrapper to transmit the root folder location. The root folder is the place where they should be found. What will follow then is a description of Learn.main, and the differences with Predict.main will then be highlighted.

# exec.Learn.main

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

# exec.Predict.main

Essentially similar to `exec.Learn.main`. The main differences are:

- the gramOpti parameter in algo.csv is ignored. Direct() is always used instead.
    - in prediction, the gram matrix has dimension (nObsLearn + nObsPredict) x (nObsLearn + nObsPredict), and usually each coefficient is used only once
- io.ReadVar.readAndParseVars2Files is called instead of io.ReadVar.readAndParseVars, in order to generate a Gram matrix that combines learn and prediction data.
