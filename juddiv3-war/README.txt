To support our users with different platform configurations we have the following profiles:


1. Target platform Tomcat using OpenJPA and CXF.

Both OpenJPA and CXF are packaged up in the juddiv3.war.

mvn clean package -P openjpa


2. Target platform Tomcat using Hibernate and CXF

Both Hibernate and CXF are packaged up in the juddiv3.war.

mvn clean package -P hibernate


3. Target platform JBoss-6.x using Hibernate and JBossWS-native

The juddiv3.war relies on Hibernate and JBossWS-native in the appserver.

mvn clean package -P hibernate-jbossws-native


4. Target platform JBoss-6.x using Hibernate and JBossWS-cxf

The juddiv3.war relies on Hibernate and JBossWS-cxf in the appserver.

mvn clean package -P hibernate-jbossws-cxf


JBoss-5.x KNOWN ISSUE
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

