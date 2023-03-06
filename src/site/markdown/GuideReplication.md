## Replication Services

### Introduction

The UDDIv3 specification introduced a Replication API that outlines a mechansim for maintaing data ownership and data synchronization across more than one UDDI node. The replication specification has a number of facets that to the casual reader, can see overwhelmingly complex. jUDDI v3 provides support for the majority of the UDDIv3 replication API. This article will attempt to describe the in's and out's of the specification, what jUDDI supports and doesn't, finally, how to use it with your jUDDI instance(s).

### UDDIv3 Replication Overview

The UDDIv3 replication API defines a number of web service methods that are used to manage and replicate UDDI data. Each node is responsible for maintaining a record of all changes made both locally and at all remote nodes. Everytime a Business, Service, Binding, tModel, or Publisher Assertion changes, all nodes are notified of the change. Once receiving the notification of the change, all nodes are then responsible to obtain the change set, apply it locally, and then retransmit (if needed and based on topology). The topology is configured via the Replication Configuration. With jUDDI, this is configured using the adminstration console.

There's one important note to remember. Each piece of data in UDDI is owned by a given node.

#### UDDIv3 Replication Topology

The specification identifies two scenarios for replication topology. 

 1. Non-directed Graph: All nodes can talk directly to each other and have direct communication with each other.
 2. Directed Graph: Nodes can only talk to subset of the complete set of nodes.

The Non-directed graph is easier to implement and to understand. During the "notify" phase of replication, the node where the change originates, simply tell's everyone it knows about it.

In a directed graph, the node where the change originates only notifies the nodes designated nodes. This typically forms some kind of ring in which one node notifies the next and so on until the original change ends up at the origin.

#### Conflict handling

The specification defines a mechanism that is similar to a two step commit (for those familiar with database terminology). Esscentially, when a given change (typically a new record) is created, it then notifies all other nodes to put a block on the new record's keys and waits for all nodes to respond with an "OK" to commit message. This prevents the same record from being created in multiple locations. These types of messages are refered to in the specification as NewDataConditional. As of the time of this writting jUDDI doesn't support it. When a record is created at the same at two different nodes within the same replication graph, jUDDI will simply reject the change and prevent the modifications or the transfer from happening. Records that fail to apply for one reason or another are stored in the database and can be accessed via the admin console via "Admin" and selected "getFailedReplicationChangeRecords" from the drop down menu.

### Configuring your jUDDI Node for replication

Prerequisites:

 1. Each node must have a unique ID associated with it.
 2. Each node must have the UDDI v3 Replication service (juddiv3replication.war) deployed and configured for CLIENT-CERT authentication using SSL/TLS.
 3. Each node must have a configured JKS key store and trust store.

#### Changing the Node ID

Forgot to change the Node ID before starting jUDDI for the first time? No problem. Visit the jUDDI Administration console at http://localhost:8080/juddiv3/admin, then go to the Admin page and select "Change Node Id" from the drop down menu.

#### Setting up CLIENT-CERT authentication

Since a registry can be corrupted via the replication endpoint, it is important to provide adequate security. The UDDI spec recommends using mutual certificate authentication. This is somtimes returned to as "CLIENT-CERT", certificate based authentication, or two-way SSL. All of these terms really refer to the same thing. jUDDI comes prebundled with Apache Tomcat that is configured for mutal certificate authentication out of the box (with self signed certificates). To setup CLIENT-CERT authentication, please see the documentation for your web application server.

##### Special notes on key stores and trust stores

jUDDI's use of key stores and trust stores for replication purposes using the standard system properties
 - -Djavax.net.ssl.keyStore
 - -Djavax.net.ssl.keyPassword
 - -Djavax.net.ssl.trustStore 
 - -Djavax.net.ssl.trustStorePassword 

