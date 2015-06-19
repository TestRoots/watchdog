#!/bin/bash
echo
echo Build WatchDogServer
echo 
cd WatchDogServer
bundler
./run_tests.sh
SERVER_STATUS=$?
cd ..

echo
echo Build WatchDogEclipsePlugin
echo 
cd WatchDogEclipsePlugin/
mvn integration-test -B
CLIENT_STATUS=$?
exit $(($SERVER_STATUS + $CLIENT_STATUS))
