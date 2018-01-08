#!/bin/bash

idea_version="2017.2.6"
idea_zip="ideaIU-$idea_version.tar.gz"
idea_URL="https://download.jetbrains.com/idea/$idea_zip"
script_dir=$(dirname "$(readlink -f "$0")")
build_dir="$script_dir/build_cache"

echo "Creating directory '$build_dir'"
mkdir -p $build_dir
cd $build_dir

# Cache IntellIJ download. If not available, download anew (big!)
if [ ! -f $idea_zip ];
   then
   echo "File $idea_zip not found. Loading from the Internetz ..."
   wget $idea_URL || { echo "Failed to download IntelliJ, you probably need to update the version tp a version listed on https://www.jetbrains.com/idea/download/previous.html"; exit 1; }
fi

has_directory=$(find . -type d -name 'idea-IU*' | head -n 1)

if [ ! $has_directory ];
   then
   echo "Idea folder does not exist, extracting '$idea_zip'"
   tar zxf $idea_zip
fi

# Recompute if we just extracted and it initially did not exist
idea_directory=$(find . -maxdepth 1 -type d -name 'idea-IU*' | head -n 1)

if [ ! -f $idea_directory.zip ];
   then
   echo "Compressing directory '$idea_directory' into '$idea_directory.zip'"
   cd $idea_directory
   zip -rq ../$idea_directory.zip *
   cd ../
fi

cd ..

# Install IDEA to Maven repo
mvn install:install-file -Dmaven.exec.skip -Dfile=$build_dir/$idea_directory.zip -DgroupId=org.jetbrains -DartifactId=org.jetbrains.intellij-ce -Dversion=$idea_version -Dpackaging=zip
