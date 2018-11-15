# Dependencies

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

On macOS, [Homebrew](https://brew.sh/) is required. Once installed, the command to getsbt simply is:

```bash
brew install git sbt
```

# Eclipse

- In Eclipse, activate the option "Insert spaces for tabs"
- To build the jar and transfer it to the R project, run the script [update assembly](updateAssembly.sh)
