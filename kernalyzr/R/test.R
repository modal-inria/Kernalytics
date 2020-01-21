# Copy the examples in current directory, for easier testing
#
# @author Vincent Kubicki
copyTest <- function() {
    file.copy(system.file("extdata", "offlinechangepoint", package = "kernalyzr"), ".", recursive = TRUE)
    file.copy(system.file("extdata", "regression", package = "kernalyzr"), ".", recursive = TRUE)
    file.copy(system.file("extdata", "svm", package = "kernalyzr"), ".", recursive = TRUE)
}

# Run examples
#
# @author Vincent Kubicki
#
# @export
runTest <- function() {
  copyTest()

  kernelLearn("offlinechangepoint")

  kernelLearn("regression")
  kernelPredict("regression")

  kernelLearn("svm")
  kernelPredict("svm")
}
