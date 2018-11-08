# Dependencies

## Ubuntu

Kernalytics is cross-built for Scala 2.11 and Scala 2.12. It depends on sbt and scala, which can be obtained on Ubuntu via:

```bash
sudo apt install scala sbt
```

For Ubuntu 16.04, scala and sbt are not provided in the main repositories. It is possible to follow the instructions provided at https://www.scala-sbt.org/download.html. The command to install the missing dependencies then become:

```echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt install openjdk-8-jdk scala sbt
```

## macOS

On macOS, [Homebrew](https://brew.sh/) is required. Once installed, the command to get scala and sbt simply is:

```bash
brew install scala sbt
```

It is recommended to run `rscala::scalaVersionJARs()` in R to get the version numbers supported by rscala, and crossbuild against them.

# Eclipse

- In Eclipse, activate the option "Insert spaces for tabs"
- To build the jar and transfer it to the R project, run the script [update assembly](updateAssembly.sh)
