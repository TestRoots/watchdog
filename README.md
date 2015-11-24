TestRoots Watchdog
==================

WatchDog is a development effort from the TestRoots of the Software Engineering Research Group at TU Delft. The WatchDog project comprises an eclipse plugin for application development monitoring and its server.

We are on the web: http://www.testroots.org

[![Build Status](https://travis-ci.org/TestRoots/watchdog.svg?branch=master)](https://travis-ci.org/TestRoots/watchdog) [![Coverity Status](https://scan.coverity.com/projects/4880/badge.svg)](https://scan.coverity.com/projects/4880) [![Scrutinizer Code Quality](https://scrutinizer-ci.com/g/TestRoots/watchdog/badges/quality-score.png?b=master)](https://scrutinizer-ci.com/g/TestRoots/watchdog/?branch=master)  [![Test Coverage](https://codeclimate.com/github/TestRoots/watchdog/badges/coverage.svg)](https://codeclimate.com/github/TestRoots/watchdog/coverage)


Get Started To Develop
----------------------
In this section, we describe how WatchDog needs to be setup. It is important to have both plugins and development environments set-up to catch 

### Eclipse
1. Download RCP or PDE version.
2. Import all projects from repositories.
3. Works.

### IntelliJ
1. Download IC (for testing/SDK), IU (for development) edition
2. Run IU, create a new Empty Project, best outside of the git repository to avoid accidentally committing it.
3. (Under File > Project Structure >) Project, define IC as a new Platform SDK. Select it as the Project SDK for the whole Project.
4. Import WatchDogIntelliJ as an IntelliJ module by selecting the IML file from its subfolder. We now have 1 module in our project.
5. Import WatchDogCore as an IntelliJ module by selecting the IML file from its subfolder. We now have 2 modules in our project.
6. Rebuild the project (Build > rebuild). You will receive an error from the Maven Resources Compiler.
7. Right-click on WatchDogCore, select "Add as Maven project".
8. File > Project Sturcture and from remove the newly created WatchDogCore module and the fully qualified nl.tudelft.WatchDogCore (leaving "> Core" and WatchDog), as in step 5.
9. Works.
