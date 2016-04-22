#!/bin/bash

idea_version="15.0.5"
idea_zip="ideaIC-$idea_version.tar.gz"
idea_URL="http://download.jetbrains.com/idea/$idea_zip"
build_dir="build_cache"

mkdir -p $build_dir
cd $build_dir

# Cache IntellIJ download. If not available, download anew (big!)
if [ ! -f $idea_zip ];
   then
   echo "File $idea_zip not found. Loading from the Internetz ..."
   wget http://download.jetbrains.com/idea/$idea_zip
fi

# Unzip IDEA
tar zxf ideaIC-${idea_version}.tar.gz

idea_path=$(find . -type d -name 'idea-IC*' | head -n 1)

if [ ! -f ${idea_path}.zip ];
   then
   # Compress to ZIP file
   cd $idea_path
   zip -r ../${idea_path}.zip *
   cd ..
fi

cd ..

# Install IDEA to Maven repo
mvn install:install-file -Dfile=$build_dir/${idea_path}.zip -DgroupId=org.jetbrains -DartifactId=org.jetbrains.intellij-ce -Dversion=${idea_version} -Dpackaging=zip