These are used for transport layer security (node to node). On a side node, jUDDI (server) can also use the trust store to verify signed entities (configured though _juddiv3.xml_) and finally, the application server itself needs access to the key store and trust store for providing a certificate for SSL/TLS communication with clients for validating users (or another jUDDI replication node) that provide a client certificate. 

For Tomcat, all you need is a connector with "clientAuth=want". Here's an example:

````
<Connector port="8443" protocol="HTTP/1.1" SSLEnabled="true"
               maxThreads="150" scheme="https" secure="true"
               clientAuth="want" sslProtocol="TLS" 
               truststoreFile="truststore.jks" 
			   truststorePass="password"
			   keystoreFile="conf/keystore.jks" 
			   keystorePass="password"/>
````

##### Mapping certificates to roles

For each certificate that is used by a jUDDI node to authenticate to another, you'll have to map the Subject DN of the certificate to a user with the role "replication". In our example, we'll use tomcat's _tomcat-users.xml_ file.

````
<user username="CN=localhost, OU=jUDDI Test, O=Apache Software \
	Foundation, L=Anytown, ST=MD, C=US" password="null" \
	roles="replication"/>
````

In this example, we've added our test certificate's subject DN to the role of "replication". 

TIP: If you run into issues getting things working, try adding the following to the startup parameters for tomcat: -Djavax.net.debug=all

IMPORTANT: Besides mapping the certificates to the replication role, either the certificate itself or the issuer of the certificate must be in the trust store used by the application server.

Since dealing with certificates can be confusing, consider the following configuration.

 - Node 1 sends updates to Node 2
 - Node 2 sends updates to Node 1

Then the certificates must be setup as follows (assuming that each node's SSL cert is used for authentication to the other node(s))

 - Node 1's public key must be trusted by Node 2 (in Node 2 app server's trust store)
 - Node 2's public key must be trusted by Node 1 (in Node 1 app server's trust store)
 - Node 1 must have Node 2's certificate's Subject DN mapped to the 'replication' role
 - Node 2 must have Node 1's certificate's Subject DN mapped to the 'replication' role
 - Node 1's public and private keys must be in a keystore on Node 1 (and the Java -D properties set)
 - Node 2's public and private keys must be in a keystore on Node 2 (and the Java -D properties set)

#### Setting the Replication Configuration

To set the replication configuration, you'll need to go to http://localhost:8080/juddiv3/admin then click on "Admin" in the top navigation bar and login. Once logged in, select "set_ReplicationNodes" from the drop down menu. The text entry field is actually resizable, so you'll probably want to make it bigger. This text box should be pre-populated with an example replication configuration. Edit the replication as needed, then click the "Go!" button to save it.

Note: when saving the configuration, several of the fields (time stamp, serial number) will be overwritten by the server. This is normal.

Additional notes:
jUDDI doesn't currently support maximumTimeToSyncRegistry, maximumTimeToGetChanges, and controlledMessage. Due to the way the specification was written, these fields are mandatory (they must be in the Replication Configuration XML), but jUDDI wont't respect them.

##### Replication Configuration

When using jUDDI's Admin console to set the replication config, here's a few things to keep in mind (using xpath notation).

 - replicationConfiguration/operator()  - All nodes in the replication graph must be listed in the Operator section, including all directed graph nodes
 - replicationConfiguration/registryContact - Must have at least one contact. If one is specified for the node's root business, then jUDDI will include that with the default config.
 - replicationConfiguration/communicationGraph - Must be specified with all nodes listed as identified by the NodeID in replicationConfiguration/operator/operatorNodeID. 
 - replicationConfiguration/communicationGraph/controlledMessage must be specified. jUDDI uses a '*' to represent all messages.
 - replicationConfiguration/maximumTimeToSyncRegistry isn't used and jUDDI will always set it to 1
 - replicationConfiguration/maximumTimeToGetChanges - isn't used and jUDDI will always set it to 1
 - replicationConfiguration/serialNumber - jUDDI will always set this to the time stamp when the configuration was last changed (time since epoch)
 - replicationConfiguration/timeOfConfigurationUpdate - jUDDI will always set this to the time stamp when the configuraiton was last changed in a human readable form. The UDDI specification doesn't state what format it should be in, so we used ISO 8601 as the format.

