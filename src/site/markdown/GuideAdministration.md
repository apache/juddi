## Administration

### Changing the Web Server Listen Port

If you want to change the port Tomcat listens on to something non-standard (something other than 8080), use the following guidance.

jUDDI Server (Tomcat) - This assumes you are using the jUDDI server bundled with Apache Tomcat. For other application servers, consult their documentation, however the juddiv3.xml must still be altered. 

- Edit `conf/server.xml` and change the port within the `<Connector>` element.
- Edit `webapps/juddiv3/WEB-INF/classes/juddiv3.xml` and change the port number jUDDI Server Baseurl.
- Edit `webapps/juddiv3/WEB-INF/config.properties` and change the port numbers for "securityurl" and "juddipapi".
- Edit `webapps/juddi-gui/META-INF/config.properties` and change the port numbers for all of the URLs listed.

If these changes are made before jUDDI has been started for the first time, then no further action is required. If jUDDI has been previously started, you'll need to either A) update the URL information for the Node's root business entity URLs or B) turn on "Seed Always" in the juddiv3.xml file to auto update the changes.

### Administering Users and Access Control

As of version 3.2, jUDDI Authentication is handled from two perspectives, administrator and end user access.

#### Administrative Users

Administrative users have special access to juddi-gui's remote configuration page at http://localhost:8080/juddi-gui/settings.jsp and to the Administrative Console at http://localhost:8080/juddiv3/admin. Access to both of these is configured at the container level (i.e. Jboss, Tomcat, etc). By default, users that need to access these pages need to have the "uddiadmin" role (which is defined in the WEB-INF/web.xml of both web application archives). When you are running on tomcat this configuration can be found in the `<tomcat>/conf/tomcat-users.conf` file.

#### End Users

End users typically will either access jUDDI's services directly at http://localhost:8080/juddiv3/ or via the user interfaces http://localhost:8080/juddi-gui. In both cases, authentication is handled via jUDDI's Authentication providers which is configured in `juddiv3.war/WEB-INF/classes/juddiv3.xml`.

##### Under the Hood

In order to enforce proper write access to jUDDI, each request to jUDDI needs a valid authToken. Note that read access is not restricted (by default, but can be enabled) and therefore queries into the registries are not restricted.

To obtain a valid authToken a getAuthToken() request must be made, where a GetAuthToken object is passed. On the GetAuthToken object a userid and credential (password) needs to be set.

````
org.uddi.api_v3.GetAuthToken ga = new org.uddi.api_v3.GetAuthToken();
ga.setUserID("username");
ga.setCred("password");
org.uddi.api_v3.AuthToken token = securityService.getAuthToken(ga);
````

The property `juddi/auth/*` in the `juddiv3.xml` configuration file can be used to configure how jUDDI is going to check the credentials passed in on the GetAuthToken request. By default jUDDI uses the JUDDIAuthenticator implementation. You can provide your own authentication implementation or use any of the ones mention below. The implementation needs to implement the org.apache.juddi.auth.Authenticator interface, and `juddi/auth/authenticator/class` property should refer to the implementation class.

There are two phases involved in Authentication. The authenticate phase and the identify phase. Both of these phases are represented by a method in the Authenticator interface.

The authenticate phase occurs during the GetAuthToken request as described above. The goal of this phase is to turn a user id and credentials into a valid publisher id. The publisher id (referred to as the "authorized name" in UDDI terminology) is the value that assigns ownership within UDDI. Whenever a new entity is created, it must be tagged with ownership by the authorized name of the publisher. The value of the publisher id can be completely transparent to jUDDI - the only requirement is that one exists to assign to new entities. Thus, the authenticate phase must return a non-null publisher id. Upon completion of the GetAuthToken request, an authentication token is issued to the caller.

In subsequent calls to the UDDI API that require authentication, the token issued from the GetAuthToken request must be provided. This leads to the next phase of jUDDI authentication - the identify phase.

The identify phase is responsible for turning the authentication token (or the publisher id associated with that authentication token) into a valid UddiEntityPublisher object. The UddiEntityPublisher object contains all the properties necessary to handle ownership of UDDI entities. Thus, the token (or publisher id) is used to "identify" the publisher.

The two phases provide compliance with the UDDI authentication structure and grant flexibility for users that wish to provide their own authentication mechanism. Handling of credentials and publisher properties can be done entirely outside of jUDDI. However, jUDDI provides the Publisher entity, which is a sub-class of UddiEntityPublisher, to persist publisher properties within jUDDI. This is used in the default authentication and is the subject of the next section.

##### Choosing a Cryptographic Provider

jUDDI provides a number of cryptographic providers. Some of them may not be available in your region of the world due to export restrictions. All of these providers are provides that are included with the Oracle Java Runtime Environment. 

###### jUDDI's Cryptographic Providers

TIP: The AES256Cryptor requires the Sun Java unlimited strength Crypograhpic Extensions to be installed. OpenJDK users are not affected by this.

