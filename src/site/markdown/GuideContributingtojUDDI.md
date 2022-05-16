## Contributing to jUDDI

We welcome contributions to jUDDI. Visit the jUDDI web set at http://juddi.apache.org for more information.

### License guidance

Apache jUDDI is released under the Apache Software Foundation v2.0 License. Details on the license is located at the following link: http://apache.org/licenses/LICENSE-2.0.

If you wish to bring in 3rd libraries, please keep in mind that certain libraries cannot be used due to license restrictions. See http://www.apache.org/legal/3party.html for details.

### SVN access

Source code is accessible at the following link: https://svn.apache.org/viewvc/juddi/trunk/.

### Project structure

jUDDI, from a developer's perspective, is divided into a number of smaller, more manageable modules. In general, each module contains all of the necessary unit tests in order to ensure functionality.

### Building and testing jUDDI

jUDDI has a number of components, however it is mostly Java based. The following sections describe the particulars for each language.
 
#### All Java Components

Procedure

1. Acquire a Subversion client.
2. Execute svn co https://svn.apache.org/viewvc/juddi/trunk/
3. Acquire a JDK5 or higher and setup the JAVA_HOME environment variable.
4. Acquire Apache Maven. Known working version: 3.0.4
5. Setup an environment variable, MAVEN_OPTS=-Xmx768m -XX:MaxPermSize=512m
6. Make sure the Maven/bin folder and the JDK/bin folders are in the current path
7. Execute "mvn clean install"

This will build, test and package all of the Java components of jUDDI. This includes the Technical Conformance Kit (TCK), a live Tomcat server, the user interfaces, and more.

For additional build output, add -Ddebug-true for Java.

To prepare a deployable jUDDI war for an alternate deployment scenario (other than Tomcat with CXF and OpenJPA), use the following procedure:

Execute `mvn clean package -P<packageName>`

Where `<packageName>`is one of the following 

- openjpa-jboss7up for EAP 6 and up, GA 7 and up
- hibernate-jbossws-native for EAP 5, Jboss GA 6 and down with the JbossWS Native soap stack
- hibernate-jbossws-cxf for EAP 5, Jboss GA 6 and down with the JbossWS Native soap stack
- hibernate (includes CXF in the war, used for Tomcat)
- openjpa (includes CXF in the war, used for Tomcat)
- axis2 (includes Axis2 in the war)

TIP: When altering the TCK based modules, make sure you clean install in the root check out location. Due to the build order, you may end up with strange results when just executing the tests, even with clean install.

TIP: To attach the debugger to the build process try "mvn -Dmaven.surefire.debug clean install". It listens on port 5005 by default. More info on debugging maven projects is here http://maven.apache.org/surefire/maven-surefire-plugin/examples/debugging.html


#### .NET

jUDDI also has a .NET based jUDDI Client. To build this, only the .NET Framework needs to be installed, version 3.5 or higher. A Visual Studio solution file is included, but it is not required for building.

Procedure - Windows
* Add MSBuild.exe to your system path. It's usually in %SYSTEMROOT%\Microsoft.NET\Framework(64)\v4.x.x. If you haven't installed .NET 4 yet, replace v4.x.x with v2.x.x
* Build the solution. This will build the juddi-client.net.dll, the same application(s) and the test project(s).

````
MSBuild.exe juddi-client.net.sln /p:Configuration=Debug /p:Platform="Any CPU"
````

For additional debug output set the environment variable 'debug=true'

````
set debug=true
````

Procedure - *nix using Mono

TIP: Support on Mono is very experimental. There are still many APIs that have no yet been implements on Mono that may cause compilation failure.

To build the .NET assemblies on a Linux or Unix based computer:
* Install Mono (apt-get install mono-complete mono-develop
* Build it

````
cd juddi-client.net
xbuild judddi-client.net-mono.sln
cd juddi-client.net-sample/bin/Debug/
mono juddi-client.net-sample.exe
````

### Other ways to Contribute to jUDDI 

There are many ways you can contribute to jUDDI. We welcome all kinds and types contributions.

#### Bug Reports 

Bug reports and feature requests are low effort tasks that do not require a high level of technical proficiency.

#### Internationalization

The jUDDI GUI user interface is designed to be multi-lingual. For the 3.2 release, English and Spanish are provided for the user interface. The jUDDI server administration user interface is also available in English and Spanish.

#### Contributing Source code

When contributing source code, you must own the code and be will to donate the code to the Apache Software Foundation. For those without SVN access, the process is as follows:

1. Open a JIRA on the jUDDI Issue Tracker
2. Write your code and test it (mvn clean install)
3. Use Subversion to create a patch (svn patch)
4. Upload the patch as an attachment for the JIRA

Once accepted, your code will be added to the baseline. Code submissions may be modified for style, content, documentation and any other reason that we see fit.

##### Coding Standards

The majority of jUDDI's source code is formatted using 8 space tabs and using Javadoc style documentation. In general, test cases are often more useful and more valuable that the code being tested.

#### Releases

For the latest information on jUDDI's release process, visit http://juddi.apache.org/committers.html

### What the?

Having ran into a number of strange issues when developing with jUDDI, we decided to write a few of them down.

Q:I added a new class to juddi-core but it doesn't end up in the packaged tomcat instance? 

A: Modify the pom and make sure the package name is added to juddi-core-openjpa

Q: Some unit tests fail, but only under windows. 
A: This is specifically for the SubscriptionListerner Tests and most likely has something to do with ports getting locked up by the Java process.