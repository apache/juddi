## jUDDI Architecture

### jUDDI Server

The jUDDI Architecture leverages well known frameworks to minimize the codebase we need to maintain. The API layer uses JAX-WS, while the persistence layer uses JPA. The entire server is packages as a war archive that can be deployed to different servlet containers with minimal configuration changes. The JPA layer uses JDBC to communicate to a relational database. <<figure-jUDDIArchitecture-Architecture>> shows the different components, where the implementation providers marked with a blue dot are the implementations we use by default.

!["jUDDI Architecture Diagram"](images/jUDDIArchitecture_server.png)

#### UDDI API layer `uddi-ws` using JAX-WS

The API layer is generated from the WSDL files provided with the UDDI specification. Since the 3.2 release we support both the UDDIv2 as well as the UDDIv3 API.  The `uddi-ws` components leverages `JAX-WS` annotations to bring up the UDDIv2 and v3 Endpoints.  In addition to these two sets of SOAP based services, we also support a REST based API. The REST based API is a subset of the SOAP API. The  default JAX-WS implemention used is Apache CXF, but we also offer scripted deployments for JBossWS and Axis2. Each WebService stack relies on the web.xml as well as vendor specific configuration files. For example, CXF uses a beans.xml file in the WEB-INF directory. For more details on this see  <<GuideAdministration#HowToDeployjUDDITo>>.

The `juddi-client.jar` can be used on the client side to communicate with the API layer. The juddi-client can be configured to use either SOAP, RMI or and inVM protocol, where the inVM protocol is the most performant. For more details on the juddi-client configuration options see the Client Guide. 

#### Core UDDI `juddi-core` using JPA

The jUDDI server logic is packaged in the `juddi-core.jar`. It implements all of the server side behavior defined in the UDDI specification. For persistence it uses the Java Peristence Api (JPA). The default JPA implemenation used is OpenJPA, but Hibernate is supported as well. The configuration for JPA implementations lives in the `WEB-INF/classes/META-INF/persistence.xml` file. This file also references the datasource that is used to connect to the datasource. 

IMPORTANT: It is important to note that there are two JARs provided through maven. If you will be using Hibernate, please use the juddi-core JAR, if you are using OpenJPA, use juddi-core-openjpa.

The difference between these JARs is that the persistence classes within juddi-core-openjpa have been enhanced (http://people.apache.org/~mprudhom/openjpa/site/openjpa-project/manual/ref_guide_pc_enhance.html). Unfortunately, the Hibernate classloader does not deal well with these enhanced classes, so it it important to note not to use the juddi-core-openjpa JAR with Hibernate.

#### Relational Databases

By default we ship jUDDI preconfigured with a Java based Database called `Derby`. This database persists to the local file system, typically from where the application was started.

NOTE: To switch databases, you need to change the JDBC driver configuration in the datasource as well as the database dialect setting in the persistence.xml.

For details on switching database see the <<GuideAdministration#ConfiguringDatabaseConnections>>.

#### Servlet Containers

The jUDDI server is packaged up a WebArchive (`juddiv3.war`). This war archive can be deployed to different servlet containers with minimal configuration changes. By default we ship on Apache Tomcat but we also have scripted deployment support for GlassFish and JBoss. 

TIP: Most open source EE6 containers (JBoss, Geronimo, Glassfish) ship with jUDDI preconfigured to pass the JAXR tests in the TCK. 

When switching containers you may need to use different configuration to create a datasource. Some containers already package up a WebServices stack which can be used instead of the CXF packages up in juddiv3.war/WEB-INF/lib. In that case the number of dependent jars in the `juddiv3.war` can be reduced significantly. For details on switching containers see the <<GuideHowToDeployjUDDITo#chapter-HowToDeployjUDDITo>>.
	
	
### jUDDI GUI `juddi-gui.war`

The jUDDI GUI is also a Web Archive that is deployed along side the `juddiv3` server in the same servlet container. The GUI uses the `juddi-client` to communicate to the UDDI API Endpoints. It can use a SOAP, RMI or an inVM transport protocol, so the GUI can be deployed in a different location then the server as long as it can connect to the UDDI SOAP API.

jUDDI Client and GUI Architecture

!["jUDDI Client and GUI Diagram"](images/jUDDIArchitecture_clientAndGui.png)

The diagram above shows the admin console and the juddi-gui. Typically usage is run the admin console behind a firewall and/or restrict access to localhost connections only. The admin console interacts over a jUDDI WS API and, among other things, it can be used to create and delete publishers.

The `juddi-gui` can be configured to connect to any UDDIv2 or UDDIv3 compliant UDDI server.

jUDDI Console Architecture

!["jUDDI GUI Diagram"](images/jUDDIArchitecture_Gui.png)

You may have a jUDDI v3 Server for each type of environment (Dev, QA and Prod) and you would only need one console to connect to each one of them.

For details on using the GUI see the Client and GUI Guide .




