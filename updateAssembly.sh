cd kernalytics; sbt +assembly; cd ..
cp kernalytics/target/scala-2.11/Kernalytics-assembly-1.0.jar kernalyzr/inst/java/scala-2.11
cp kernalytics/target/scala-2.12/Kernalytics-assembly-1.0.jar kernalyzr/inst/java/scala-2.12
R CMD INSTALL --no-multiarch --with-keep.source kernalyzr
