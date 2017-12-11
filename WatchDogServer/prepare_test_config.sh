#!/bin/sh

if [ ! -e config.yaml ];
then
  cp config.yaml.tmpl config.yaml
fi
