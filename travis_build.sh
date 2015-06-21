#!/bin/bash
echo
echo Build WatchDogServer
echo 
cd WatchDogServer
bundler
cp config.yaml.tmpl config.yaml
rake
SERVER_STATUS=$?
rm config.yaml
cd ..

echo
echo Build WatchDogEclipsePlugin
echo 
cd WatchDogEclipsePlugin/
mvn integration-test -B
CLIENT_STATUS=$?
exit $(($SERVER_STATUS + $CLIENT_STATUS))
