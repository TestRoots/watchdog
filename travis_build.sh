#!/bin/bash
cd WatchDogServer
bundle install
if [ ! -e config.yaml ];
then
  cp config.yaml.tmpl config.yaml
fi
cd ..

sh WatchDogIntelliJPlugin/fetchIdea.sh