In the following section, Authentication, a Cryptographic Provider must be selected using the following property in juddiv3.xml:
````
juddi/cryptor
````

###### jUDDI Server Providers

* org.apache.juddi.cryptor.DefaultCryptor - Password Based Encryption With MD5 and DES
* org.apache.juddi.cryptor.TripleDESCrytor - Triple DES 168 bit
* org.apache.juddi.cryptor.AES128Cryptor - Advanced Encryption Standard 128 bit
* org.apache.juddi.cryptor.AES256Cryptor - Advanced Encryption Standard 256 bit

##### jUDDI Client Providers (Java and .NET)
* org.apache.juddi.v3.client.crypto.DefaultCryptor - Password Based Encryption With MD5 and DES
* org.apache.juddi.v3.client.crypto.TripleDESCrytor - Triple DES 168 bit
* org.apache.juddi.v3.client.crypto.AES128Cryptor - Advanced Encryption Standard 128 bit
* org.apache.juddi.v3.client.crypto.AES256Cryptor - Advanced Encryption Standard 256 bit


###### Encrypting a Password

To encrypt a password, the jUDDI Tomcat server comes with a basic Windows Batch file and a Unix Bash script which will fire off the correct Java command. It is located at the following path:

````
{tomcat_home}/bin/juddi-cryptor.bat/sh
````

TIP: The jUDDI-Client (Java only) uses the same encryption keys and the jUDDI Server, therefore encrypted passwords using this tool will work with the jUDDI-client's configuration file.

In addition, an MD5 hashing program is included to assist with setting users passwords for the MD5XMLDocAuthenticator.
````
{tomcat_home}/bin/juddi-md5.bat/sh
````

TIP: You can generate new encryption keys using this utility by specifying the System Property -Dgenerate=true option. You can then use them using the System Property -Djuddi.encryptionKeyFile.TripleDESCrytor=path/to/key

##### jUDDI Authentication

The default authentication mechanism provided by jUDDI is the JUDDIAuthenticator. The authenticate phase of the JUDDIAuthenticator simply checks to see if the user id passed in has an associated record in the Publisher table. No credentials checks are made. If, during authentication, the publisher does not exist, it the publisher is added on the fly.

WARNING: Do not use jUDDI Default Authenticator in production. It does not compare passwords to anything! 

The identify phase uses the publisher id to retrieve the Publisher record and return it. All necessary publisher properties are populated as Publisher inherits from UddiEntityPublisher.
````
juddi/auth/authenticator/class = org.apache.juddi.auth.JUDDIAuthentication
````

##### XMLDocAuthentication

The XMLDocAuthentication implementation needs a XML file on the classpath. The juddiv3.xml file would need to look like

````
juddi/auth/authenticator/class = org.apache.juddi.auth.XMLDocAuthentication
juddi/auth/usersfile = juddi-users.xml
````

where the name of the XML can be provided but it defaults to juddi-users.xml, and the content of the file would looks something like

````
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<juddi-users>
    <user userid="anou_mana" password="password" />
    <user userid="bozo" password="clown" />
    <user userid="sviens" password="password" />
</juddi-users>
````

The authenticate phase checks that the user id and password match a value in the XML file. The identify phase simply uses the user id to populate a new UddiEntityPublisher.

##### CryptedXMLDocAuthentication

The CryptedXMLDocAuthentication implementation is similar to the XMLDocAuthentication implementation, but the passwords are encrypted.

````
juddi/auth/authenticator/class = org.apache.juddi.auth.CryptedXMLDocAuthentication
juddi/auth/usersfile = juddi-users-encrypted.xml
juddi/cryptor = org.apache.juddi.cryptor.DefaultCryptor
````

where the name user credential file is juddi-users-encrypted.xml, and the content of the file would looks something like

````
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<juddi-users>
    <user userid="anou_mana" password="+j/kXkZJftwTFTBH6Cf6IQ=="/>
    <user userid="bozo" password="Na2Ait+2aW0="/>
    <user userid="sviens" password="+j/kXkZJftwTFTBH6Cf6IQ=="/>
</juddi-users>
````
The DefaultCryptor implementation uses BEWithMD5AndDES and Base64 to encrypt the passwords. Note that the code in the AuthenticatorTest can be used to learn more about how to use this Authenticator implementation. You can plugin your own encryption algorithm by implementing the org.apache.juddi.cryptor.Cryptor interface and referencing your implementation class in the juddi.cryptor property.
The authenticate phase checks that the user id and password match a value in the XML file. The identify phase simply uses the user id to populate a new UddiEntityPublisher.

##### MD5XMLDocAuthenticator

The MD5XMLDocAuthenticator implementation is similar to the XMLDocAuthentication implementation, but the passwords are hashed using MD5.

````
juddi/auth/authenticator/class = org.apache.juddi.auth.MD5XMLDocAuthenticator
juddi/auth/usersfile = juddi-users-hashed.xml
juddi/cryptor = org.apache.juddi.cryptor.DefaultCryptor
````

