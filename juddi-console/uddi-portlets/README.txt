This project was created using

mvn archetype:create -DarchetypeGroupId=com.totsp.gwt \
    -DarchetypeArtifactId=maven-googlewebtoolkit2-archetype \
    -DarchetypeVersion=1.0.3 \
    -DremoteRepositories=http://gwt-maven.googlecode.com/svn/trunk/mavenrepo \
    -DgroupId=org.apache.juddi \
    -DartifactId=uddi-portlets

To bring up the gwt runShell use:
mvn clean com.totsp.gwt:maven-googlewebtoolkit2-plugin:2.0-beta26:gwt

To build the uddi-portlet.war use
mvn package



juddi-tomcat should bundle up pluto and the portlets.