Everytime the configuration changes, an audit log is required in jUDDI log file.

Here's an example default configuration

````
<?xml version="1.0" encoding="UTF-8"?><replicationConfiguration 
	xmlns="urn:uddi-org:repl_v3" xmlns:ns2="urn:uddi-org:api_v3" 
	xmlns:ns3="http://www.w3.org/2000/09/xmldsig#">
    <serialNumber>1424114880586</serialNumber>
    <timeOfConfigurationUpdate>
		201502161428-0500
	</timeOfConfigurationUpdate>
    <registryContact>
        <ns2:contact>
            <ns2:personName>unknown</ns2:personName>
        </ns2:contact>
    </registryContact>
    <operator>
        <operatorNodeID>
			uddi:juddi.apache.org:node1
		</operatorNodeID>
        <operatorStatus>normal</operatorStatus>
        <ns2:contact/>
        <soapReplicationURL>
		http://localhost:8080/juddiv3/services/replication
		</soapReplicationURL>
    </operator>
    <communicationGraph>
        <node>uddi:juddi.apache.org:node1</node>
        <controlledMessage>*</controlledMessage>
    </communicationGraph>
    <maximumTimeToSyncRegistry>1</maximumTimeToSyncRegistry>
    <maximumTimeToGetChanges>1</maximumTimeToGetChanges>
</replicationConfiguration>
````

Here's an example non-directed replicaton graph. In this example, all changes perform on all nodes get set to all the other nodes.

````
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<replicationConfiguration xmlns="urn:uddi-org:repl_v3" 
	xmlns:ns2="urn:uddi-org:api_v3" 
	xmlns:ns3="http://www.w3.org/2000/09/xmldsig#">
    <serialNumber>0</serialNumber>
    <timeOfConfigurationUpdate></timeOfConfigurationUpdate>
    <registryContact>
        <ns2:contact>
            <ns2:personName>unknown</ns2:personName>
        </ns2:contact>
    </registryContact>
    <operator>
        <operatorNodeID>
			uddi:juddi.apache.org:node1
		</operatorNodeID>
        <operatorStatus>normal</operatorStatus>
        <ns2:contact useType="admin">
            <ns2:personName xml:lang="en">bob</ns2:personName>
        </ns2:contact>
        <soapReplicationURL>
		https://localhost:8443/juddiv3replication/services/replication
		</soapReplicationURL>
    </operator>
    <operator>
        <operatorNodeID>
		uddi:another.juddi.apache.org:node2
		</operatorNodeID>
        <operatorStatus>normal</operatorStatus>
        <ns2:contact useType="admin">
            <ns2:personName xml:lang="en">mary</ns2:personName>
        </ns2:contact>
        <soapReplicationURL>
		https://localhost:9443/juddiv3replication/services/replication
		</soapReplicationURL>
    </operator>
    <communicationGraph>
        <node>uddi:juddi.apache.org:node1</node>
        <node>uddi:another.juddi.apache.org:node2</node>
        <controlledMessage>*</controlledMessage>
    </communicationGraph>
    <maximumTimeToSyncRegistry>1</maximumTimeToSyncRegistry>
    <maximumTimeToGetChanges>1</maximumTimeToGetChanges>
</replicationConfiguration>
````

In this example, we have a directed graph where Node 1 sends to Node2, Node 2 to Node 3, and Node 3 to Node 1. Note the addition of the replicationConfiguration/communicationGraph/edge() that defines this interaction pattern. Again all nodes defined in edges must also be defined both in the communicationGraph and as operator() XML elements.

