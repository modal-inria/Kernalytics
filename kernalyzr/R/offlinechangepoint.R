# Offline changepoint segmentation for real data
#
# @param data data.frame or matrix (do not contain the second line with Real, ...)
# @param desc named matrix containing the kernel to apply
# @param Dmax maximal number of segments
# @param gramOpti méthode LowRank() pas gérée pour le moment
# @param folder folder to save data and output
# @param rmCreatedFiles if TRUE, remove generated files
#
# @return a list containing:
# \itemize{
#   \item D number of segments
#   \item tau changepoints (end of segments/ start of segment (-1))
#   \item raw.cost
#   \item regressed.cost penalty
#   \item penalized.cost slope heuristic
#   \item data input data
#   \item desc kernel description
#   \item algo algo description
# }
#
# @details
# create the following files in folder: c("/algo.csv", "/learnData.csv", "/desc.csv", "/paramTau.csv", "/error.txt")
#
# @examples
# desc <- matrix(c(c(0.25, "Gaussian(1.0)"), c(0.25, "Linear()"), c(0.5, "Gaussian(0.7)")), nrow = 2)
# colnames(desc) = c("GaussA", "GaussA", "GaussB")
#
# data <- data.frame(GaussA = c(rnorm(100, 1, 0.8), rnorm(200, 2, 0.8), rnorm(20, 1, 0.5)), GaussB = c(rnorm(100, 1, 0.8), rnorm(200, 2, 0.8), rnorm(20, 1, 0.5)))
#
# res <- realOfflineCpt(data, desc, Dmax = 6)
#
# @author Quentin Grimonprez
#
# @export
realOfflineCpt <- function(data, desc, Dmax, gramOpti = c("Direct()", "Cache()"), folder = "./offlinechangepoint/", rmCreatedFiles = TRUE)
{
  ## data preparation
  gramOpti = match.arg(gramOpti)

  algo = data.frame(algo = "offlinechangepoint", DMax = Dmax, gramOpti = gramOpti)

  res <- list(data = data, algo = algo, desc = desc)
  data = rbind(rep("Real", ncol(data)), data)

  existingFolder <- dir.exists(folder)
  if(!existingFolder)
    dir.create(folder, recursive = TRUE, showWarnings = FALSE)

  write.table(algo, paste0(folder, "/algo.csv"), sep = ";", quote = FALSE, row.names = FALSE)
  write.table(data, paste0(folder, "/learnData.csv"), sep = ";", quote = FALSE, row.names = FALSE)
  write.table(desc, paste0(folder, "/desc.csv"), sep = ";", quote = FALSE, row.names = FALSE)

  ## run
  kernelLearn(folder)

  ## get results
  out <- read.table(paste0(folder, "/paramTau.csv"), sep = ";", header = TRUE)

  res = c(list(tau = convertTau(out), D = as.numeric(out$D), raw.cost = as.numeric(out$raw.cost),
               regressed.cost = as.numeric(out$regressed.cost), penalized.cost = as.numeric(out$penalized.cost)), res)
  class(res) = "RealOffCpt"

  ## delete created files
  if(rmCreatedFiles)
  {
    if(existingFolder)
      suppressWarnings(file.remove(paste0(folder, c("/algo.csv", "/learnData.csv", "/desc.csv", "/learnCost.csv", "/paramTau.csv", "/error.txt"))))
    else
      unlink(folder, recursive = TRUE)
  }

  return(res)
}


# Plot signal with cpt
#
# @param res output of \link{realOfflineCpt} function
#
# @examples
# desc <- matrix(c(c(0.25, "Gaussian(1.0)"), c(0.25, "Linear()"), c(0.5, "Gaussian(0.7)")), nrow = 2)
# colnames(desc) = c("GaussA", "GaussA", "GaussB")
#
# data <- data.frame(GaussA = c(rnorm(100, 1, 0.8), rnorm(200, 2, 0.8), rnorm(20, 1, 0.5)), GaussB = c(rnorm(100, 1, 0.8), rnorm(200, 2, 0.8), rnorm(20, 1, 0.5)))
#
# res = realOfflineCpt(data, desc, Dmax = 6)
#
# plot(res)
#
#
# @author Quentin Grimonprez
#
# @export
plot.RealOffCpt <- function(res, D = NULL)
{
  if(is.null(D))
    D = which.min(res$penalized.cost)

  nSignal <- ncol(res$data)

  oldPar <- par(mfrow = n2mfrow(nSignal))
  on.exit(par(oldPar))
  for(i in 1:nSignal)
  {
    plot(res$data[,i], type = "l", xlab = "Time", ylab = colnames(data)[i])
    abline(v = res$tau[D, ], lty = "dashed", col = "red")
  }
}

# Plot slope heuristic criterion
#
# @param res output of \link{realOfflineCpt} function
#
# @examples
# desc <- matrix(c(c(0.25, "Gaussian(1.0)"), c(0.25, "Linear()"), c(0.5, "Gaussian(0.7)")), nrow = 2)
# colnames(desc) = c("GaussA", "GaussA", "GaussB")
#
# data <- data.frame(GaussA = c(rnorm(100, 1, 0.8), rnorm(200, 2, 0.8), rnorm(20, 1, 0.5)), GaussB = c(rnorm(100, 1, 0.8), rnorm(200, 2, 0.8), rnorm(20, 1, 0.5)))
#
# res = realOfflineCpt(data, desc, Dmax = 6)
#
# plotSlopeHeuristic(res)
#
#
# @author Quentin Grimonprez
#
# @export
plotSlopeHeuristic <- function(res)
{
  matplot(cbind(res$raw.cost, res$regressed.cost, res$penalized.cost), type = "l", xlab = "Number of segments",
          ylab = "Cost", col = c(4, 2, 1), lty = c(1, 2, 1), lwd = c(1, 1, 2), main = "Slope heuristic")
  abline(v = which.min(res$penalized.cost), lty = "dashed")
  legend("topright", c("Criterion", "Slope", "Penalized criterion"), col = c(4, 2, 1), lty = c(1, 2, 1), lwd = c(1, 1, 2))
}


# Extract the changepoint
#
# @param res data.frame containing the content of "paramTau.csv" file
#
# @return matrix tau containing the changepoint (end of segment/ start of segment(-1))
convertTau <- function(res)
{
  out <- matrix(nrow = nrow(res), ncol = nrow(res))
  tau <- lapply(strsplit(as.character(res$tau), split = ","), as.numeric)

  for(i in seq_along(tau))
  {
    out[i, seq_along(tau[[i]])] = tau[[i]]
  }

  return(out)
}
