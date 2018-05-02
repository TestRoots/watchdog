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
1. Import `Maven -> Existing Maven project` and select the watchdog root folder. Import all subprojects except the IntelliJ folder (or remove it later if you have imported it, it will give build errors).
1. Optional: For Maven IDE support, you need [m2e](https://www.eclipse.org/m2e/download/) for IDE-supported POM.xml-file editing. As we are also using [Tycho](https://eclipse.org/tycho/), you need the tycho configuration connector in m2e's marketplace.
1. Optional: If your run configuration complains about unrecognized options, make sure to remove all inclusions of `--add-modules=ALL-SYSTEM` in your VM arguments. These are added incorrectly by PDE and won't work on a JDK 1.8 or lower.
1. Select the correct target platform:
  1. `Window -> Preferences -> Plug-in Development -> Target Platform`
  1. Select the target platform from `/watchdog/eclipse/platform/watchdog.eclipse.platform.target`

## IntelliJ
1. Download the free IC edition
1. Install the "Intellij plugin development with Maven"-plugin from https://plugins.jetbrains.com/plugin/7127-intellij-plugin-development-with-maven
1. Start by Importing a project and selecting `pom.xml` in the root folder of the repository. When importing, you can step through with the defaults. The process of fetching dependencies by IntelliJ can take a while.
1. Add a local version of the IntelliJ SDK to your IntelliJ running per instructions of https://www.jetbrains.com/help/idea/configuring-intellij-platform-plugin-sdk.html
    1. In Project Structure, go to Modules -> watchdog -> intellij.
    1. On the tab "dependencies", click new module SDK
    1. Select the IntelliJ SDK from the local version available in `intellij/build_cache/idea-IU-***`
1. Create a new Run Configuration.
    1. Run -> Edit Configurations -> New -> Plugin.
    1. Classpath should point to `watchdog.intellij`
    1. JRE should be the local version of IntelliJ that exists in `intellij/build_cache/`
1. Double check that in `Project Structure -> Modules -> watchdog -> intellij -> Plugin Deployment`, the Path to `META-INF/plugin.xml` is `watchdog/intellij/resources/`. You can safely delete the `intellij/META-INF/` folder now.
1. Install the Checkstyle Plugin via Settings > Plugins > Browse repositories ... and searching for "CheckStyle-IDEA". The plugin comes from https://infernus.org/ and https://github.com/jshiell/checkstyle-idea.
1. Open `WatchDogStartup.java` and click on the run configuration. A runtime workbench of IntelliJ should pop up with a local version of WatchDog running.

## Subprojects

Using either IDE, you will import multiple subprojects.
This section explains the structure of this project with its subprojects and their corresponding purpose.

- `core`

  Core contains all code that is shared between all custom IDE implementations.
  It mostly focuses on the data format, its storage method and the network protocols.

- `eclipse`

  Project that contains several other subprojects all related to the Eclipse plugin.
  Most of these projects are necessary to generate a correct OSGi bundle that can be consumed by Eclipse.

  - `plugin`

    The plugin contains the actual implementation of the plugin.
    It augments `core` by all custom logic to wire into the Eclipse editor.
    You can start the plugin by running `nl.tudelft.watchdog.eclipse.Activator` with `Run As Eclipse Application`.

  - `tests`

    An Eclipse plugin test project that runs an editor and instruments the Eclipse plugin either directly or indirectly.

  - `platform`

    To correctly build both the plugin and its tests, the plugin requires a target platform which specify its dependencies.
    For more information, read [this documentation page](http://www.vogella.com/tutorials/EclipseTargetPlatform/article.html).

  - `features`

    Based on the plugin and the target platform, both the `core` and `eclipse.plugin` need to build a feature that can be consumed by Eclipse OSGi.
    The features do not contain any code implementation, rather they contain a `feature.xml` and `build.properties` to configure the Eclipse OSGi dependencies.

  - `p2updatesite`

    Both features are then grouped into one artifact called a repository.
    This repository is the final output that will be uploaded to the Eclipse updatesite such that users can install the plugin.

- `intellij`

  The plugin implementation that augments `core` into the IntelliJ Idea editor.
  You can start the plugin by using the run configuration as explained in the IntelliJ section down below.

- `server`

  A Ruby server that processes user/project creation as well as any other data we require and inserts it into a Mongo database.
  This server therefore requires Mongo to be running locally on your machine.


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
To make things easier, you can use this command (to bump from 3.0.0 to 3.1.0 in this example):
`find . -type f ! -path "*.git*" ! -path "*/libs/*" ! -path "*/target/*" ! -path "*/build_cache/*" | xargs sed -i 's/3\.0\.0/3\.1\.0/g'`
Please check the diff before committing!


1. Export ZIP for IntelliJ via Build > Prepare Plugin Module ...
Upload the generated ZIP to the IntelliJ repository

2. Sign the generated Eclipse packages

3. Update update_site on server

4. Restart server (for updated version string)

## Sign it
We only sign Eclipse releases.

1. Go to `cd eclipse/p2updatesite/target/repository/`
2. Make sure `export JAVA_HOME=/usr/lib/jvm/java-8-oracle`
3. Eplace STOREPASS and KEYPASS and run `find . -type f -follow -print | xargs -i  jarsigner -keystore "$JAVA_HOME/jre/lib/security/cacerts" -storepass "STOREPASS" -keypass "KEYPASS" -verbose '{}' 72a9b5399b49480482699d126b4ee9e5`
4. Update Eclipse Updatesite on server
5. Git pull and Restart server
6. Update Eclipse Marketplace
7. Upload to IJ repository
