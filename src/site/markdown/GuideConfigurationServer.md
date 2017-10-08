## jUDDI Server Configuration (`juddiv3.xml`)

jUDDI will look for a `juddiv3.xml` file on the root of the classpath. In the `juddiv3.war` you can find it in `juddiv3.war/WEB_INF/classes/juddiv3.xml`.

Since 3.2 the jUDDI server now uses an XML file for configuration. Previous versions uses a properties file.

IMPORTANT: When referring to configuration 'properties', we are really referencing the XPath to specified setting.

### Authentication

Authentication properties that can be referenced in the `juddiv3.xml` file

|Property Name                   |Description       |Required       |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/auth/authenticator/class_            |The jUDDI authenticator class to use. See the Userguide for the choices provided. |N                         |_org.apache.juddi.v3.auth.JUDDIAuthenticator_
|_juddi/auth/Inquiry_      |This flag determines whether authentication (the presence of a getAuthToken) is required on queries invoking the Inquiry API. By default, jUDDI sets this to false for ease of use.|N  |_false_
|_juddi/auth/token/Timeout_      |Time in minutes to expire tokes after inactivity.|N  |_15_
|_juddi/auth/token/Expiration_   |As of 3.1.5 Duration of time for tokens to expire, regardless of inactivity.|N  |_15_
|_juddi/auth/token/enforceSameIPRule_   |As of 3.2 This setting will enable or disable the auth token check to ensure that auth tokens can only be used from the same IP address that they were issued to..|N  |_true_
|_juddi/auth/authenticator@useAuthToken_ | Indicates that the authenticator is use requires a UDDI auth token. Set to false when using HTTP based authenticators | N | _true_


### Startup

Startup properties that can be referenced in the `juddiv3.xml` file

|Property Name                   |Description         |Required     |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/server/baseurl_          |Token that can be accessed in accessPointURLs and resolved at runtime. Currently this is only used during the Installation process (seeding root data) |N   |_http://localhost:8080_
|_juddi/root/publisher_       | The username for the jUDDI root publisher. This is usually just set to "root". |N|_root_
|_juddi/seed/always_            | Forces seeding of the jUDDI data. This will re-apply all files with the exception of the root data files. Note that this can lead to losing data that was added to the entities that are re-seeded, since data is not merged.|N |_false_
|_juddi/server/name_            | This token is referenced in the install data. Note that you can use any tokens, and that their values can be set here or as system parameters..|N |_false_
|_juddi/server/port_            | This token is referenced in the install data. Note that you can use any tokens, and that their values can be set here or as system parameters..|N |_false_
|_juddi/nodeId_            | The Node ID uniquely identifies this server. Use caution when changing the Node ID after jUDDI has been started, you may not be able to edit any existing entities! ..|N |_uddi:juddi.apache.org:node1_
|_juddi//load/install/data_ | This property allows you to cancel loading of the jUDDI install data.|N | false
|_juddi/locale_ | The default local to use. This currently is not used. |N|_en_US_
|_juddi/operatorEmailAddress_ | The UDDI Operator Contact Email Address. This currently is not used. |N|_admin@juddi.org_
|_juddi/persistenceunit.name_ | The persistence name for the jUDDI database that is specified in the persistence.xml file.|N |juddiDatabase
|_juddi/configuration/reload/delay_ |The time in milliseconds in which juddiv3.xmlis polled for changes.|N|5000


CAUTION: Take caution in changing the jUDDI Node ID. (Updated at 3.3) jUDDI can now change Node IDs via the Admin console. However care must be taken to prevent changes to data while the rename is in progress. It is recommended to use the Admin console to change the Node ID. It will automatically update the database and the _juddiv3.xml_ configuration file.

### Email

As of 3.1.5, jUDDI supports Email delivery options for Subscription API functions. Email properties can be referenced in the _juddiv3.xml_ file

Starting with 3.2.1, jUDDI can now send a test email via the juddiv3.war/admin console.

