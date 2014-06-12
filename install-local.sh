#!/bin/sh
mkdir ivy-local
cd ivy-local
git clone https://github.com/schaloner/deadbolt-2-core.git
git clone https://github.com/schaloner/deadbolt-2-scala.git
cd deadbolt-2-core
sbt ++2.10.4 clean publish-local
cd ../deadbolt-2-scala
sbt ++2.10.4 clean publish-local
cd ../..
rm -Rf ivy-local