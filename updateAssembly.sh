#!/bin/sh

# This script is called by kernalyzr to build a fat assembly jar of Kernalytics and all its dependencies.

git pull
sbt assembly

rm -Rf kernalyzr/inst/java/scala-2.12
mkdir -p kernalyzr/inst/java/scala-2.12
cp target/scala-2.12/Kernalytics-assembly-1.0.jar kernalyzr/inst/java/scala-2.12
