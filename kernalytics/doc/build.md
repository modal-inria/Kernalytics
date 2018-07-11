If you intend to develop / run from the Eclipse IDE, you only need sbt. However, if you want to run the code from R, you must have the same scala version as the one used to generate the jar archive. sbt is not used in rscala. You might want to run:

```bash
sudo apt remove scala-library scala
wget www.scala-lang.org/files/archive/scala-2.12.3.deb
sudo dpkg -i scala-2.12.3.deb
```

- In Eclipse, activate the option "Insert spaces for tabs"