where the name user credential file is juddi-users-encrypted.xml, and the content of the file would looks something like

````
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<juddi-users>
    <user userid="anou_mana" password="+j/kXkZJftwTFTBH6Cf6IQ=="/>
    <user userid="bozo" password="Na2Ait+2aW0="/>
    <user userid="sviens" password="+j/kXkZJftwTFTBH6Cf6IQ=="/>
</juddi-users>
````

The DefaultCryptor implementation uses BEWithMD5AndDES and Base64 to encrypt the passwords. Note that the code in the AuthenticatorTest can be used to learn more about how to use this Authenticator implementation. You can plugin your own encryption algorithm by implementing the org.apache.juddi.cryptor.Cryptor interface and referencing your implementation class in the juddi.cryptor property.
The authenticate phase checks that the user id and password match a value in the XML file. The identify phase simply uses the user id to populate a new UddiEntityPublisher.


##### LDAP Authentication

LdapSimpleAuthenticator provides a way of authenticating users using LDAP simple authentication. It is fairly rudimentary and more LDAP integration is planned in the future, but this class allows you to authenticate a user based on an LDAP prinicipal, provided that the principal (usually the distinguished name) and the juddi publisher ID are the same. 

To use this class you must add the following properties to the juddi3v.xml file:

````
juddi/auth/authenticator/class=org.apache.juddi.auth.LdapSimpleAuthenticator
juddi/auth/authenticator/url=ldap://localhost:389
juddi/auth/authenticator/style=simple
````

The juddi/authenticator/url property configures the LdapSimpleAuthenticator class so that it knows where the LDAP server resides. Future work is planned in this area to use the LDAP uid rather than the LDAP principal as the default publisher id.

LdapExpandedAuthenticator provides a slightly more flexible way to authenticate users via LDAP.

````
juddi/auth/authenticator/class=org.apache.juddi.v3.auth.LdapSimpleAuthenticator
juddi/auth/authenticator/url=ldap://localhost:389
juddi/auth/authenticator/style=simple
juddi/auth/authenticator/ldapexp=CN=%s, OU=Users,DC=Domain, etc
````

##### JBoss Authentication

Is it possible to hook up to third party credential stores. If for example jUDDI is deployed to the JBoss Application server it is possible to hook up to it's authentication machinery. The JBossAuthenticator class is provided in the docs/examples/auth directory. This class enables jUDDI deployments on JBoss use a server security domain to authenticate users.

TIP: The JBoss authentication is not distributed with jUDDI. It can be found here: http://svn.apache.org/viewvc/juddi/extras/jbossauthenticator/src/org/apache/juddi/auth/JBossAuthenticator.java?view=markup

To use this class you must add the following properties to the juddiv3.xml file:

````
juddi/auth/authenticator/class=org.apache.juddi.auth.JBossAuthenticator
juddi/auth/securityDomain=java:/jaas/other
````

The juddi/auth/authenticator/class property plugs the JbossAuthenticator class into the jUDDI the Authenticator framework. The juddi/sercuityDomain, configures the JBossAuthenticator class where it can lookup the application server's security domain, which it will use to perform the authentication. Note that JBoss creates one security domain for each application policy element on the `$JBOSS_HOME/server/default/conf/login-config.xml` file, which gets bound to the server JNDI tree with name java:/jaas/<application-policy-name></application-policy-name>. If a lookup refers to a non existent application policy it defaults to a policy named other.

##### Container Based Authentication

Certain security configurations may use HTTP based authentication. In this scenario, jUDDI simply trust's that the container will authenticate the user via some mechanism and uses that username for interactions with jUDDI.  To configure this setup, use the following configuration settings in juddiv3.xml:

````
juddi/auth/authenticator/class=org.apache.juddi.auth.HTTPContainerAuthenticator
juddi/auth/authenticator@useAuthToken=false
````

In addition, you'll have to make whatever changes necessary to the juddiv3.war/WEB-INF/web.xml file in order to use the chosen authentication mechanism. See your appliation server's documentation for details on this.


##### Authentication by Proxy (HTTP Header)

Certain security configurations that enforce authentication before requests come to the web application, such as via Apache HTTPD or a reverse SSL proxy. In these cases, the proxy provided authenticates the user, then passes along the user's identity via a HTTP header. To configure this setup, use the following configuration settings in juddiv3.xml:

````
juddi/auth/authenticator/class=org.apache.juddi.auth.HTTPHeaderAuthenticator
juddi/auth/authenticator/header=(Some HTTP Header)
juddi/auth/authenticator@useAuthToken=false
````

### Configuration Database Connections

#### Derby Out-of-the-Box

