Welcome to the Apache JUDDI Project!

Here's some quick notes for building, testing and deploying JUDDI from source.

1) Acquire a JDK5 or higher and setup the JAVA_HOME environment variable
2) Acquire Apache Maven. Known working version: 3.0.4
3) Setup an environment variable, MAVEN_OPTS=-Xmx768m -XX:MaxPermSize=512m
4) Make sure the Maven/bin folder and the JDK/bin folders are in the current path
5) execute "mvn clean install"

That should build the whole project and test all Java components. Depending on your computer's speed, it can take up to 15 minutes to build.

To enable additional output during the build and test project:
	mvn clean install -Ddebug=true

To attach the debugger to the build process
mvn -Dmaven.surefire.debug clean install
It listens on port 5005 by default. More info on debugging maven projects is here http://maven.apache.org/surefire/maven-surefire-plugin/examples/debugging.html

To setup an Eclipse environment with support for building using the Google Web Toolkit portlets in Pluto, see the blog entry here http://apachejuddi.blogspot.com/2013_02_01_archive.html
Eclipse will initially complain about maven plugins.

To setup a Netbeans environment, the process is much simpler.
Install Netbeans and open the project. Compiling from Netbeans however doesn't work and you'll have to resort to command line builds


To build your changes locally and skip the the tests run:
mvn install -DskipTests=true

To also build the Pluto/GWT/Portlet interface
cd juddi-console
mvn clean install

To start Juddi's embedded Tomcat server:
juddi-tomcat\target\tomcat\apache-tomcat-6.0.26\bin\startup.bat
juddi-tomcat\target\tomcat\apache-tomcat-6.0.26\bin\startup.sh

To build the .NET components of jUDDI
cd juddi-client.net
mono users use: xbuild juddi-client.net-mono.sln
.net users user: msbuild juddi-client.net.sln

To test the .NET components of jUDDI using nunit
nunit-console.exe juddi-client.net.test\bin\Debug\juddi-client.net.test.dll

To run the integration tests on the .NET components of jUDDI using nunit
catalina run (start the jUDDI tomcat server)
nunit-console.exe juddi-client.net-integration.test\bin\Debug\juddi-client.net-integration.test.dll


UPGRADING
Note: one or more of the database columns names have changed.

OpenJDK users: please install icedtea-plugin if you run into trouble with the juddi-gui-dsig project

