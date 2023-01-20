# Welcome to the Apache JUDDI Project!

Here's some quick notes for building, testing and deploying JUDDI from source.

See our website at https://juddi.apache.org 

# NOTICE

December 2023 - Apache jUDDI discussed potentially retiring the project.
An annoucement was made via the ASF mailing lists. No response was received.

January 2023 - Apache jUDDI PCM team held a formal vote (it passed) to retire the project. 
It will be moving to the Apache Attic in the near future. See https://attic.apache.org/ for additional details.

## What does this mean?

If you're still using Apache jUDDI, there will be no more releases from
the jUDDI team at Apache. The source code and binaries will always be
available, however there will be no support for security updates, patches, 
or new features.

We recommend to find a new solution, mitgrate to a different product, 
or if you're willing, fork Apache jUDDI and perform your own maintenance.


# Building

1) Acquire a JDK8 and setup the JAVA_HOME environment variable
2) Acquire Apache Maven. Known working version: 3.2.1 or newer
3) Setup an environment variable, MAVEN_OPTS=-Xmx768m -XX:MaxPermSize=512m
4) Make sure the Maven/bin folder and the JDK/bin folders are in the current path
5) execute `mvn clean install`

That should build the key modules of the project and test most of the Java components. Depending on your computer's speed, it can take up to 15 minutes to build.

To enable additional output during the build and test project:

	mvn clean install -Ddebug=true

To build all of the project modules, including utilities and sample projects, run the majority of the integration tests and documentation (this is our CI build):

	mvn clean install -Pdist
	
	
To attach the debugger to the build process

	mvn -Dmaven.surefire.debug clean install

It listens on port 5005 by default. More info on debugging maven projects is here http://maven.apache.org/surefire/maven-surefire-plugin/examples/debugging.html

## IDE imports

To setup an Eclipse environment, simply import the maven project.

To setup a Netbeans environment, start netbeans and open the maven based project


## To build your changes locally and skip the the tests run:

`mvn install -DskipTests=true`

To start Juddi's embedded Tomcat server:

	juddi-tomcat\target\tomcat\apache-tomcat-9.0.XX\bin\startup.bat
	juddi-tomcat\target\tomcat\apache-tomcat-9.0.XX\bin\startup.sh

To build the .NET components of jUDDI

	cd juddi-client.net

mono users use: `xbuild juddi-client.net-mono.sln`

.net users user: `msbuild juddi-client.net.sln`

To test the .NET components of jUDDI using nunit

	nunit-console.exe juddi-client.net.test\bin\Debug\juddi-client.net.test.dll

To run the integration tests on the .NET components of jUDDI using nunit

	catalina run (start the jUDDI tomcat server)
	nunit-console.exe juddi-client.net-integration.test\bin\Debug\juddi-client.net-integration.test.dll

OpenJDK users: please install icedtea-plugin if you run into trouble with the juddi-gui-dsig project

 