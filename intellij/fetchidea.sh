#!/bin/bash

idea_version="2017.2.7"
idea_directory_name="idea-IU-"
idea_tar="$idea_directory_name$idea_version.tar.gz"
idea_URL="https://download.jetbrains.com/idea/ideaIU-$idea_version.tar.gz"
script_dir=$(dirname "$(readlink -f "$0")")
build_dir="$script_dir/build_cache"

echo "Creating directory '$build_dir'"
mkdir -p $build_dir
cd $build_dir

# Cache IntellIJ download. If not available, download anew (big!)
if [ ! -f $idea_tar ];
   then
   echo "File idea_tar not found. Loading from the Internetz ..."
   wget $idea_URL -O $idea_tar || { echo "Failed to download IntelliJ, you probably need to update the version to a version listed on https://www.jetbrains.com/idea/download/previous.html"; exit 1; }
fi

has_directory=$(find . -maxdepth 1 -type d -name "$idea_directory_name*" | head -n 1)

if [ ! $has_directory ];
   then
   echo "Idea folder does not exist, extracting '$idea_tar'"
   tar zxf $idea_tar
fi

# Recompute if we just extracted and it initially did not exist
idea_directory=$(find . -maxdepth 1 -type d -name "$idea_directory_name*" | head -n 1)

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

cd $build_dir

checkstyle_version="5.18.3"
checkstyle_directory_name="CheckStyle-"
checkstyle_zip="$checkstyle_directory_name$checkstyle_version.zip"
checkstyle_idea_URL="http://plugins.jetbrains.com/plugin/download?rel=true&updateId=44080"

if [ ! -f $checkstyle_zip ];
    then
    echo "File $checkstyle_zip not found. Loading from the Internetz ..."
    wget $checkstyle_idea_URL -O $checkstyle_zip || { echo "Failed to download CheckStyle IDEA plugin zip. Was the plugin version removed from http://plugins.jetbrains.com/plugin/1065-checkstyle-idea ? "; exit 1; }
fi

cd ..

mvn install:install-file -Dfile=$build_dir/$checkstyle_zip -DgroupId=org.infernus.idea -DartifactId=checkstyle -Dversion=$checkstyle_version -Dpackaging=zip