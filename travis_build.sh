#!/bin/bash
echo
echo Build WatchDogServer
echo
cd WatchDogServer
bundle install
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

exit $(($SERVER_STATUS + $CORE_STATUS + $INTELLIJ_CLIENT_STATUS + $ECLIPSE_CLIENT_STATUS))
