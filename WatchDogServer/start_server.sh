#!/bin/sh

cd .
if [ -z $RACK_ENV ]; then
    export RACK_ENV="production"
fi
echo "Starting Watchdog with RACK_ENV $RACK_ENV"
unicorn --env $RACK_ENV -c unicorn.rb 