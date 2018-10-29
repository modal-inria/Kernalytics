# Dependencies

Kernalytics is cross-built for Scala 2.11 and Scala 2.12. It depends on sbt and scala, which can be obtained on Ubuntu via:

```bash
sudo apt install scala sbt
```

On macOS, [Homebrew](https://brew.sh/) is required. Once installed, the command to get scala and sbt simply is:

```bash
brew install scala sbt
```

It is recommended to run `rscala::scalaVersionJARs()` in R to get the version numbers supported by rscala, and crossbuild against them.

# Eclipse

- In Eclipse, activate the option "Insert spaces for tabs"
- To build the jar and transfer it to the R project, run the script [update assembly](updateAssembly.sh)
