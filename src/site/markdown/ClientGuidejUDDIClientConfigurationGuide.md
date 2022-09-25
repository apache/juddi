## jUDDI Client Configuration Guide


### Introduction

The jUDDI Java and .NET clients use an XML configuration file that dictates settings and behaviors.
This guide provides an overview of the settings. Both .NET and jUDDI use the same configuration file schema. This schema is located within the source tree and with the client distribution packages of jUDDI.

### Client Settings

The root XML node for the jUDDI client configuration file is always "uddi".

````
<!-- applies to Java clients only -->
uddi/reloadDelay
````

Multiple clients can be defined in each configuration file.

````
uddi/client@name="someName"
````

#### Nodes

At least one node is required per client. A node represents a one logical UDDI server (or cluster of servers). Each UDDI node should host at least the inquiry service. A client using the jUDDI client package can be configured to access multiple nodes at the same time.

````
<!-- if isHomeJUDDI is true, then this node is loaded by default,
 when no node is specified in client code -->
uddi/client/nodes[]/node@isHomeJUDDI=true/false
<!-- the name of the node is referenced in client code -->
uddi/client/nodes[]/node/name
<!-- the description of the node -->
uddi/client/nodes[]/node/description
<!-- the properties section defines HTTP style credentials and a 
runtime tokenizer for URLs -->
uddi/client/nodes[]/node/properties
<!-- The transport represents the class name of the transport
 mechanism that the client will use to connect
to the UDDI node. The most commonly used is 
org.apache.juddi.v3.client.transport.JAXWSTransport, however
RMITransport, InVMTransport and JAXWSv2TranslationTransport are 
also defined -->
uddi/client/nodes[]/node/proxyTransport

<!-- endpoint URLs -->
uddi/client/nodes[]/node/custodyTransferUrl
uddi/client/nodes[]/node/inquiryUrl
uddi/client/nodes[]/node/publishUrl
uddi/client/nodes[]/node/securityUrl
uddi/client/nodes[]/node/subscriptionUrl
uddi/client/nodes[]/node/subscriptionListenerUrl
<!-- note: this is for jUDDI v3.x servers only and is not part
 of the UDDI standard -->
uddi/client/nodes[]/node/juddiApiUrl
````

#### Transport Options

The Proxy Transport defines which mechanism is used for 'on the wire' transport of the UDDI XML messages. JAXWS Transport is the most commonly used and assumes SOAP messaging protocol over HTTP transport layer.

RMI Transport using the Java Remote Method Invocation for transport. It's more commonly used for communicating within a J2EE container, but could be used in other cases. It's not required by the UDDI specification and is considered a jUDDI add on.

InVM Transport is for hosting jUDDI services without a J2EE container.

JAXWSv2TranslationTransport is a bridge for accessing UDDIv2 web services using the UDDIv3 data structures and APIs. Only the Inquiry and Publish services are required and they must point to SOAP/HTTP endpoints for a UDDI v2 node.

#### Clerks

Clerks are responsible for mapping stored user credentials to a Node and for automated registration. 

````
<!-- if true, the contents of the child node xregister are 
registered when the UDDIClient object is created, using the
 credential and node reference.-->
uddi/client/clerks/registerOnStartup=true/false
````

##### Clerk

Clerks store credentials and map to a specific node.

````
<!-- the name is a reference to the Node that these credentials 
apply to-->
uddi/client/clerks[]/clerk@node - This is reference to 
	uddi/client/node/name, it must exist
uddi/client/clerks[]/clerk@name - This is a unique identifier 
	of the clerk
uddi/client/clerks[]/clerk@publisher  - This is the username
uddi/client/clerks[]/clerk@password
uddi/client/clerks[]/clerk@isPasswordEncrypted=true/false
uddi/client/clerks[]/clerk@cryptoProvider=(see Crypto providers)
````

Credentials can be encrypted using the included batch/bash scripts and then appended to the configuration. Example with encryption:

````
<clerk name="default" node="default" publisher="root" 
password="(cipher text removed)"
isPasswordEncrypted="true" 
cryptoProvider="org.apache.juddi.v3.client.cryptor.AES128Cryptor" />
````

Clerks also have settings for the automated cross registration of Businesses and Services on start up. 

````
uddi/client/clerks[]/xregister/service@bindingKey
uddi/client/clerks[]/xregister/service@fromClerk
uddi/client/clerks[]/xregister/service@toClerk
````

#### Digital Signatures

The Signature section contains settings that map to the Digital Signature Utility that makes working with UDDI digital signatures simple. The section contains all of the settings for both signing and validating signatures.

