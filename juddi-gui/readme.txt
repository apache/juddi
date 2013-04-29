The juddi-gui project is an extension to jUDDI that provides a user interface for UDDI v3 compliant registries.

Direction for building:

Pre-requists:
1) Ant 1.8+
2) Maven 3.0.x
3) Netbeans 7.x
4) Full source for jUDDI
5) Java JDK 1.6+

Build:
1) First, build the main jUDDI project. 
	mvn clean install
2) Build the juddi-gui-dsig project
	cd juddi-gui-disg
	ant jar
3) Copy the applet binaries to within the juddi-gui war
	copy dist\juddi-gui-dsig.jar ..\juddi-gui\web\applets
	mkdir ..\juddi-gui\web\applets\lib
	copy dist\lib\*.* ..\juddi-gui\web\applets\lib
3) Build the war
	cd juddi-gui
	ant dist
4) Deploy
	copy the war file into any J2EE container that has a soap stack that supports JAX-WS 2.2 or higher
5) Access
	http://localhost:8080/juddi-gui
6) Configure
	Alter the contents of juddi-gui/META-INF/config.properties