# only for real
#
# data data.frame or matrix (do not contain the second line with Real, ...)
# desc matrix
#
# gramOpti méthode LowRank() pas gérée pour le moment
#
# create the following files in folder: c("/algo.csv", "/learnData.csv", "/desc.csv", "/learnCost.csv", "/paramTau.csv", "/error.txt")
#
# @examples
# desc <- matrix(c(c(0.25, "Gaussian(1.0)"), c(0.25, "Linear()"), c(0.5, "Gaussian(0.7)")), nrow = 2)
# colnames(desc) = c("GaussA", "GaussA", "GaussB")
#
# data <- data.frame(GaussA = c(rnorm(100, 1, 0.8), rnorm(200, 2, 0.8), rnorm(20, 1, 0.5)), GaussB = c(rnorm(100, 1, 0.8), rnorm(200, 2, 0.8), rnorm(20, 1, 0.5)))
#
# res = realOfflineCpt(data, desc, Dmax = 6)
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
  learnCost <- read.table(paste0(folder, "/learnCost.csv"), sep = ";", header = TRUE)
  tau <- drop(as.matrix(read.table(paste0(folder, "/paramTau.csv"), sep = ";", header = TRUE)))

  res = c(list(learnCost = learnCost, tau = tau), res)
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

# plot signal with cpt
plot.RealOffCpt <- function(res)
{
  nSignal <- ncol(res$data)

  par(mfrow = n2mfrow(nSignal))
  for(i in 1:nSignal)
  {
    plot(res$data[,i], type = "l", xlab = "Time", ylab = colnames(data)[i])
    abline(v = res$tau, lty = "dashed", col = "red")
  }
  par(mfrow = c(1, 1))
}

# plot slope heuristic criterion
plotSlopeHeuristic <- function(res)
{
  matplot(res$learnCost, type = "l", xlab = "Number of segments", ylab = "Cost", col = c(4, 2, 1), lty = c(1, 2, 1), lwd = c(1, 1, 2), main = "Slope heuristic")
  abline(v = length(res$tau) + 1, lty = "dashed")
  legend("topright", c("Criterion", "Slope", "Penalized criterion"), col = c(4, 2, 1), lty = c(1, 2, 1), lwd = c(1, 1, 2))
}
