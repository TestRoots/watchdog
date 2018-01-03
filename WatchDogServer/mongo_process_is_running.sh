#!/bin/sh

if pgrep mongod > /dev/null
then
    echo "Verified Mongo is running"
    exit 0
else
    echo "Mongo is not running! Make sure the service is running before running any test"
    exit 1
fi