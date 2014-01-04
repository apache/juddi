juddi-client.net

This project is a complete port of the Java juddi-client project. 

Prerequists
-.NET 3.5
-Nunit 2.6.1 or better

Build Instructions
msbuild.exe juddi-client.net\juddi-client.net.sln /p:Configuration=Debug /p:Platform="Any CPU" /m  

Tests
"%NUNIT_HOME%\bin\nunit-console.exe" juddi-client.net\juddi-client.net.test\bin\Debug\juddi-client.net.test.dll

Integration Tests
Start Tomcat or a container with jUDDIv3.war deployed (and working!)
"%NUNIT_HOME%\bin\nunit-console.exe" juddi-client.net-integration.test\bin\Debug\juddi-client.net-integration.test.dll


Some quick notes on usage.

This assembly is not signed (mostly to make portability easier). You'll probably need to sign it at somepoint. Consult Microsoft documentation on how to do this.

Start your own .NET project
Add a reference to juddi-client.net.dll
Add a copy of juddi-client.net-sample\uddi.xml (set it to copy to output directory always)
Follow the patterns in the example projects and consult the documentation
