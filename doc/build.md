# How to build from command line

Check that sbt is installed on your computer. Follow the *Dependencies* instructions in this document if that is not the case.

The current development environment used is Scala-IDE, hence all the instructions provided here target that platform. However, Lightbend is pulling support for Scala-IDE, and other alternatives are available. IntelliJ IDEA with the scala plugin enabled seems easier to setup. For example there is no need to run sbt in a separate terminal to generate an IDE configuration.

## For Eclipse (recommended)

- run `sbt eclipse` to generate the project with the correct classpath
- Get Scala-IDE, which is a customization of Eclipse, either a a stand-alone IDE, or as an update site for a current installation of Eclipse
- In Eclipse, activate the option "Insert spaces for tabs"
- To build the jar and transfer it to the R project, run the script [update assembly](/updateAssembly.sh)

## Other methods

Kernalytics is built through sbt. `sbt update` is the most basic way to setup / build it.

# Dependencies

## Java JDK

The version 1.8 of the jdk is required. More recent versions will not be compatible with the Scala compiler. You can get a build of OpenJDK for most platforms at [AdoptOpenJDK](https://adoptopenjdk.net).

## Ubuntu

Kernalytics is built for Scala 2.12. It depends on sbt and git is called from the configure script. They can be obtained on Ubuntu via:

```bash
sudo apt install git sbt
```

For Ubuntu 16.04, sbt is not provided in the main repositories. It is possible to follow the instructions provided at https://www.scala-sbt.org/download.html. The command to install the missing dependencies then become:

```bash
echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update
sudo apt install git sbt
```

## macOS

On macOS, [Homebrew](https://brew.sh/) is required. Once installed, the command to get AdoptOpenJDK is:

```bash
brew tap AdoptOpenJDK/openjdk
brew cask install adoptopenjdk8
```

and the command to get sbt is:

```bash
brew install git sbt
```
