# Input / output formats

This document presents the data format used in Kernalytics. Examples can be found in the [exec](/data/exec) directory.

A Kernalytics analysis case is composed of several files:
- `algo.csv`: describes the numerical method used, and its parameters
- `desc.csv`: describes the kernel used on the data
- `learnData.csv`: the data used to compute the Gram matrix during learning
- `predictData.csv`: the data used to compute the Gram matrix during prediction (if need be)

Note that some numerical methods use additional files. For example the regression algorithm uses `learnY.csv` to provide the dependant variable.

## `algo.csv`

The entries used for all numerical methods are:

### `algo`

Provides the numerical method name. The complete list for learning phase numerical methods can be found at [callAlgo for Learn](/src/main/scala/exec/Learn.scala). The numerical methods that can do prediction are to be found in [callAlgo for Predict](/src/main/scala/exec/Predict.scala).

Numerical methods for learn:

- `offlinechangepoint`
- `twosampletest`
- `kmeans`
- `regression`
- `svm`
    
Numerical methods for predict:

- `regression`
- `svm`

### `cacheGram`

The option that determines how the gram matrix is computed. Options for the value are parsed in the function [cacheGram](/src/main/scala/exec/Learn.scala). They are:

- `Direct()`: any Gram matrix entry is computed on-the-fly
- `Cache()`: all Gram matrix entries are computed at once and stored in a `DenseMatrix[Real]` for fast retrieval
- `LowRank(m)`: compute a rank m approximation of the Gram matrix using an incomplete Cholesky decomposition in [icd](/src/main/scala/linalg/IncompleteCholesky.scala). The low rank description is stored, and each value of the full-rank matrix is reconstructed at each call. Kernel evaluation are only performed during the incomplete Cholesky decomposition.

Other entries are specific to each numerical method, see the example cases.

## `desc.csv`

Each column correspond to one individual kernel based on one variable. The complete kernel is computed as a linear combination of individual kernels. The various lines for a given column are:

1. Variable name: the variable in `data.csv` on which the kernel is computed
2. Weight: weight in the linear combination of kernels
3. Kernel: kernel and parameter in the format `name(parameters value separated by commas)`

## `learnData.csv` or `predictData.csv`

The data used for the computation. Each column represent a separate variable. The first line contains the variable name. The second line contains the variable type. All subsequent lines contain the data, with one observation per line.