|Property Name                   |Description         |Required     |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/mail/smtp/from_          |The Operator's Email address|Y|[_jUDDI@example.org_]
|_juddi/mail/smtp/host_          |The hostname of the SMTP server|Y|[_localhost_]
|_juddi/mail/smtp/port_          |The portname of the SMTP server|Y|[_25_]
|_juddi/mail/smtp/socketFactory.class_ |If set, specifies the name of a class that implements the _javax.net.SocketFactory interface_. This class will be used to create SMTP sockets. |N|
|_juddi/mail/smtp/socketFactory/fallback_|If set to true, failure to create a socket using the specified socket factory class will cause the socket to be created using the _java.net.Socket_ class. Defaults to true. |N|_true_
|_juddi/mail/smtp/starttls/enable_|f true, enables the use of the STARTTLS command (if supported by the server) to switch the connection to a TLS-protected connection before issuing any login commands. Note that an appropriate trust store must configured so that the client will trust the server's certificate. Defaults to false. |N|_false_
|_juddi/mail/smtp/socketFactory/port_|Specifies the port to connect to when using the specified socket factory. If not set, the default port will be used. |N|[_465_]
|_juddi/mail/smtp/auth_|If true, attempt to authenticate the user using the AUTH command. Defaults to false.|N|[_false_]
|_juddi/mail/smtp/user_|Username used to authenticate to the SMTP server|Y, if _juddi/mail/smtp/auth_ is true|[_juddi@apache.org_]
|_juddi/mail/smtp/password_|Username used to authenticate to the SMTP server|Y, if _juddi/mail/smtp/auth_ is true|[_secret_]
|_juddi/mail/smtp/password@encrypted_|If the password is encrypted, the setting juddi/cryptor is the Cryptographic provider used to decrypt at runtime.|Y, if _juddi/mail/smtp/auth_ is true|false

### Query Properties

Query properties that can be referenced in the _juddiv3.xml_ file


|Property Name                   |Description         |Required     |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/maxBusinessesPerPublisher_          |The maximum number of UDDI Businesses that can be registered  per publisher. A value of '-1' indicates any number of businesses is allowed (These values can be overridden at the individual publisher level)| N| -1
|_juddi/maxServicesPerBusiness_|The maximum number of UDDI BusinessServices allowed per Business. A value of '-1' indicates any number of artifacts is valid (These values can be overridden at the individual publisher level).|N|-1
|_juddi/maxBindingsPerService_ | The maximum number of UDDI TemplateBindings allowed per BusinessService. A value of '-1' indicates any number of artifacts is valid (These values can be overridden at the individual publisher level).|N|-1
|_juddi/maxTModelsPerPublisher_|The maximum number of TModels allowed per publisher. A value of '-1' indicates any number of artifacts is valid (These values can be overridden at the individual publisher level).|N|-1
|_juddi/maxInClause_ | The maximum number of "IN" clause parameters.  Some RDMBS limit the number of parameters allowed in a SQL "IN" clause. |Y|[_1000_]
|_juddi/maxNameElementsAllowed_| The maximum name size and maximum number of name elements allows in several of the _FindXxxx_ and _SaveXxxx_ UDDI functions |N|[_5_]
|_juddi/maxNameLength_ | The maximum name size of name elements|N|[_255_]
|_juddi/maxRows_ | The maximum number of rows returned in a find* operation.  Each call can set this independently, but this property defines a global maximum. This is related to the _maxInClause_ setting (the same?).|N|1000

### RMI Proxy 

These properties are used to bring up RMI server socket. The settings allow for registering this service to JNDI.

RMI Proxy properties that can be referenced in the _juddiv3.xml_ file and is only used by RMITransport.