````
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<replicationConfiguration xmlns="urn:uddi-org:repl_v3" 
xmlns:ns2="urn:uddi-org:api_v3" 
xmlns:ns3="http://www.w3.org/2000/09/xmldsig#">
    <serialNumber>0</serialNumber>
    <timeOfConfigurationUpdate></timeOfConfigurationUpdate>
    <registryContact>
        <ns2:contact>
            <ns2:personName>unknown</ns2:personName>
        </ns2:contact>
    </registryContact>
    <operator>
        <operatorNodeID>
		uddi:juddi.apache.org:node1
		</operatorNodeID>
        <operatorStatus>normal</operatorStatus>
        <ns2:contact useType="admin">
            <ns2:personName xml:lang="en">bob</ns2:personName>
        </ns2:contact>
        <soapReplicationURL>
		https://localhost:8443/juddiv3replication/services/replication
		</soapReplicationURL>
    </operator>
    <operator>
        <operatorNodeID>
		uddi:another.juddi.apache.org:node2
		</operatorNodeID>
        <operatorStatus>normal</operatorStatus>
        <ns2:contact useType="admin">
            <ns2:personName xml:lang="en">mary</ns2:personName>
        </ns2:contact>
        <soapReplicationURL>
		https://localhost:9443/juddiv3replication/services/replication
		</soapReplicationURL>
    </operator>
    <operator>
        <operatorNodeID>
		uddi:yet.another.juddi.apache.org:node3
		</operatorNodeID>
        <operatorStatus>normal</operatorStatus>
        <ns2:contact useType="admin">
            <ns2:personName xml:lang="en">mary</ns2:personName>
        </ns2:contact>
        <soapReplicationURL>
		https://localhost:10443/juddiv3replication/services/replication
		</soapReplicationURL>
    </operator>
    <communicationGraph>
        <node>uddi:another.juddi.apache.org:node2</node>
        <node>uddi:juddi.apache.org:node1</node>
        <node>uddi:yet.another.juddi.apache.org:node3</node>
        <edge>
            <messageSender>
			uddi:juddi.apache.org:node1
			</messageSender>
            <messageReceiver>
			uddi:another.juddi.apache.org:node2
			</messageReceiver>
        </edge>
        <edge>
            <messageSender>uddi:another.juddi.apache.org:node2
			</messageSender>
            <messageReceiver>uddi:yet.another.juddi.apache.org:node3
			</messageReceiver>
        </edge>
        <edge>
            <messageSender>
			uddi:yet.another.juddi.apache.org:node3
			</messageSender>
            <messageReceiver>
			uddi:juddi.apache.org:node1
			</messageReceiver>
        </edge>
    </communicationGraph>
    <maximumTimeToSyncRegistry>1</maximumTimeToSyncRegistry>
    <maximumTimeToGetChanges>1</maximumTimeToGetChanges>
</replicationConfiguration>
````

One last point of interest, Edge's can have a list of alternate message receivers and it is supported by jUDDI.

#### Performing Custody Transfer between nodes

Custody transfer (from a user's perspective) happens exacty the same way as it would to transfer between two users on the same node. The only change is that the Replication API plays a signficant role in this process and is thus a requirement.


#### What's Supported and What's Not

Here's a quick summary of what is and isn't supported for jUDDI replication capabilities. Want more support? Open a ticket and contribute.

Supported:

 - Directed graph replication with retransmit (primary and alternate message receivers)
 - Non-directed graphic replication (no edges defined)
 - All UDDI data is replicated (Business, Binding, Serivce, tModels and Publisher Assertions)
 - Custody transfer from Node to Node within the replication graph.

Functions not supported:

 - Conditional Data Updates
 - Configuration Settings:
   - maximumTimeToSyncRegistry
   - maximumTimeToGetChanges
   - OperatorStatus - Node Status (New, Normal, Resigned)
   - Controlled Messages (all messages are sent to all nodes)



