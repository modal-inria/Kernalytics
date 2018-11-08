#' Copy the examples in current directory, for easier testing
copyTest <- function() {
    file.copy(system.file("extdata", "offlinechangepoint", package = "kernalyzr"), ".", recursive = TRUE)
    file.copy(system.file("extdata", "regression", package = "kernalyzr"), ".", recursive = TRUE)
    file.copy(system.file("extdata", "svm", package = "kernalyzr"), ".", recursive = TRUE)
}

runTest <- function() {
  kernelLearn("offlinechangepoint")

  kernelLearn("regression")
  kernelPredict("regression")

  kernelLearn("svm")
  kernelPredict("svm")
}
