# How to edit Kernalytics

The current development environment used is Scala-IDE, hence all the instructions provided here target that platform. However, Lightbend is pulling support for Scala-IDE, and other alternatives are available. IntelliJ IDEA with the scala plugin enabled seems easier to setup. For example there is no need to run sbt in a separate terminal to generate an IDE configuration. VS Code Studio also seems a promising candidate for Scala editing.

## Intellij IDEA

Simply install the Scala plugin, and open the root Kernalytics directory.

## Eclipse

- run `sbt eclipse` to generate the project with the correct classpath
- Get Scala-IDE, which is a customization of Eclipse, either a a stand-alone IDE, or as an update site for a current installation of Eclipse
- In Eclipse, activate the option "Insert spaces for tabs"
- To build the jar and transfer it to the R project, run the script [update assembly](/updateAssembly.sh)

## no IDE

Kernalytics is built through sbt. `sbt update` is the most basic way to setup / build it.
