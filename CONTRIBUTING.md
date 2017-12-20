On this page, we describe the development process that all team members contributing to TestRoots must adhere to.

# Getting started
This section gives a brief overview of the steps necessary to have a working development environment for WatchDog. This should work for Linux, Windows, and MacOs.

1. Install Git. Install Eclipse. Install IntelliJ. Details under section IDE on this page. (Optional: Install Eclipse plugins.)
2. Install Maven.
3. Clone Git repository branch master.
4. Run `./intellij/fetchidea.sh` to obtain a local version of IntelliJ for the project.
5. Setup the WatchDog project for IntelliJ and Eclipse (see later steps)
6. Install WatchDog for IntelliJ and Eclipse and set it up at least for your WatchDog development workspaces.

Study the section "Workflow" to get to know how we develop.

# IDE
WatchDog is an Eclipse and IntelliJ plugin. It is built and written for Java 1.8. Following the "eat your own dog food" principle, all developers should have WatchDog installed and running in their development instance of the IDE.

In this section, we describe how WatchDog needs to be setup. It is important to have both plugins and development environments set-up to catch potential induced problems even if you only intend to modify one.

## Eclipse
1. Download Eclipse.
1. Install the PDE plugin (https://marketplace.eclipse.org/content/eclipse-pde-plug-development-environment)
1. Import the `pom.xml` in the root folder
1. Optional: For Maven IDE support, you need [m2e](https://www.eclipse.org/m2e/download/) for IDE-supported POM.xml-file editing. As we are also using [Tycho](https://eclipse.org/tycho/), you need the tycho configuration connector in m2e's marketplace.
1. Works.

## IntelliJ
1. Download the free IC edition
1. Import the `pom.xml` in the root folder
1. Add local version of IntelliJ to your IntelliJ running per instructions of https://www.jetbrains.com/help/idea/configuring-intellij-platform-plugin-sdk.html
1. Create a new Run Configuration.
    1. Classpath should point to `watchdog-intellij-plugin`
    1. JRE should be the local version of IntelliJ that exists in `intellij/build_cache/`
1. Open `WatchDogStartup.java` and click on the run configuration. A new version of IntelliJ should pop up with a local version of WatchDog running.
1. Works.

As our headless build tool we are using Maven.

# Install WatchDog
To install WatchDog locally, follow the process described on http://testroots.org/testroots_watchdog.html.
There are two additional options that might be interesting to you:

1. To install your home-built version of WatchDog, click on `Local`. Once you ran step (4), your local build is located under `<Git repository>/WatchDogEclipse/p2updatesite/target/repository` and `<Git repository>/WatchDogIntelliJ/WatchDog/WatchDog.zip`. Add this folder/zip as a plugin. Follow the instructions in the dialogue and restart Eclipse to activate WatchDog. **or**
2. The latest development build is stored under `http://www.watchdog.testroots.org/updatesite_snaphsot` (checked-out automatically and built on master).


# Workflow
0. If you are not a core committer: Fork the WatchDog repo.
1. Create a new issue in issue tracker https://github.com/TestRoots/watchdog/issues.
2. Create local branch in git: `git branch your_branch`
3. Make your modifications. Every commit must reference the issue id from (1).
5. Make sure the project builds correctly through travis_build.sh.
4. Once your work is complete, push the local branch to remote: `git push --set-upstream origin your_branch`
5. File a pull request on the remote branch for integration to `master`.

## Issue Task Manager
We manage our issues and tasks in Github. Every commit must reference an issue on Github in its commit message, like so #(issueId). If a commit closes an issue, this can be done via fixes #(issueId). Non-technical tasks can and should also be managed via Github's issue manager.

## Build dependencies
Eclipse plugins are OSGI bundles. As such, they are Manifest-first in the maven build process and therefore cannot use Maven's pom-first dependency resolution mechanism. Therefore, we manage our dependencies manually in the lib folder.

## Continuous Integration
We use Travis CI as our build server. It can be reached under https://travis-ci.org/TestRoots/watchdog.

At the moment, our build status is ![](https://api.travis-ci.org/TestRoots/watchdog.png?branch=master).

## Testing
Being quality-driven, WatchDog makes use of unit testing. the JUnit tests are collected in an Eclipse fragment project, complementing the original Eclipse IDE plugin.

## Static Analysis
In WatchDog, we heavily rely on static analysis to ensure product quality.

### Code Review
Every change must undergo code review before acceptance into the main branch. This is done by developing on your own local branch, and then filing a pull request for the branch. Another member of the team will review the pull request, and give you feedback.

### Automated Static Analysis
We use a number of configured automated static analysis tools such as FindBugs, Codeclimate and Teamscale to monitor the quality of our product. We use Teamscale as a dashboard to monitor our general product quality. Register yourself here: https://demo.teamscale.com The number of findings in our product should be kept as low as possible.

## Deployment
The Eclipse IDE updatesite needs to be signed before deployment. Deployment is then a simple mv of directories on our server. For IntelliJ, the plugin repository needs to be updated.

## Creating a new release
Builds are created automatically every hour on the server, and on-demand on the CI.
Creating a new relase involves increasing the version number in several places.
To make things easier, you can use this command (to bump from 1.0.1 to 1.0.2 in this example):
`find . -type f ! -path "*.git*" ! -path "*/libs/*" | xargs sed -i 's/1\.0\.1/1\.0\.2/g'`
Please check the diff before committing!


1. Export ZIP for IntelliJ via Build > Prepare Plugin Module ...
Upload the generated ZIP to the IntelliJ repository

2. Sign the generated Eclipse packages

3. Update update_site on server

4. Restart server (for updated version string)

## Sign it
We only sign Eclipse releases.

1. Go to `git-repo/WatchDogEclipsePlugin/p2updatesite/target/repository`
2. Make sure `export JAVA_HOME=/usr/lib/jvm/java-8-oracle`
3. Eplace STOREPASS and KEYPASS and run `find . -type f -follow -print | xargs -i  jarsigner -keystore "$JAVA_HOME/jre/lib/security/cacerts" -storepass "STOREPASS" -keypass "KEYPASS" -verbose '{}' 72a9b5399b49480482699d126b4ee9e5`
4. Update Eclipse Updatesite on server
5. Git pull and Restart server
6. Update Eclipse Marketplace
7. Upload to IJ repository
