#!/bin/bash

ideaVersion="14.1"

if [ ! -d ./idea-IC ]; then
    # Get our IDEA dependency
    if [ -f ~/Tools/ideaIC-${ideaVersion}.tar.gz ];
    then
        cp ~/Tools/ideaIC-${ideaVersion}.tar.gz .
    else
        wget http://download.jetbrains.com/idea/ideaIC-${ideaVersion}.tar.gz
    fi

    # Unzip IDEA
    tar zxf ideaIC-${ideaVersion}.tar.gz
    rm -rf ideaIC-${ideaVersion}.tar.gz

    # Move the versioned IDEA folder to a known location
    ideaPath=$(find . -name 'idea-IC*' | head -n 1)
    mv ${-IC} ./idea-IC
	
	# Compress to ZIP file
	zip -r ideaIC-${ideaVersion}.zip idea-IC
	rm -rf idea-IC
fi

# Install IDEA to Maven repo
mvn install:install-file -Dfile=ideaIC-${ideaVersion}.zip -DgroupId=org.jetbrains -DartifactId=org.jetbrains.intellij-ce -Dversion=${ideaVersion} -Dpackaging=zip

rm -rf ideaIC-${ideaVersion}.zip
