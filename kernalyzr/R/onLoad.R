.onLoad <- function(libname, pkgname) {
  assign("s", scala(pkgname), envir = parent.env(environment()))
}

.onUnload <- function(libpath) {
  .close(s)
}
