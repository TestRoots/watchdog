#!/bin/bash

ideaVersion="14.1"

# Get our IDEA dependency
wget http://download.jetbrains.com/idea/ideaIC-${ideaVersion}.tar.gz

# Unzip IDEA
tar zxf ideaIC-${ideaVersion}.tar.gz
rm -rf ideaIC-${ideaVersion}.tar.gz

# Move the versioned IDEA folder to a known location
ideaPath=$(find . -name 'idea-IC*' | head -n 1)
mv  ${ideaPath} ./idea-IC
	
# Compress to ZIP file
zip -r ideaIC.zip idea-IC/*

# Install IDEA to Maven repo
mvn install:install-file -Dfile=ideaIC.zip -DgroupId=org.jetbrains -DartifactId=org.jetbrains.intellij-ce -Dversion=${ideaVersion} -Dpackaging=zip