By default jUDDI uses an embedded Derby database. This allows us to build a  downloadable distribution that works out-of-the-box, without having to do any database setup work. We recommend switching to an enterprise-level database  before going to production. JUDDI uses the Java Persistence API (JPA) in the back  end and we've tested with both OpenJPA and Hibernate. To configure which JPA  provider you want to use, you will need to edit the configuration in the _juddiv3.war/WEB-INF/classes/META-INF/persistence.xml_. The content of this file is pretty standard between JPA implementations, however there can be slight differences. 
To make it easy we created different versions for different JPA implementations and target platforms. All JPA implementation have an enhancement phase, where the persistence 'model' classes are enhanced. Hibernate does this at runtime, OpenJPA prefers doing this at compile time. This is the reason we ship two versions of _juddi-core_, where the _juddi-core-openjpa.jar_ contains classes (byte-code) enhanced by OpenJPA. This is the reason this jar is larger then the _juddi-core.jar_. 

For Hibernate, for testing the content of this file looks like

````
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://java.sun.com/xml/ns/persistence" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/persistence 
    http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd" 
    version="1.0">
    <persistence-unit name="juddiDatabase" transaction-type="RESOURCE_LOCAL">
        <provider>org.hibernate.ejb.HibernatePersistence</provider>
        <jta-data-source>java:comp/env/jdbc/JuddiDS</jta-data-source>
        <!-- entity classes -->
        <class>org.apache.juddi.model.Address</class>
        <class>org.apache.juddi.model.AddressLine</class>
        ...
        <class>org.apache.juddi.model.UddiEntity</class>
        <class>org.apache.juddi.model.UddiEntityPublisher</class>

        <properties>
            <property name="hibernate.archive.autodetection" value="class"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
            <property name="hibernate.show_sql" value="false"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.DerbyDialect"/>
        </properties>
    </persistence-unit>
</persistence>
````

For OpenJPA the persistence.xml looks like

````
<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="http://java.sun.com/xml/ns/persistence" 
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd" 
             version="1.0">
  <persistence-unit name="juddiDatabase" transaction-type="RESOURCE_LOCAL">
    <provider>org.apache.openjpa.persistence.PersistenceProviderImpl</provider>
    <non-jta-data-source>java:comp/env/jdbc/JuddiDS</non-jta-data-source>
    <!-- entity classes -->
    <class>org.apache.juddi.model.Address</class>
    <class>org.apache.juddi.model.AddressLine</class>
   ...
    <class>org.apache.juddi.model.UddiEntity</class>
    <class>org.apache.juddi.model.UddiEntityPublisher</class>
    <properties>
      <property name="openjpa.jdbc.SynchronizeMappings" value="buildSchema(SchemaAction='add')"/>
      <property name="openjpa.Log" value="DefaultLevel=WARN, Tool=INFO"/>
      <property name="openjpa.jdbc.UpdateManager" value="operation-order"/>
      <property name="openjpa.jdbc.DBDictionary" value="derby"/>
      <!-- dialects: derby, postgres, mysql, oracle, sybase, sqlserver 
           for a complete list check the OpenJPA documentation -->
      <property name="openjpa.RuntimeUnenhancedClasses" value="warn"/>
      <property name="openjpa.Compatibility" value="CheckDatabaseForCascadePersistToDetachedEntity=true"/>
    </properties>
  </persistence-unit>
</persistence>
````

In this case we reference a _jta-data-source_ called _java:comp/env/jdbc/JuddiDS_. Datasource deployment is Application Server specific. If you are using Tomcat, then the datasource is defined in _juddi/META-INF/context.xml_ which by default looks like 

````
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <WatchedResource>WEB-INF/web.xml</WatchedResource>
    <Resource name="jdbc/JuddiDS" auth="Container"
        type="javax.sql.DataSource" username="" password=""
        driverClassName="org.apache.derby.jdbc.EmbeddedDriver" 
        url="jdbc:derby:juddi-derby-test-db;create=true"
        maxActive="8" 
        />  
</Context>
````

By default the juddiv3.war is configured to be used on Tomcat using OpenJPA. However the download bundle lets you specify different target platforms resulting in a different setup. In all cases it will point to the embedded Derby database.

#### Switching to another Database

We recommend switching to an enterprise-level database before going to production. Most JPA providers support a large number of Databases and switching to another database is achieved by updating the configuration settings in both the persistence.xml and datasource files. The recipe is:

* change the database dialect in the persistence.xml.
* change the database connection information in the datasource.
* add the database specific driver to your classpath.
* in some cases (Oracle is one such case) you will need to use sequences for the ID generation, in this case you will need an _orm.xml_ file. We ship a _orm.xml.example_ along side the _persistence.xml_. Rename this file and update this to your liking.

Some examples for specific databases are given below.

WARNING: Tomcat copies the _context.xml_ to `<tomcat>/conf/CATALINA/localhost/juddiv3.xml`, and if you update the _context.xml_ it may not update this copy. You should simply delete the _juddiv3.xml_ file after updating the _context.xml_. 

#### Switch to MySQL on Tomcat using OpenJPA

Check if you have are using Hibernate of OpenJPA, by looking at the jars in the _juddiv3.war/WEB-INF/lib_.
Edit the dialect in the _persistence.xml_ 
For OpenJPA:

