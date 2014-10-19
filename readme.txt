Welcome to the Apache JUDDI Project!

Here's some quick notes for building, testing and deploying JUDDI from source.

1) Acquire a JDK5 or higher and setup the JAVA_HOME environment variable
2) Acquire Apache Maven. Known working version: 3.0.4
3) Setup an environment variable, MAVEN_OPTS=-Xmx768m -XX:MaxPermSize=512m
4) Make sure the Maven/bin folder and the JDK/bin folders are in the current path
5) execute "mvn clean install"

That should build the key modules of the project and test most of the Java components. Depending on your computer's speed, it can take up to 15 minutes to build.

To enable additional output during the build and test project:
	mvn clean install -Ddebug=true

To build all of the project modules, including utilities and sample projects, run the majority of the integration tests and documentation (this is our CI build):
	mvn clean install -Pdist
	
	
To attach the debugger to the build process
mvn -Dmaven.surefire.debug clean install
It listens on port 5005 by default. More info on debugging maven projects is here http://maven.apache.org/surefire/maven-surefire-plugin/examples/debugging.html

To setup an Eclipse environment, simply import the maven project.

To setup a Netbeans environment, start netbeans and open the maven based project


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

 