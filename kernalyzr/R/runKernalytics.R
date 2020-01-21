#' run Kernalytics in Learn mode
#'
#' @param rootFolder folder containg data.csv, desc.csv and algo.csv
#'
#' @author Vincent Kubicki
#'
#' @export
kernelLearn <- function(rootFolder) {
  return(s$exec.Learn.main(rootFolder))
}

#' run Kernalytics in Predict mode
#'
#' @param rootFolder folder containg data.csv, desc.csv and algo.csv
#'
#' @author Vincent Kubicki
#'
#' @export
kernelPredict <- function(rootFolder) {
  return(s$exec.Predict.main(rootFolder))
}
