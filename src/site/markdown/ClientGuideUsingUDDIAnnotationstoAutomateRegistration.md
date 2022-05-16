## Using UDDI Annotations

Conventionally Services (BusinessService) and their EndPoints (BindingTemplates) are registered to a UDDI Registry using a GUI, where an admin user manually adds the necessary info. This process tends to make the data in the Registry rather static and the data can grow stale over time. To make the data in the UDDI more dynamic it makes sense to register and EndPoint (BindingTemplate) when it comes online, which is when it gets deployed. The UDDI annotations are designed to just that: register a Service when it get deployed to an Application Server. There are two annotations: UDDIService, and UDDIServiceBinding. You need to use both annotations to register an EndPoint. Upon undeployment of the Service, the EndPoint will be de-registered from the UDDI. The Service information stays in the UDDI. It makes sense to leave the Service level information in the Registry since this reflects that the Service is there, however there is no EndPoint at the moment ("Check back later"). It is a manual process to remove the Service information. The annotations use the juddi-client library which means that they can be used to register to any UDDIv3 registry.

### UDDI Service Annotation

The UDDIService annotation is used to register a service under an already existing business in the Registry. The annotation should be added at the class level of the java class.

|attribute|	description|	required
| ---     | ---        | ----------- |
|serviceName|	The name of the service, by default the clerk will use the one name specified in the WebService annotation|	no
|description|	Human readable description of the service|	yes
|serviceKey|	UDDI v3 Key of the Service|	yes
|businessKey|	UDDI v3 Key of the Business that should own this Service. The business should exist in the registry at time of registration|	yes
|lang|	Language locale which will be used for the name and description, defaults to "en" if omitted|	no
|categoryBag|	Definition of a CategoryBag, see below for details|	no


### UDDIServiceBinding Annotation

The UDDIServiceBinding annotation is used to register a BindingTemplate to the UDDI registry. This annotation cannot be used by itself. It needs to go along side a UDDIService annotation.


|attribute|	description|	required
| ---     | ---        | ----------- |
|bindingKey|	UDDI v3 Key of the ServiceBinding|	yes
|description|	Human readable description of the service|	yes
||accessPointType	UDDI v3 AccessPointType, defaults to wsdlDeployment if omitted|	no
|accessPoint|	Endpoint reference|	yes
|lang|	Language locale which will be used for the name and description, defaults to "en" if omitted|	no
|tModelKeys|	Comma-separated list of tModelKeys key references|	no
|categoryBag|	Definition of a CategoryBag, see below for further details|	no

#### Java Web Service Example
The annotations can be used on any class that defines a service. Here they are added to a WebService, a POJO with a JAX-WS 'WebService' annotation.

````
package org.apache.juddi.samples;

import javax.jws.WebService;
import org.apache.juddi.v3.annotations.UDDIService;
import org.apache.juddi.v3.annotations.UDDIServiceBinding;

@UDDIService(
  businessKey="uddi:myBusinessKey",
  serviceKey="uddi:myServiceKey", 
  description = "Hello World test service")
@UDDIServiceBinding(
  bindingKey="uddi:myServiceBindingKey",
  description="WSDL endpoint for the helloWorld Service. This service is used for "
				  + "testing the jUDDI annotation functionality",
  accessPointType="wsdlDeployment",
  accessPoint="http://localhost:8080/juddiv3-samples/services/helloworld?wsdl")
@WebService(
  endpointInterface = "org.apache.juddi.samples.HelloWorld",
  serviceName = "HelloWorld")

public class HelloWorldImpl implements HelloWorld {
    public String sayHi(String text) {
        System.out.println("sayHi called");
        return "Hello " + text;
    }
}
````
On deployment of this WebService, the juddi-client code will scan this class for UDDI annotations and take care of the registration process. The configuration file uddi.xml of the juddi-client is described in the chapter, Using the jUDDI-Client. In the clerk section you need to reference the Service class 'org.apache.juddi.samples.HelloWorldImpl':
````
<clerk name="BobCratchit" node="default" publisher="sales" password="sales"> 
     <class>org.apache.juddi.samples.HelloWorldImpl</class>  
</clerk>
````
which means that Bob is using the node connection setting of the node with name "default", and that he will be using the "sales" publisher, for which the password it "sales". There is some analogy here as to how datasources are defined.

#### Wiring it all together

The mechanism that triggers the automated registration is the UDDIClient. For each class you annotation, the class needs to be listed in the jUDDI Client Configuration file. When the client reads in the configuration, it will read the uddi.xml config file for the following location:
````
client/clerks/clerk[].class
````
In addition, the following flag must be set to true.
````
client/clerks@registerOnStartup="true"
````
Here's a full example
````
<clerks registerOnStartup="false" >
    <clerk name="default" node="default" publisher="userjoe" password="******" cryptoProvider="" isPasswordEncrypted="false">
        <class>com.mybiz.services.Service1</class>
    </clerk>
</clerks>
````

The next step is to automate the "starting" and "stopping" of UDDIClient. In Java, anything that runs in a servlet container and use the following servlet class:
````
org.apache.juddi.v3.client.config.UDDIClerkServlet
````
It will automatically handle registration on start up and it will remove binding Templates on shutdown (this ensuring clients that discover the endpoint won't find a dead link).

Clients that run elsewhere simply need to "start" the UDDIClient.
````
//start up
UDDIClient clerkManager = new UDDIClient("META-INF/uddi.xml");
// register the clerkManager with the client side container
UDDIClientContainer.addClient(clerkManager); 
clerkManager.start(); //will create business/services/bindings as necessary

//shutdown down
clerkManager.stop(); //will unregister binding templates
````


### .NET Web Service Example

In .NET, the procedure is almost identical to Java. Annotate your web service classes, append the classnames to your uddi.xml client config file. .NET annotations work with any WCF, ASP.NET or any other class. 

#### Wiring it all together

In .NET, there's a few options, each with pro's and con's for automating registration.

##### Use UDDIClient in your service's constructor
Pro: It's simple
Con: Services often get multiple instances depending on the number of worker threads in the server and thus can cause some concurrency issues.

##### Use UDDIClient in Global.asa Application_Start
Pro: It's simple 
Con: You need .NET 4.0 and ASP.NET enabled in order to utilize this function

More information about this can be found here: http://weblogs.asp.net/scottgu/archive/2009/09/15/auto-start-asp-net-applications-vs-2010-and-net-4-0-series.aspx


### CategoryBag Attribute
The CategoryBag attribute allows you to reference tModels. For example the following categoryBag
````
<categoryBag>
    <keyedReference tModelKey="uddi:uddi.org:categorization:types" 
     keyName="uddi-org:types:wsdl" keyValue="wsdlDeployment" /> 
    <keyedReference tModelKey="uddi:uddi.org:categorization:types" 
     keyName="uddi-org:types:wsdl2" keyValue="wsdlDeployment2" />
</categoryBag> 
````
can be put in like
````
categoryBag="keyedReference=keyName=uddi-org:types:wsdl;keyValue=wsdlDeployment;" +
            "tModelKey=uddi:uddi.org:categorization:types," +  
                     "keyedReference=keyName=uddi-org:types:wsdl2;keyValue=wsdlDeployment2;" +
            "tModelKey=uddi:uddi.org:categorization:types2",
````

### Considerations for clustered or load balanced web servers and automated registration

Most production environments have primary and failover web servers and/or an intelligent load balancer that routers traffic to whichever server is online. When using automated registration with the jUDDI client, care must be taken when enabling automated registration.
