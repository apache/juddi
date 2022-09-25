## How to deploy jUDDI To?

The jUDDI distribution ships preconfigured on Tomcat - it runs out of the box. All you have to do in go into the `juddi-distro-<version>/juddi-tomcat-<version>/bin` directory and start up Tomcat. All of this just as described in  `<chapter-GettingStarted>`. 
	
By default the `juddiv3.war` is configured to use OpenJPA and CXF. If you want to change your JPA or WS provider, or you'd like to run on a different container then this chapter may come in handy, as there a number of scripted 'profiles' to change the configuration and dependencies in the `juddiv3.war`. To run these maven based scripts you need to go into `juddi-distro-<version>/juddiv3-war` directory. 

### Tomcat

#### OpenJPA and CXF

Target platform Tomcat and Derby using OpenJPA and CXF. Both OpenJPA and CXF are packaged up in the juddiv3.war.

````
mvn clean package -P openjpa
````

Then copy the `target/juddiv3.war` to the `<tomcat>/webapps` directory.
	
#### Hibernate and CXF

Target platform Tomcat and Derby using Hibernate and CXF. Both Hibernate and CXF are packaged up in the juddiv3.war.

````
mvn clean package -P hibernate
````

Then copy the `target/juddiv3.war` to the `<tomcat>/webapps` directory.
	
#### OpenJPA and Axis2

Target platform Tomcat and Derby using OpenJPA and Apache Axis2. Both Hibernate and Axis2 are packaged up in the juddiv3.war.

````
mvn clean package -P axis2
````

Then copy the `target/juddiv3.war` to the `<tomcat>/webapps` directory.
	
### JBoss

#### JBossAS 6.0.0.GA

This section describes how to deploy juddi to JBoss 6.0.0.GA.

First, download jboss-6.0.0.GA - the zip or tar.gz bundle may be found at http://www.jboss.org/jbossas/downloads/. Download the bundle and uncompress it.


##### Hibernate and JBossWS-Native

Target platform JBoss-6.x and HSQL using Hibernate and JBossWS-native. The juddiv3.war relies on Hibernate and JBossWS-native in the appserver.
````
mvn clean package -P hibernate-jbossws-native
````
Then copy the `target/juddiv3.war` to the `<jboss>/server/default/deploy` directory.

##### Hibernate and JBossWS-CXF

Target platform JBoss-6.x and HSQL using Hibernate and JBossWS-cxf. The juddiv3.war relies on Hibernate and JBossWS-cxf in the appserver.

````
mvn clean package -P hibernate-jbossws-cxf
````

##### Known Issues

Issue 1

````
15:14:37,275 SEVERE [RegistryServlet] jUDDI registry could not be
 started. org.apache.commons.configuration.ConfigurationException:
 java.util.zip.ZipException: error in opening zip file: 
 org.apache.commons.configuration.ConfigurationException: 
 org.apache.commons.configuration.ConfigurationException: 
 java.util.zip.ZipException: error in opening zip file
````

Workaround: deploy juddiv3.war as a directory (not a zip file).

Issue 2::

JBoss-5.x Note that configuration 3 and 4 will also run on JBoss-5.x, but you may run into the following