````
<property name="openjpa.jdbc.DBDictionary" value="mysql"/>
````

Next edit the datasource. For tomcat you need to update the _juddiv3/META-INF/context.xml_ which should look something like

````
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <WatchedResource>WEB-INF/web.xml</WatchedResource>
    <Resource name="jdbc/JuddiDS" auth="Container"
        type="javax.sql.DataSource" username="root" password=""
        driverClassName="com.mysql.jdbc.Driver" 
        url="jdbc:mysql://localhost:3306/juddiv3"
        maxActive="8"/>  
</Context>
````

Finally you need to add the MySQL mysql driver (i.e. The _mysql-connector-java-5.1.6.jar_) to the classpath. Note that this jar may already by in the tomcat/lib directory, in which case you can move on to the step and create the mysql juddiv3 database. To create a MySQL database name +juddiv3+ use 

````
mysql> create database juddiv3
````
and finally you probably want to switch to a user which is a bit less potent then _root_, and delete the `<tomcat>/conf/CATALINA/localhost/juddiv3.xml` file.

#### Switch to Postgres on Tomcat using OpenJPA

Check if you have are using Hibernate of OpenJPA, by looking at the jars in the _juddiv3.war/WEB-INF/lib_.
Edit the dialect in the _persistence.xml_ 
For OpenJPA:

````
<property name="openjpa.jdbc.DBDictionary" value="postgres"/>
````

Next edit the datasource. For tomcat you need to update the _juddiv3/META-INF/context.xml_ which should look something like

````
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <WatchedResource>WEB-INF/web.xml</WatchedResource>
    <Resource name="jdbc/JuddiDS" auth="Container"
            type="javax.sql.DataSource" username="juddi" password="juddi"
            driverClassName="org.postgresql.Driver" 
            url="jdbc:postgresql://localhost:5432/juddi"
            maxActive="8"/>
</Context>
````

To create a MySQL database name _juddi_ use 

````
postgres= CREATE USER juddi with PASSWORD 'password';
postgres= CREATE DATABASE juddi;
postgres= GRANT ALL PRIVILEGES ON DATABASE juddi to juddi;
````
Be sure to have _postgresql-8.3-604.jdbc4.jar_ to the classpath. Note that this jar may already by in the tomcat/lib directory, in which case the final step is to delete the `<tomcat>/conf/CATALINA/localhost/juddiv3.xml` file.

#### Switch to Postgres on JBoss using Hibernate

This was written from a JBoss - jUDDI perspective. Non-JBoss-users may have to tweak this a little bit, but for the most part, the files and information needed is here. Logged in as postgres user, access psql: 

````
postgres= CREATE USER juddi with PASSWORD 'password';
postgres= CREATE DATABASE juddi;
postgres= GRANT ALL PRIVILEGES ON DATABASE juddi to juddi;
````

Note, for this example, my database is called juddi, as is the user who has full privileges to the database. The user 'juddi' has a password set to 'password'. Next edit the juddi-ds.xml datasource file with the settings for the postgres connection info:

````
<datasources>
    <local-tx-datasource>
        <jndi-name>JuddiDS</jndi-name>
        <connection-url>jdbc:postgresql://localhost:5432/juddi</connection-url>
        <driver-class>org.postgresql.Driver</driver-class>
        <user-name>juddi</user-name>
        <password>password</password>
        <!-- sql to call when connection is created.  Can be anything, 
        select 1 is valid for PostgreSQL 
        <new-connection-sql>select 1</new-connection-sql>
        -->
        <!-- sql to call on an existing pooled connection when it is obtained 
        from pool.  Can be anything, select 1 is valid for PostgreSQL
        <check-valid-connection-sql>select 1</check-valid-connection-sql>
        -->
        <!-- corresponding type-mapping in the standardjbosscmp-jdbc.xml -->
        <metadata>
            <type-mapping>PostgreSQL 8.0</type-mapping>
        </metadata>
    </local-tx-datasource>
</datasources>
````

In _persistence.xml_, reference the correct JNDI name of the datasource and remove the derby Dialect and add in the postgresql Dialect, for Hibernate on JBoss use:

````
<jta-data-source>java:comp/env/jdbc/JuddiDS</jta-data-source>
...

<property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
````

Be sure to have _postgresql-8.3-604.jdbc4.jar_ in the _lib_ folder.

#### Switch to Oracle on Tomcat using Hibernate

To switch over to Oracle you need to add the oracle driver (i.e. the_classes12.jar_) to the  classpath and you will need to edit the _persistence.xml_ 

````
<property name="hibernate.dialect" value="org.hibernate.dialect.Oracle10gDialect"/>
````

To create a Oracle database name juddiv3 with the ultimate in minimalism use 

````
sqlplus> create database juddiv3;
````

then you probably want to switch to a user which is a bit less potent then 'root' and set the appropriate password, and delete the `<tomcat>/conf/CATALINA/localhost/juddiv3.xml`

##### Changing the Oracle Sequence name

