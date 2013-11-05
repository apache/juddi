The juddi-gui project is an extension to jUDDI that provides a user interface for UDDI v3 compliant registries.

Direction for building:

Pre-requists:
1) Maven 3.0.x
2) Full source for jUDDI
3) Oracle Java JDK 1.6+ (needed for the digital signature applet)

Build:
1) First, build the main jUDDI project. 
	mvn clean install
2) Deploy
	copy the war file into any J2EE container that has a soap stack that supports JAX-WS 2.2 or higher
	and the servlet 2.4 API spec or higher
3) Access
	http://localhost:8080/juddi-gui
4) Configure
	Alter the contents of juddi-gui/META-INF/config.properties and juddi-gui/WEB-INF/classes/META-INF/uddi.xml as necessary