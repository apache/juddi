## Using the UDDI v2 Services and Adapters

### Introduction

Starting with jUDDI version 3.2, a number of adapters are provided to help you use or access UDDI version 2 based services. There are a multitude of options and will be discussed in the following sections.

### Accessing UDDI v2 services using the jUDDI v3 Client

Accessing UDDI v2 services via the jUDDI v3 client is quite simple. All that's required is modification of the uddi.xml client configuration file. Simply set the transport to:

````
org.apache.juddi.v3.client.transport.JAXWSv2TranslationTransport
````

...and adjust the 'inquiryUrl' and 'publishUrl' URL endpoints.

TIP: When accessing UDDI v2, Custody Transfer, Subscription, Replication and Value Set APIs will not be available and may generate unexpected behavior. The UDDIv3 Inquiry getOperationalInfo method is only partially mapped.

That's it. No code changes are required other than to avoid Custody Transfer, Subscription, Replication and Value Set APIs. In addition, digital signatures are not mapped.

### Accessing UDDI v2 services using UDDI v2 APIs

The jUDDI v3 client now contains the UDDI 2 web service clients. Although, there isn't currently a configuration/transport/client/clerk wrapper for it, you can still get access to web service clients with the following code:

````
org.apache.juddi.v3.client.UDDIServiceV2 svc = new UDDIServiceV2();
Inquire port = svc.getInquire();
((BindingProvider) port).getRequestContext().\
	put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
	"http://localhost:8080/juddiv3/services/inquiryv2");
Publish pub= svc.getPublish();
((BindingProvider) pub).getRequestContext().\
	put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, 
	"http://localhost:8080/juddiv3/services/publishv2");
````

All you need to reference the following projects/jars from jUDDI:

* juddi-client
* uddi-ws

### Accessing jUDDI v3 services from an existing UDDI v2 based client, plugin or tool

When UDDI v2 was release, many application developers jumped on board to support it. As such, there are many UDDI v2 tools that exist, such as IDE plugins like Eclipse's Web Services Explorer. To support legacy tools, jUDDI now offers UDDI v2 endpoints. Simple point your tool at the following URLs. You'll have to alter them to match your environment. 

````
http://localhost:8080/juddiv3/services/inquiryv2
http://localhost:8080/juddiv3/services/publishv2
````

### Additional Information

The UDDI v2 adapters provide basic mappings from to and from UDDI v3. The juddi-client has several mapping functions that are used both client and service side to convert from UDDI v2 to UDDI v3. In addition, the client has as several interface adapters to help with a seamless transition.