If you are using Hibernate as a persistence layer for jUDDI, then Oracle will generate a default sequence for you ("HIBERNATE_SEQUENCE"). If you are using hibernate elsewhere, you may wish to change the sequence name so that you do not share this sequence with any other applications. If other applications try to manually create the default hibernate sequence, you may even run into situations where you find conflicts or a race condition.

The easiest way to handle this is to create an orm.xml file and place it within the classpath in a META-INF directory, which will override the jUDDI persistence annotations and will allow you to specify a specific sequence name for use with jUDDI. The orm.xml.example specifies a "juddi_sequence" sequence to be used with jUDDI. Rename this file and update it to your liking.

#### Switch to HSQL on Tomcat using Hibernate

First make sure you have a running hsqldb. For a standalone server startup use:

````
java -cp hsqldb.jar org.hsqldb.server.Server --port 1747 --database.0 file:juddi --dbname.0 juddi
````

Next, connect the client manager to this instance using:

````
java -classpath hsqldb.jar org.hsqldb.util.DatabaseManagerSwing --driver org.hsqldb.jdbcDriver --url jdbc:hsqldb:hsql://localhost:1747/juddi  -user sa
````

and create the juddi user:

````
CREATE USER JUDDI PASSWORD "password"  ADMIN;
CREATE SCHEMA JUDDI AUTHORIZATION JUDDI;
SET DATABASE DEFAULT INITIAL SCHEMA JUDDI;
ALTER USER juddi set initial schema juddi;
````		

From now on, one can connect as JUDDI user to that database and the database is now ready to go. To switch jUDDI over to HSQL you need to add the hsql driver (i.e. The _hsqldb.jar_) to the classpath and you will need to edit the _persistence.xml_ 

````
<property name="hibernate.dialect" value="org.hibernate.dialect.HSQLDialect"/>
````

and the datasource. For tomcat you the _context.xml_ should look something like 

````
<?xml version="1.0" encoding="UTF-8"?>
<Context>
    <WatchedResource>WEB-INF/web.xml</WatchedResource>
    <!-- HSQL data source -->
    <Resource name="jdbc/JuddiDS" auth="Container"
            type="javax.sql.DataSource" username="JUDDI" password="password"
            driverClassName="org.hsqldb.jdbcDriver"
            url="jdbc:hsqldb:hsql://localhost:1747/juddi"
            maxActive="8"/>
</Context>
````

#### Switch to other db

If you use another database, please document, and send us what you had to change to make it work and we will include it here. 

#### Override persistence properties in the juddiv3.xml

The juddiv3.xml file can be externalized; if you give the path of juddiv3.xml in the JVM args, the juddiv3.xml will not be picked up from the WAR. To use this set the  _juddi.propertiesFile_ to a location of your configuration file. This allows the user to change the jUDDI properties without having to open up the juddiv3.war file. For this use case it makes sense that also persistence properties can be overridden as well in the juddiv3.xml file. The following properties can be set: 

Hibernate properties that can be referenced in the _juddiv3.xml_ file

|property name                   |description              |example value
| --- | --- | --- |
|persistenceProvider             |JPA Implementation       |Hibernate
|hibernate.connection.datasource |datasource name          |java:/jdbc/JuddiDS
|hibernate.hbm2ddl.auto          |hibernate to ddl setting |java:/jdbc/JuddiDS
|hibernate.default_schema        |Schema name              |JuddiSchema
|hibernate.dialect               |DataBase vendor name     |org.hibernate.dialect.DB2Dialect

### Logging

The jUDDI codebase uses the _commons-logging-api_, and _log4j_ as the default logging implementation. The _juddiv3/WEB-INF/classes/commons-logging.properties_ sets the logging to _log4j_. The default _log4j_ configuration logs to a _juddi.log_ file in the _tomcat/logs_ directory. The _log4j_ configuration lives in the _juddiv3/WEB-INF/classes/log4j.properties_ file, which is referenced in the _web.xml_

````
<context-param>
    <param-name>log4jConfigLocation</param-name>
    <param-value>/WEB-INF/classes/log4j.properties</param-value>
</context-param>
````

The _commons-logging_ and _log4j_ jars are shipped in the _juddiv3/WEB-INF/lib_ directory.

If you are using CXF for the webservice stack you can log the request/response xml by adding

````
log4j.category.org.apache.cxf=INFO
````

to your log4j.properties and the cxf.xml file should contains this:

````
<cxf:bus>
    <cxf:features>
        <cxf:logging/>
    </cxf:features>
</cxf:bus>
````

The jUDDI beans.xml specifies the location of this file at _META-INF/cxf/cxf.xml_.

### Administering the GUI (juddi-gui.war)

There are a few things worth mentioning for administering the jUDDI Graphical User Interface. The first is user authentication, which is covered in the authentication chapter. The other the the Digital Signature Applet. This applet enables users to digitally signed UDDI entities via the GUI. There are a number of requirements in order for this to work. 

