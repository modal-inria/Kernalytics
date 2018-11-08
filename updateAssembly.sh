#!/bin/sh

git pull
sbt +assembly
mkdir -p kernalyzr/inst/java/scala-2.11
cp target/scala-2.11/Kernalytics-assembly-1.0.jar kernalyzr/inst/java/scala-2.11
mkdir -p kernalyzr/inst/java/scala-2.12
cp target/scala-2.12/Kernalytics-assembly-1.0.jar kernalyzr/inst/java/scala-2.12
cd kernalyzr
R CMD INSTALL --no-multiarch --with-keep.source .