````
ERROR [org.jboss.ws.metadata.wsdl.xmlschema.JBossXSErrorHandler] 
(main) [domain:http://www.w3.org/TR/xml-schema-1]::
[key=src-resolve]::Message=src-resolve: Cannot resolve the 
name ns1:Signature to a element declaration component.
````

Workaround: Unzip the deployers/jbossws.deployer/jbossws-native-core.jar and add the xmldsig-core-schema.xsd in the schema directory,

````
10293 Fri May 27 14:40:40 EDT 2011 schema/xmldsig-core-schema.xsd
````

Edit the file META-INF/jbossws-entities.properties by adding a line at the bottom saying:

````
http\://www.w3.org/2000/09/xmldsig#=schema/xmldsig-core-schema.xsd
````

Copy juddiv3.war to server/default/deploy and unpack it.

Insert jboss-web.xml into the juddiv3.war/WEB-INF directory , should look like the following :

````
<?xml version="1.0" encoding="ISO-8859-1"?>

<!DOCTYPE jboss-web PUBLIC 
	"-//JBoss//DTD Web Application 2.3V2//EN"
    "http://www.jboss.org/j2ee/dtd/jboss-web_3_2.dtd">
    <jboss-web>
    <resource-ref>
        <res-ref-name>jdbc/JuddiDS</res-ref-name>
        <jndi-name>java:JuddiDS</jndi-name>
    </resource-ref>
    <depends>jboss.jdbc:datasource=JuddiDS,service=metadata
		</depends>

</jboss-web>
````
##### Change web.xml

Replace the WEB-INF/web.xml with the jbossws-native-web.xml within docs/examples/appserver.

##### Configure Datasource

The first step for configuring a datasource is to copy your JDBC driver into the classpath. Copy your JDBC driver into `${jboss.home.dir}/server/${configuration}/lib`, where configuration is the profile you wish to start with (default, all, etc.). Example :

````
cp mysql-connector-java-5.0.8-bin.jar \
	/opt/jboss-5.1.0.GA/server/default/lib
````

Next, configure a JBoss datasource file for your db. Listed below is an example datasource for MySQL :

````
<?xml version="1.0" encoding="UTF-8"?>
<datasources>
 <local-tx-datasource>
   <jndi-name>JuddiDS</jndi-name>
   <connection-url>jdbc:mysql://localhost:3306/juddiv3
	</connection-url>
   <driver-class>com.mysql.jdbc.Driver</driver-class>
   <user-name>root</user-name>
   <password></password>
   <exception-sorter-class-name>
   org.jboss.resource.adapter.jdbc.vendor.MySQLExceptionSorter
   </exception-sorter-class-name>

   <!-- corresponding type-mapping in the 
	standardjbosscmp-jdbc.xml (optional) -->
   <metadata>
      <type-mapping>mySQL</type-mapping>
   </metadata>
 </local-tx-datasource>
</datasources>
````

Next, make a few changes to the juddiv3.war/classes/META-INF/persistence.xml. Change the "hibernate.dialect" property to match the database you have chosen for persistence. For MySQL, change the value of hibernate.dialect to "org.hibernate.dialect.MySQLDialect". A full list of dialects available can be found in the hibernate documentation (https://www.hibernate.org/hib_docs/v3/api/org/hibernate/dialect/package-summary.html). Next, change the `<jta-data-source>` tags so that it reads `<non-jta-data-source>`, and change the value from java:comp/env/jdbc/JuddiDS to java:/JuddiDS.

#### JBossAS 7.x/JBossEAP-6.x

This section describes how to deploy juddi to JBossAS 7, WildFly and JBossEAP 6

##### Hibernate and JBossWS-CXF

Target platform Wildfly/EAP and H2 using Hibernate and JBossWS-cxf. The juddiv3.war relies on Hibernate and JBossWS-cxf modules in the appserver. To build the correct juddiv3.war run
````
mvn clean package -P jboss7up
````

Use the JBoss add-user.sh script to create an application user with the uddiadmin role.

### Deploying to Glassfish
This section describes how to deploy juddi to Glassfish 2.1.1. These instructions will use CXF as a webservice framework.

First, download the glassfish-v2.1.1 installer JAR. Once downloaded,install using the JAR and then run the ant setup script :
````
java -jar glassfish-installer-v2.1.1-b31g-linux.jar
cd glassfish
ant -f setup.xml
````

#### Glassfish jars
Copy the following JARs into domains/domain1/lib/ext. Note that for the purposes of this example, we have copied the MySQL driver to domains/domain1/lib/ext :
````
antlr-2.7.6.jar
cglib-nodep-2.1_3.jar
commons-collections-3.2.1.jar
commons-logging-1.1.jar
dom4j-1.6.1.jar
hibernate-3.2.5.ga.jar
hibernate-annotations-3.3.0.ga.jar
hibernate-commons-annotations-3.0.0.ga.jar
hibernate-entitymanager-3.3.1.ga.jar
hibernate-validator-3.0.0.ga.jar
javassist-3.3.ga.jar
jboss-common-core-2.0.4.GA.jar
jta-1.0.1B.jar
mysql-connector-java-5.0.8-bin.jar
persistence-api-1.0.jar
````

#### Configure the JUDDI datasource

First, using the asadmin administration tool, import the following file :

````
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE resources PUBLIC "-//Sun Microsystems Inc.//DTD 
Application Server 9.0 Domain//EN" "*<install directory>/lib/dtds/sun-resources_1_3.dtd*">
<resources>
<jdbc-connection-pool name="mysql-pool" 
datasource-classname="com.mysql.jdbc.jdbc2.optional.MysqlDataSource"
 res-type="javax.sql.DataSource">
<property name="user" value="juddi"/>
<property name="password" value="juddi"/>
<property name="url" value="jdbc:mysql://localhost:3306/juddiv3"/>
</jdbc-connection-pool>
<jdbc-resource enabled="true" jndi-name="jdbc/mysql-resource" 
object-type="user" pool-name="mysql-pool"/>
</resources>
````

````
asadmin add-resources resource.xml
````

Then use the Glassfish administration console to create a "jdbc/juddiDB" JDBC datasource resource based on the mysql-pool Connection Pool.

#### Add juddiv3-cxf.war

Unzip the juddiv3-cxf WAR into domains/domain1/autodeploy/juddiv3.war .

Add a sun-web.xml file into juddiv3.war/WEB-INF. Make sure that the JNDI references matches the JNDI location you configured in the Glassfish administration console.

````
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE sun-web-app PUBLIC '-//Sun Microsystems, Inc.//DTD 
Application Server 9.0 Servlet 2.5//EN' 
'http://www.sun.com/software/appserver/dtds/sun-web-app_2_5-0.dtd'>
<sun-web-app>
<resource-ref>
<res-ref-name>jdbc/juddiDB</res-ref-name>
<jndi-name>jdbc/juddiDB</jndi-name>
</resource-ref>
</sun-web-app>
````

Next, make a few changes to juddiv3.war/WEB-INF/classes/META-INF/persistence.xml . Change the "hibernate.dialect" property to match the database that you have chosen for persistence. For MySQL, change the value of hibernate.dialect to "org.hibernate.dialect.MySQLDialect". A full list of dialects available can be found in the hibernate documentation (https://www.hibernate.org/hib_docs/v3/api/org/hibernate/dialect/package-summary.html). Next, change the `<jta-data-source>` change the value from java:comp/env/jdbc/JuddiDS to java:comp/env/jdbc/JuddiDB.

#### Run jUDDI

Start up the server :

````
cd bin
asadmin start-domain domain1
````

Once the server is deployed, browse to http://localhost:8080/juddiv3