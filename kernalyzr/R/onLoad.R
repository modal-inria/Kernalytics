# @author Vincent Kubicki
.onLoad <- function(libname, pkgname) {
    if (scalaConfig()$scalaMajorVersion != "2.12") {
      scalaConfig(download = "scala")
    }

  assign("s", scala(pkgname), envir = parent.env(environment()))
}

.onUnload <- function(libpath) {
  close(s)
}
