demoOfflineChangePoint <- function() {
  dataFile <- system.file("extdata", "data.csv", package = "Kernalizr")
  descFile <- system.file("extdata", "descriptor.csv", package = "Kernalizr")

  print(dataFile)
  print(descFile)
  return(s$.offlinechangepoint.DemoIO$segmentData(dataFile, descFile, 8L))
}