- The applet must be digitally signed. It is recommended that this signed by the administrator using the SSL certificate of the jUDDI instance. If it is not signed, it may not be able to digital certificates.
- The Oracle Java browser plugin must be installed. For details on this, visit Oracle's website.
- The end user must have a digital certificate installed that is accessible to the browser. On Windows computers, this is supported by Internet Explorer, Opera and Chrome which use the Windows Certificate Store (Start > Run > MMC, Add Certificates). Firefox uses its own certificate store. On MacOS, Safari uses the Mac Keychain.

### Task: Signing the Digital Signature Applet jar file

````
jarsigner -keystore your.keystore -storepass yourpass -keypass keypass <pathto>/juddi-gui.war/applets/juddi-gui-dsig-all.jar
````

Note: Jarsigner comes with most JDKs and has many command line options.

### Administrating your jUDDI Instance using the Administrative Console

Your instance of the jUDDI (juddiv3.war) can be managed via the administration console. It can be access url the following URL: 

````
http://localhost:8080/juddiv3/admin
````

By default, only users with the role "uddiadmin" are allowed to access this page. In addition, it must be accessed from the same computer hosting juddiv3.war (this can be changed if needed). When accessing the URL, you should be prompted for login via username/password (this can also be changed to another mechanism).

After authenticating, you will be prompted with a very similar interface to the juddi-gui.war. From here, you can perform a number of tasks.

- Access Status and Statistics of jUDDI
- Configure jUDDI (juddiv3.war)
- Access the jUDDIv3 API, which provides a number of administrative tasks and functions (requires an additional login)*

*Why is there another login required for the jUDDIv3 API functions?

The answer is because the admin console directly accesses a web service and it requires a user account with juddi admin rights. This may be the same username you use to access the admin console (juddiv3.war/admin) but unfortunately, this double login is unavoidable.

### Configure jUDDI

From the browser, it is possible to configure jUDDI's web services via the web browser. All of the settings available from the chapter on configuring jUDDI can be set there.

#### Enabling Remote Access

The jUDDI Configuration page by default is only accessible via the same host that is hosting the server. To enable remote access, change the setting

````
config/props/configLocalHostOnly=true
````

To false.

jUDDI Server Configuration Page. 

!["jUDDI Server Configuration Page"](images/juddi-admin-configure.png)


### Monitoring the Status and Statistics

The Statistics and Status page provides valuable information to administrators and developers looking to trouble shoot or debug problems with jUDDI.

#### Statistics

The Statistics page provides you with access to usage counts and time spent processing on each method of each service that jUDDI provides. 

TIP: This information can be pulled and is available in JSON encoded data from the following URL: http://localhost:8080/juddiv3/admin/mbeans.jsp

jUDDI Server Statistics.

!["jUDDI Server Statistics"](images/juddi-admin-stats.png)

or you can hook up the jconsole to look at the jUDDI mbeans

jUDDI MBeans.

!["jUDDI MBeans"](images/juddi-admin-mbeans.png)

#### Status

The Status page gives you the former "Happy jUDDI" page from version 2 of jUDDI.

jUDDI Server Status.

!["jUDDI Server Status"](images/juddi-admin-status.png)

### Accessing the jUDDIv3 API

The jUDDI API is a web service that extends the UDDI specification. It provides various functions for both configuring the jUDDI server and for performing administrative functions, such as authorizing a new username as a publisher, user rights assignment and so on. This page will let you access the functions from the web browser. 

TIP: You must authenticate using the top right hand side login/password box in order to use this.

jUDDI API.

!["jUDDI API"](images/juddi-admin-juddiapi.png)

### Security Guidance

This guide contains general security guidelines to ensure that your jUDDI server and jUDDI Client based application are relatively safe and to prevent authorized users.

This section is broken down into guidance for the jUDDI server and for the jUDDI Client

#### jUDDI Server

* Always use SSL or TLS for connections to and from the jUDDI server, especially connections where authentication is used. Use encrypted connections to the database server when possible. client configs (uddi.xml), database (juddiv3/WEB-INF/classes/META-INF/persistence.xml)

* If the juddi-gui web app is not on the same server as the juddiv3 web services web app, use SSL or TLS. (juddi-gui/WEB-INF/classes/META-INF/uddi.xml)

* Use UDDI Digital Signatures where appropriate. Enable all validation options. Java/.NET Clients + juddi-gui, uddi.xml uddi/client/signatures, checkTimestamps,checkTrust,checkRevocationCRL

* Require authentication for Inquiry API. (config/juddi/auth/Inquiry=true)

* Use a LDAP user store and set passwords to expire regularly. Enforce the usage of strong passwords of sufficient length and SSL for LDAP connections. (config/juddi/auth/token/authenticator)

* Encrypt all stored credentials (database, key stores, email, etc) with the highest possible encryption available. (config/juddi/cryptor=org.apache.juddi.v3.client.cryptor.AES256Cryptor or AES128)