|Property Name                   |Description         |Required     |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/proxy/factory/initial_ | JNDI Contect Facory    |N|[_org.jnp.interfaces.NamingContextFactory_]
|_juddi/proxy/provider/url_    | JNDI Provider Address  |N|[_jnp://localhost:1099_]
|_juddi/proxy/factory/url/pkg_ | JNDI Naming Convention |N|[_org.jboss.naming_]


### Key Generation and Cryptography

UDDI Key generation properties that can be referenced in the `juddiv3.xml` file.

|Property Name                   |Description         |Required     |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/cryptor_ | jUDDI Cryptor implementation class that jUDDI will use to encrypt and decrypt password settings |N|_org.apache.juddi.cryptor.DefaultCryptor_
|_juddi/keygenerator_ | Key generator implementation that jUDDI will use to create UDDI keys if no key is passed in by the user.|N|_org.apache.juddi.keygen.KeyGenerator_
|_juddi/uuidgen_ | UUID generator implementation that jUDDI will use to create UUIDs.|N|_org.apache.juddi.uuidgen.DefaultUUIDGen_

### Subscription

Subscription properties that can be referenced in the _juddiv3.xml_ file.

|Property Name                   |Description         |Required     |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/subscription/expiration/days_|Days before a subscription expires|N|[_30_]
|_juddi/subscription/chunkexpiration/minutes_| Minutes before a "chunked" subscription call expires|N|[_5_]
|_juddi/notification/interval_|Specifies the interval at which the notification timer triggers. This is the upper boundary set by the registry. Between the user defined endDate of a Subscription and this value, the registry will pick the earliest date. (in ms)|N|_3000000_
|_juddi/notification/start/buffer_|Specifies the amount of time to wait before the notification timer initially fires. (in ms)|N|20000
|_juddi/notification/acceptableLagtime_|Specifies the amount of time (in ms) from which to determine if the server is overload and to skip notifications. Notifications during this cycle will not be repeated (i.e. never be delivered). (in ms)|N|10000
|_juddi/notification/maxTries_|Specifies the number of times to attempt the delivery of messages to subscribers.|N|3
|_juddi/notification/maxTriesResetInterval_|Once the maximum delivery attempts have been made, the server will add that endpoint to an ignore list, which is reset every N ms.|N|600000
|_juddi/notification/sendAuthTokenWithResultList_|Sends a valid authentication token for the owning user of the subscription in the subscription notification result message. Unless it is specifically needed, this is recommended to be set to false.|N|false

### Custody Transfer

Transfer properties that can be referenced in the _juddiv3.xml_ file.

|Property Name                   |Description         |Required     |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/transfer/expiration/days_|Days before a transfer request expires.|N|[_3_]


### Validation

These settings are for validating the data that users store in jUDDI. They can be referenced in the _juddiv3.xml_ file.

|Property Name                   |Description         |Required     |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/validation/enforceReferentialIntegrity_|As of 3.1.5 This setting will force referential integrity for all tModels (except keyGenerators), category bags, bindingTemplate/AccessPoint/hostingRedirector (referencing another host), tModelInstanceParms and anything else that references a KeyName default value is true. Set to false for backwards compatibility or for a more lax registry.|N|[_true_]
|_juddi/validation/rejectInvalidSignatures/enable_ | Enables or Disables the validation of signatures when a publisher attempts to save an entity | N | false
|_juddi/validation/rejectInvalidSignatures/enable/trustStorePath_ | Path to the trust store. Can be overridden via system properties. If not specified, the Windows trust store will be used, else the default JRE trust store will be used. | N | [truststore.jks]
|_juddi/validation/rejectInvalidSignatures/trustStoreType_ | The type of store to use | N | JKS
|_juddi/validation/rejectInvalidSignatures/trustStorePassword_ | The clear text or encrypted password to the trust store | N | 
|_juddi/validation/rejectInvalidSignatures/trustStorePassword@isPasswordEncrypted_ | True/False | N | false
|_juddi/validation/rejectInvalidSignatures/trustStorePassword@cryptoProvider_ | A cryptographic provider, representing the one that was used to encrypt | 
|_juddi/validation/rejectInvalidSignatures/checkTimestamps_ | If true, certificates are checked against the time validity | N | false
|_juddi/validation/rejectInvalidSignatures/checkTrust_ | If true, the certificates trust chain is validated against the trust store | N | false
|_juddi/validation/rejectInvalidSignatures/checkRevocationCRL_ | If true, the certificate will attempted to be validated using online certificate revocation protocols | N | false


### Logging

These properties are used to enable additional logging capabilities.

Logging properties that can be referenced in the _juddiv3.xml_ file.

|Property Name                   		|Description         |Required     |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/logging/logInquirySearchPayloads_| Enables request payload logging for the Inquiry Find apis    |N| false


### Performance

These properties are used to enable or disable certain capabilities based on performance considerations.

Perofrmance properties are referenced in the _juddiv3.xm_ file.

|Property Name                   		|Description         |Required     |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/performance/enableFindBusinessTModelBagFiltering_ | UDDI defines a mechansim to filter findBusiness relates based on tModelInstanceInfo within their service's binding templates. This is an expensive operation and will cause significant performance degredation on larger registries. For spec complliance, it should be set to true. We suspect it's not a commonly used feature and recommend setting this to false.    |N| true


### Replication

These properties are used to tweak the replication service capabilities.

These properties are referenced in the _juddiv3.xml_ file.


|Property Name                   		|Description         |Required     |Default Value or [Example Value]
| --- | --- | --- | --- |
|_juddi/replication/getChangeRecordsMax_ | The maximum number of records to return from a getChangeRecord request   |N| 100
|_juddi/replication/start/buffer_ 		| Specifies the amount of time to wait before the replication timer initially fires. (in ms) | N | 5000
|_juddi/replication/interval_ 			| Specifies the interval at which the replication timer triggers (in ms). | N | 5000


### Deploying two or more jUDDI server on the same application server

It is possible to deploy one or more jUDDI servers to the same application server. You will need copy the juddiv3.war archive (let's say you copied it to juddiv3a.war), and change the following settings to have it connect to a different database:

1. edit the `juddiv3a/META-INF/context.xml` (and `conf/Catalina/localhost/juddiv3a.xml`) to use the _jdbc/JuddiADS_ datasource, and add 'a' to the url: `url="jdbc:derby:target/juddi-derby-test-db-v3a;create=true"`
2. edit the _juddiv3a/WEB-INF/classes/META-INF/persistence.xml_ to use `<non-jta-data-source>java:comp/env/jdbc/JuddiADS` and `persistence-unit name="juddiADatabase"`
3. edit the _juddiv3a/WEB-INF/classes/juddiv3.xml_ to have
`<persistenceunit><name>juddiADatabase</name></persistenceunit>`

This will create a new jUDDI server under the http://localhost:8080/juddiv3a url which connects to the juddi-derby-test-db-v3a Derby database.


### jUDDI GUI Configuration

The jUDDI GUI (juddi-gui.war) has one place for configuration settings, the jUDDI Client config file.

### jUDDI Client uddi.xml Settings

Defined in WEB-INF/classes/META-INF/uddi.xml, there are many settings to configure. All of these are clearly defined by the jUDDI Client Configuration Guide. The juddi-gui, uses things a bit differently, so here are the relevant parts to use. Note: this is xpath notation.

* uddi/client/nodes/properties, not used
* uddi/client/clerks, not used
* uddi/client/nodes/node, all URLs except juddiApiUrl (not used)
* uddi/client/signature, all validation related settings
* uddi/client/subscriptionCallbacks, not used
* uddi/client/XtoWsdl, not used

In addition, there a special section added just for the juddi-gui.war

jUDDI GUI Configuration

|Property Name                   |Description       |Required       |Default Value or [Example Value]
| --- | --- | --- | --- |
|_uddi/config/props/authtype_            |This controls the authentication mode to connect to a UDDI server. Most implementations of UDDI use the security service, however others use HTTP based authentication. In this case, us the value of 'HTTP', otherwise 'UDDI_AUTH' |Y                         | _UDDI_AUTH_
|_uddi/config/props/enableAutomaticLogouts_      | This flag determines whether automatic logouts is enabled. By default, jUDDI-gui sets this to false for ease of use. (true/false)|N  |_false_
|_udddi/config/props/enableAutomaticLogouts/duration_      | Time in milliseconds to force an automatic logout after inactivity.|N  | _900000_
|_uddi/config/props/configLocalHostOnly_  |If false, the configuration page will be available from anywhere. If true, it will only be accessible from the server hosting juddi-gui. (true/false) |N   |_true_


### Encryption Keys

By default, the juddi-gui will use a randomly generated AES encryption key to help protect user credentials stored in the session object. This key is generated using the "StartupServlet" defined in the web.xml file of juddi-gui.war/WEB-INF/web.xml and then it is stored at the path `juddi-gui.war/META-INF/config.properties@key`. 

If the start up servlet fails to start, any authenticatation operation of the juddi-gui will fail.

IMPORTANT: The user account that the container for juddi-gui runs as must have write access to the file `juddi-gui.war/META-INF/config.properties`.

### Customizing the juddi-gui

The juddi-gui has a mechanism that you can use to alter the appearance of every page. This is typically used for organizations that require legal notifications, banners or warnings on every page for one reason or another. To add your own html to every page, edit the file in 

````
juddi-gui/user/banner.jsp
````
