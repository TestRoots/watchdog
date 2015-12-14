#!/bin/bash
echo
pwd
echo Build WatchDogServer
echo 
cd WatchDogServer
bundler
if [ ! -e config.yaml ];
then
  cp config.yaml.tmpl config.yaml
fi
rake
SERVER_STATUS=$?
cd ..

echo
echo Build WatchDogCore
echo
cd WatchDogCore/
mvn clean install
CORE_STATUS=$?
cd ..

echo
echo Build WatchDogIntelliJPlugin
echo
sh WatchDogIntelliJPlugin/fetchIdea.sh
cd WatchDogIntelliJPlugin/
mvn clean verify
INTELLIJ_CLIENT_STATUS=$?
cd ..

echo
echo Build WatchDogEclipsePlugin
echo 
cd WatchDogEclipsePlugin/
mvn integration-test -B
ECLIPSE_CLIENT_STATUS=$?
cd ..

git ls-files -o
cp /home/travis/build/mjduijn/watchdog/WatchDogCore/WatchDog/target/nl.tudelft.WatchDogCore-1.6.0.jar WatchDogCore-1.6.0.jar
cp /home/travis/build/mjduijn/watchdog/WatchDogIntelliJPlugin/WatchDog/target/lib/nl.tudelft.WatchDog-1.6.0.jar WatchDogIntelliJPlugin-1.6.0.jar
cp /home/travis/build/mjduijn/watchdog/WatchDogIntelliJPlugin/WatchDog/target/nl.tudelft.WatchDog-1.6.0-plugin.zip WatchDogIntelliJPlugin-1.6.0.zip
cp /home/travis/build/mjduijn/watchdog/WatchDogEclipsePlugin/WatchDog/target/nl.tudelft.WatchDog-1.6.0.jar WatchDogEclipsePlugin-1.6.0.jar

exit $(($SERVER_STATUS + $CORE_STATUS + $INTELLIJ_CLIENT_STATUS + $ECLIPSE_CLIENT_STATUS))