* Configure Auth Tokens to expire with relatively short intervals. This should meet all automatic logout requirements and help reduce the risk that an intercepted auth token can't be reused by a 3rd party. (config/juddi/auth/token/Expiration) and (config/juddi/auth/token/Timeout)

* Configure Auth Tokens to require Same IP Enforcement. This is a mitigation factor for when a token is intercepted and attempted to be reused from another source. (config/juddi/auth/token/enforceSameIPRule=true)

* Configure Custody Transfer Tokens to expire with relatively short intervals. (config/juddi/transfer/expiration/days)

* Disable sending authentication tokens to subscription notifications (config/juddi/notification/sendAuthTokenWithResultList=false)

* If you're using the replication services, configure your application server to use mutual certification authentication for that deployment (per the specification's recommendation).

#### jUDDI Client (and developers)

* Never log auth tokens. Protect it as if it was a password

* Encrypt all stored credentials (key stores, UDDI credentials, etc) with the highest possible encryption available (uddi.xml)

* Discard auth tokens when they are no longer needed.

#### jUDDI GUI (Web user interface)

* Enable automatic logouts (WEB-ING/classes/META-INF/uddi.xml)

* All cached credentials are encrypted in the session tokens using an AES key that is generated at boot up time of the juddi-gui instance.

* Use SSL or TLS when connecting using your web browser to juddi-gui.

* The juddi-gui uses cookies to store user preferences, such as language and the current node.

* The juddi-gui makes heavy use of JavaScript using Jquery and JqueryUI. Without a JavaScript enabled browser that supports AJAX, the juddi-gui will not be functional. This usually implies Firefox 1.6 or higher, IE 6, Chrome/Chromium (nearly all versions), Opera v8 or higher, and Safari v2 or higher.

* The juddi-gui uses a Java applet that is used for Digital Signature support. This runs within your web browser. The Java plugin for your web browser must be enabled in order to use this functionality. In addition, the applet itself must be digitally signed (usually performed by the administrator, see article on this).

* The juddi-gui has built in validation for digital signatures. This requires a trusted key store. Ensure that the passwords are encrypted using the highest available crypto class and that the validation settings are enabled.

* The juddi-gui has a settings pages for altering the uddi.xml configuration file. By default, this is only accessible from the same machine running juddi-gui (i.e. localhost). This behavior can be changed by either using the setting page from localhost or by manually editing the uddi.xml page. Unless required, the recommended setting is to prevent remote configuration changes. If the settings page isn't required, it can be removed.

* The juddi-gui has a settings page that is password protected to prevent unauthorized changes. Use the strongest available mechanism to protect credentials. The default configuration is for HTTP BASIC. It is recommended to use this with SSL/TLS and/or switch to DIGEST based authentication. If the settings page isn't required, it can be removed.

### Backups, Upgrading and Data Migration

There are several different strategies for managing your jUDDI backups.

#### Database Backups 

Database backups are vendor specific and are effective for backup/restore to a similar or exact jUDDI version reinstall. 

#### Config Backup

Aside from database backups, you should also make backup copies of all jUDDI configuration files and any files that you have customized to meet your operational needs.

### Upgrading jUDDI

Sometimes, the jUDDI development team has no choice but to alter the database schema. In many cases, OpenJPA or Hibernate (both Java Persistence API provides) will automatically alter database columns when a new version is installed. In some cases, there may actually be data loss. 

TIP: Check the jUDDI distribution notes before attempting an upgrade.

IMPORTANT: Always perform a database level backup of your instance before attempting the upgrade.

### Scaling jUDDI and Federation

The capabilities and components provided by jUDDI are designed to scale. The following will describe the options and known limitations of jUDDI.

#### Scaling the jUDDI Services (multiple servers)

The jUDDI web services (juddiv3.war) is designed to be scaled to multiple servers in a number of ways. The following sub sections outline the available options.

##### Scaling using a common database

The first and simplest mechanism is for the instances of juddiv3.war to share the same database. All of jUDDI's database calls are transactional SQL, meaning that concurrent changes will function just fine from multiple concurrent users. Each instance of juddiv3.war must point to the same database and must use the same Node ID and configuration settings. See the Database Configuration Chapter for more information.

##### Scaling using Subscriptions

The second mechanism is to use the Subscription API to import data and updates from a remote registry. Unfortunately, this scenario isn't quite yet supported for jUDDI, but may be in a future release.

##### Replication API

The third mechanism is the Replication API, which is part of the OASIS UDDIv3 specification. Since version 3.3, jUDDI provides support for synchronizating UDDI servers using the techniques described in the specification as Replication. See the Replication Services chapter for additional information,


#### Limitations of jUDDI

jUDDI's web services have no explicit upper bound on the volume of businesses and services registered. Load testing has shown that at least 10,000 are supported for each category. The upper limit is more of a function of both the underlying database implementation and hardware (free disk space). In either case, the likelihood of hitting the limit is low for most instances. If you happen to run into scaling issues, please file a bug report at JUDDI's JIRA site at: https://issues.apache.org/jira/browse/JUDDI



