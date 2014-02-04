This project products a browser applet that can be used to digitally UDDI entities. 

In a production environment, you'll need to digitally sign the applet's jar file with your own certificate.

In order to build this from scratch, you'll either need an Oracle JDK, or OpenJDK with IcedTea installed.

There are two ways you can go about signing your copy of the digital signature applet
1) Alter the pom.xml file and point to your keystore and passwords
2) Leave the pom.xml as is, then build, then unsign the jar[1], the sign it with your key store[2]

To build
>mvn clean install


[1] http://stackoverflow.com/questions/7757083/how-do-i-unsign-a-jar
[2] http://docs.oracle.com/javase/tutorial/deployment/jar/signing.html

Once you have a built and signed jar file, verify the signature using this command
>jarsigner -verify target/juddi-gui-dsig-{versuin}-SNAPSHOT-jar-with-dependencies.jar


If you have confirmed that the file verifies, then move on and deploy it by copying it into the juddi-gui war file.
> cp target/juddi-gui-dsig-{versuin}-SNAPSHOT-jar-with-dependencies.jar juddi-gui.war/applets/juddi-gui-dsig-all.jar

Note: If you're building all jUDDI using maven, this is done for you.

