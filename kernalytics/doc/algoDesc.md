This file contains a description of the algorithm, in particular the initialization.

An algorithm is separated in three parts.
- Reading the parameters and data
- Generating the kernel
- Running the algorithm
- Exporting the results

You can find examples for various algorithms in [Examples.scala](./kernalytics/src/main/scala/exec/Examples.scala). They all follow the same structure, a root folder is defined, and either Learn.main or Predict.main is called. Kernalytics only works with structured data in the form of a collection of csv files at the moment. The root folder is the place where they should be found. What will follow then is a description of Learn.main, and the differences with Predict.main will be highlighted.

- exec.Learn.main
    - io.ReadAlgo.readAndParseFile
    - io.ReadVar.readAndParseVars
    - exec.Learn.cacheGram
    - io.ReadParam.readAndParseParam
    - io.CombineVarParam.generateGlobalKerEval
    - exec.Learn.callAlgo
        - exec.learn.KMeans.main: read and write parameters and data that are specific for the algorithm. For kmeans, it is the number of classes asked and the number of iterations the algorithm must be run. Then call the main algorithm.
            - getNClass
            - getNIteration
            - algo.kmeans.IO.runKMeans: main algorithm, implemented separately from the IO, for better unit testing.
            - writeResults