````
uddi/client/signature/signingKeyStorePath
uddi/client/signature/signingKeyStoreFilePassword
uddi/client/signature/signingKeyStoreFilePassword\
	@isPasswordEncrypted
uddi/client/signature/signingKeyStoreFilePassword@cryptoProvider
uddi/client/signature/signingKeyPassword
uddi/client/signature/signingKeyPassword@isPasswordEncrypted
uddi/client/signature/signingKeyPassword@cryptoProvider
uddi/client/signature/signingKeyAlias
uddi/client/signature/canonicalizationMethod
uddi/client/signature/signatureMethod=(default RSA_SHA1)
uddi/client/signature/XML_DIGSIG_NS=\
	(default http://www.w3.org/2000/09/xmldsig#)
uddi/client/signature/trustStorePath
uddi/client/signature/trustStoreType
uddi/client/signature/trustStorePassword
uddi/client/signature/trustStorePassword@isPasswordEncrypted
uddi/client/signature/trustStorePassword@cryptoProvider
<!-- checks signing certificates for timestamp validity -->
uddi/client/signature/checkTimestamps
<!-- checks signing certificates for trust worthiness -->
uddi/client/signature/checkTrust
<!-- checks signing certificates for revocation -->
uddi/client/signature/checkRevocationCRL
uddi/client/signature/keyInfoInclusionSubjectDN
uddi/client/signature/keyInfoInclusionSerial
uddi/client/signature/keyInfoInclusionBase64PublicKey
<!-- default is http://www.w3.org/2000/09/xmldsig#sha1 -->
uddi/client/signature/digestMethod
````

#### Subscription Callbacks

The subscriptionCallbacks section defines settings uses by the subscription callback API. This enables developers to create capabilities that need to be notified immediately when something in UDDI changes through the use of subscriptions. 

````
uddi/client/subscriptionCallbacks/keyDomain
uddi/client/subscriptionCallbacks/listenUrl this is the URL that 
	is used for callbacks, should be externally resolvable
uddi/client/subscriptionCallbacks/autoRegisterBindingTemplate=\
	true/false
uddi/client/subscriptionCallbacks/autoRegisterBusinessServiceKey\
	=(key) append callback endpoint to this service
uddi/client/subscriptionCallbacks/signatureBehavior=(\
	AbortIfSigned,Sign,DoNothing,SignOnlyIfParentIsntSigned), 
	default DoNothing. Applies when auto registering the 
	endpoint to a business or service that is already signed.
````

#### XtoWsdl

XtoWsdl represents configuration parameters for importing WSDL or WADL files. Currently, the only setting is for ignoring SSL errors when fetching remote WSDL or WADL files.

````
uddi/client/XtoWsdl/IgnoreSSLErrors=true or false
````

### Embedded jUDDI server

jUDDI has the ability to run in embedded mode. This means that the jUDDI services can operate without a web servlet container, such as Tomcat or JBoss. Typically, this is something that application developers would use for more advanced scenarios and for operation without network connectivity.

#### Requirements

A database server, if one is not available, the embedded Derby engine can be used.

#### Changes in configuration compared to non-embedded

* META-INF/embedded-uddi.xml needs to contain the connection settings for InVmTransport.
````
<!-- In VM Transport Settings -->
<proxyTransport>
org.apache.juddi.v3.client.transport.InVMTransport
</proxyTransport>
<custodyTransferUrl>
org.apache.juddi.api.impl.UDDICustodyTransferImpl
</custodyTransferUrl>
<inquiryUrl>
org.apache.juddi.api.impl.UDDIInquiryImpl
</inquiryUrl>
<publishUrl>
org.apache.juddi.api.impl.UDDIPublicationImpl
</publishUrl>
<securityUrl>
org.apache.juddi.api.impl.UDDISecurityImpl
</securityUrl>
<subscriptionUrl>
org.apache.juddi.api.impl.UDDISubscriptionImpl
</subscriptionUrl>
<subscriptionListenerUrl>
org.apache.juddi.api.impl.UDDISubscriptionListenerImpl
</subscriptionListenerUrl>
<juddiApiUrl>
org.apache.juddi.api.impl.JUDDIApiImpl
</juddiApiUrl>
````

* The serverside config file juddiv3.xml needs to be added to the classpath.
* A META-INF/persistence.xml needs to be added.
* Add the juddi-core (UDDI server) and derby (Embedded Database) dependencies to the pom. Use the juddi-core-openjpa jar for OpenJPA.

See also the hello-world-embedded example.
