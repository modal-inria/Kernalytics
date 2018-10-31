#' Copy the examples in current directory, for easier testing
copyTest <- function() {
    file.copy(system.file("extdata", "offlinechangepoint", package = "kernalizr"), ".", recursive = TRUE)
    file.copy(system.file("extdata", "regression", package = "kernalizr"), ".", recursive = TRUE)
    file.copy(system.file("extdata", "svm", package = "kernalizr"), ".", recursive = TRUE)
}
