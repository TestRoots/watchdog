#!/bin/bash

idea_version="2017.2.5"
idea_zip="ideaIU-$idea_version.tar.gz"
idea_URL="https://download.jetbrains.com/idea/$idea_zip"
script_dir=$(dirname "$(readlink -f "$0")")
build_dir="$script_dir/build_cache"

mkdir -p $build_dir
cd $build_dir

# Cache IntellIJ download. If not available, download anew (big!)
if [ ! -f $idea_zip ];
   then
   echo "File $idea_zip not found. Loading from the Internetz ..."
   wget $idea_URL
fi

idea_path=$(find . -type d -name 'idea-IU*' | head -n 1)

if [ ! -f ${idea_path}.zip ];
   then
   # Unzip IDEA
   tar zxf $idea_zip

   # Compress to ZIP file
   zip -rq ${idea_path}.zip $idea_path/*
fi

cd ..

# Install IDEA to Maven repo
mvn install:install-file -Dfile=$build_dir/${idea_path}.zip -DgroupId=org.jetbrains -DartifactId=org.jetbrains.intellij-ce -Dversion=${idea_version} -Dpackaging=zip
