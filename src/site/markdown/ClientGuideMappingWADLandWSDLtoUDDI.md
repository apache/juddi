## Mapping WSDL and WSDL to UDDI

### Introduction

OASIS published a technical article which describes the recommended way to map the entries from a WSDL (Web Service Description Language) document into UDDI (https://www.oasis-open.org/committees/uddi-spec/doc/tn/uddi-spec-tc-tn-wsdl-v202-20040631.htm). The jUDDI-client provides a convenient mechanism for this mapping. In addition, the jUDDI team provides a similar API for mapping a WADL (Web Application Description Language) document to UDDI. This guide will help you use the APIs to register a WSDL or WADL document that describes a service within a UDDI registry.

### Use Case - WSDL

The most basic use case is that we have one or more SOAP/WSDL based services from a 3rd party that was just stood up on our network and we wish to now advertise to our user base that this service exists. We could manually punch in the information, but what fun is that? Let's import it using some code! This can be expanded to import services in bulk.

#### Sample Code

````
URL url = new URL("http://someURLtoYourWSDL");
ReadWSDL rw = new ReadWSDL();
Definition wsdlDefinition = rw.readWSDL(url);
Properties properties = new Properties();
properties.put("keyDomain", domain);
properties.put("businessName", domain);
properties.put("serverName", url.getHost());
properties.put("serverPort", url.getPort());
wsdlURL = wsdlDefinition.getDocumentBaseURI();
WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
//This creates a the services from WSDL
BusinessServices businessServices = wsdl2UDDI.createBusinessServices(wsdlDefinition);
//This creates the tModels from WSDL
Map<QName, PortType> portTypes = (Map<QName, PortType>) wsdlDefinition.getAllPortTypes();
//This creates more tModels from WSDL
Set<TModel> portTypeTModels = wsdl2UDDI.createWSDLPortTypeTModels(wsdlURL, portTypes);
Map allBindings = wsdlDefinition.getAllBindings();
//This creates even more tModels from WSDL
Set<TModel> createWSDLBindingTModels = wsdl2UDDI.createWSDLBindingTModels(wsdlURL, allBindin

//Now just save the tModels, then add the services to a new or existing business

````

#### Links to sample project

.SVN Links to sample projects
* http://svn.apache.org/repos/asf/juddi/trunk/juddi-examples/
* http://svn.apache.org/repos/asf/juddi/trunk/juddi-examples/wsdl2uddi/
* http://svn.apache.org/repos/asf/juddi/trunk/juddi-examples/uddi-samples/

The examples are also available in both jUDDI distributions.

### Use Case - WADL

The most basic use case is that we have one or more REST/WADL based services from a 3rd party that was just stood up on our network and we wish to now advertise to our user base that this service exists. We could manually punch in the information, but what fun is that? Let's import it using some code! This can be expanded to import services in bulk.

#### Sample Code

````
Application app = WADL2UDDI.ParseWadl(new URL("URL to WADL file"));
List<URL> urls = WADL2UDDI.GetBaseAddresses(app);
URL url = urls.get(0);
String domain = url.getHost();
Properties properties = new Properties();
properties.put("keyDomain", domain);
properties.put("businessName", domain);
properties.put("serverName", url.getHost());
properties.put("serverPort", url.getPort());
WADL2UDDI wadl2UDDI = new WADL2UDDI(null, new URLLocalizerDefaultImpl(), properties);

//creates the services
BusinessService businessServices = wadl2UDDI.createBusinessService(new QName("MyWasdl.namespace", "Servicename"), app);
//creates tModels (if any)
Set<TModel> portTypeTModels = wadl2UDDI.createWADLPortTypeTModels(wsdlURL, app);
//Now just save the tModels, then add the services to a new or existing business
````


#### Links to sample project

.SVN Links to sample projects
* http://svn.apache.org/repos/asf/juddi/trunk/juddi-examples/
* http://svn.apache.org/repos/asf/juddi/trunk/juddi-examples/uddi-samples/

The examples are also available in both jUDDI distributions.