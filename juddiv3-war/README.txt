To support our users with different platform configurations we have the following profiles:


1. Target platform Tomcat and Derby using OpenJPA and CXF.

Both OpenJPA and CXF are packaged up in the juddiv3.war.

mvn clean package -P openjpa


2. Target platform Tomcat and Derby using Hibernate and CXF

Both Hibernate and CXF are packaged up in the juddiv3.war.

mvn clean package -P hibernate


3. Target platform JBoss-6.x and HSQL using Hibernate and JBossWS-native

The juddiv3.war relies on Hibernate and JBossWS-native in the appserver.

mvn clean package -P hibernate-jbossws-native


4. Target platform JBoss-6.x and HSQL using Hibernate and JBossWS-cxf

The juddiv3.war relies on Hibernate and JBossWS-cxf in the appserver.

mvn clean package -P hibernate-jbossws-cxf


5. Target platform Tomcat and Derby using OpenJPA and Apache Axis2 

Both OpenJPA and Apache Axis2 are packaged up in the juddiv3.war.

mvn clean package -P hibernate-jbossws-cxf


KNOWN ISSUES

I. 15:14:37,275 SEVERE [RegistryServlet] jUDDI registry could not be started.
org.apache.commons.configuration.ConfigurationException: java.util.zip.ZipException: 
error in opening zip file: org.apache.commons.configuration.ConfigurationException: 
org.apache.commons.configuration.ConfigurationException: java.util.zip.ZipException: 
error in opening zip file

Workaround: deploy juddiv3.war as a directory (not a zip file). 


II. JBoss-5.x 
Note that configuration 3 and 4 will also run on JBoss-5.x, but you may run into the following:
ERROR [org.jboss.ws.metadata.wsdl.xmlschema.JBossXSErrorHandler] (main) 
[domain:http://www.w3.org/TR/xml-schema-1]::[key=src-resolve]::Message=src-resolve: 
Cannot resolve the name 'ns1:Signature' to a 'element declaration' component.

for which there is the following workaround:

    1. unzip the deployers/jbossws.deployer/jbossws-native-core.jar
    and add the xmldsig-core-schema.xsd in the schema directory,
    
    10293 Fri May 27 14:40:40 EDT 2011 schema/xmldsig-core-schema.xsd
    
    2. and edit the
    
    META-INF/jbossws-entities.properties
    
    by adding a line at the bottom saying:
    
    http\://www.w3.org/2000/09/xmldsig#=schema/xmldsig-core-schema.xsd

6. Target platform JBoss 7,8,EAP-6.x running OpenJPA and CXF

JBoss-7.x ships with the org.jboss.as.jpa.openjpa module. If you are running EAP-6.x you
will have to copy this module from a JBoss-AS7.x server. 

a) Your modules/system/layers/base/org/jboss/as/jpa/openjpa/main directory should look 
contain: jboss-as-jpa-openjpa-7.1.1.Final.jar, module.xml with the module.xml
<?xml version="1.0" encoding="UTF-8"?>

<!-- contains the JPA integration classes for OpenJPA 2.x --> 
<module xmlns="urn:jboss:module:1.1" name="org.jboss.as.jpa.openjpa">
    <properties>
        <property name="jboss.api" value="private"/>
    </properties>

    <resources>
        <resource-root path="jboss-as-jpa-openjpa-7.1.1.Final.jar"/>
        <!-- Insert resources here -->
    </resources>

    <dependencies>
        <module name="javax.annotation.api"/>
        <module name="javax.persistence.api"/>
        <module name="javax.transaction.api"/>

        <module name="org.jboss.as.jpa.spi"/>
        <module name="org.jboss.logging"/>
        <module name="org.jboss.jandex"/>

        <module name="org.apache.openjpa" optional="true"/>  <!-- org.apache.openjpa:main must be created manually with OpenJPA jars -->
    </dependencies>
</module>

b) Your
modules/system/layers/base/org/apache/openjpa/main directory should contain the following
files: module.xml, openjpa-2.2.1.jar, serp-1.13.1.jar, with the module.xml looking
like

<module xmlns="urn:jboss:module:1.1" name="org.apache.openjpa"> 
    <resources> 
        <resource-root path="openjpa-2.2.1.jar"/> 
        <resource-root path="serp-1.13.1.jar"/> 
    </resources> 
        <dependencies> 
            <module name="javax.persistence.api"/> 
            <module name="javax.transaction.api"/> 
            <module name="javax.validation.api"/> 
            <module name="org.apache.commons.lang"/> 
            <module name="org.apache.commons.collections"/> 
            <module name="org.apache.log4j"/> 
        </dependencies> 
</module>

Note that the openjpa and serp versions depend on the versions jUDDI is referencing,
which will like be upgraded in the future.

c) Now run 

mvn clean package -Popenjpa-jboss7up

and extract the juddiv3.war to standalone/deployments/juddiv3.war. Note that by
default jUDDI is configured to use the 'ExampleDS' H2 datasource, as configured in the
web.xml and persistence.xml. Update these files and add your database driver for your
database of chioce.








