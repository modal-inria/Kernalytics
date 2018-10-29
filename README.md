Kernel-powered analytics.

Kernel methods are versatile, Kernalytics provides a generic computational engine to take advantage of those properties.

# Quick start for Kernalyzr

Install Scala and sbt on your machine. Lauch the [update assembly](updateAssembly.sh) script which will build Kernalytics, copy the jar to the proper location, and install the Kernalizr (R interface to Kernalytics) on your machine. You can then load the Kernalizr package from your R installation.  Note that there currently the wrapper is pretty thin. One has to generate the proper csv on disk, and the results are exported as csv too.

# Links

- [License](LICENSE)
- [Build instructions](kernalytics/doc/build.md)
- [Use of Scala](kernalytics/doc/scala.md)
- [Algorithm description](kernalytics/doc/algoDesc.md)
- [Things to do](TODO.md)
- [External links](kernalytics/doc/links.md)
