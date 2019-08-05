# How to build Kernalytics

Check that sbt is installed on your computer. Follow the *Dependencies* instructions in this document if that is not the case. sbt can take care of most tasks when developing the scala library.

## Java JDK

The version 1.8 of the jdk is required. More recent versions will not be compatible with the Scala compiler. You can get a build of OpenJDK for most platforms at [AdoptOpenJDK](https://adoptopenjdk.net).

## Windows

Simply get the tools from their main site:
- [AdoptOpenJDK](https://adoptopenjdk.net)
- [sbt](https://www.scala-sbt.org/)

## Ubuntu

Kernalytics is built for Scala 2.12. It depends on sbt and git is called from the configure script. They can be obtained on Ubuntu via:

```bash
sudo apt install git sbt openjdk-8-jdk
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

and the command to get sbt and git is:

```bash
brew install git sbt
```

