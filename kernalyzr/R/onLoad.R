.onLoad <- function(libname, pkgname) {
    if (rscala::scalaConfig()$scalaMajorVersion != "2.12") {
      rscala::scalaConfig(download="scala")
    }
    
  assign("s", scala(pkgname), envir = parent.env(environment()))
}

.onUnload <- function(libpath) {
  .close(s)
}
