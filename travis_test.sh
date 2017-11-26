#!/bin/bash
echo
echo Build WatchDogServer
echo
rake
cd ..

mvn clean verify
