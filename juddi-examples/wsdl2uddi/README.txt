This example is a command line demonstration of how to interact with JUDDI.

1. Start the jUDDI-server (juddi-tomcat or juddi-bundle)

2. Check the settings of the META-INF/wsdl2uddi-uddi.xml, to make sure the serverName and serverPort are set correctly.

3. Create Joe Publisher and his keyGenerator and then register the services in wsdl/helloworld.wsdl to jUDDI.
it creates a businessEntity with businessKey 'uddi:uddi.joepublisher.com:business-for-wsdl'

mvn -Ppublish test

You should see the following output being written to the console:

[INFO] --- exec-maven-plugin:1.1.1:java (default) @ wsdl2uddi ---
May 08, 2013 12:14:41 PM org.apache.juddi.v3.client.config.ClientConfig loadConfiguration
INFO: Reading UDDI Client properties file file:/Users/kstam/osc/apache/dev/juddi-patch/juddi-examples/wsdl2uddi/target/classes/META-INF/wsdl2uddi-uddi.xml
root AUTHTOKEN = authtoken:459d0ac2-cb6c-4dde-813f-1ddba4b99f20
May 08, 2013 12:14:48 PM org.apache.juddi.v3.client.config.UDDIClerk register
INFO: Registering tModel with key uddi:uddi.joepublisher.com:keygenerator
May 08, 2013 12:14:49 PM org.apache.juddi.v3.client.config.UDDIClerk register
INFO: Registering business WSDL-Business with key uddi:uddi.joepublisher.com:business-for-wsdl
Retrieving document at 'file:/Users/kstam/osc/apache/dev/juddi-patch/juddi-examples/wsdl2uddi/target/classes/wsdl/helloworld.wsdl'.
May 08, 2013 12:14:49 PM org.uddi.JAXBContextUtil getContext
INFO: Creating JAXB Context for org.uddi.api_v3
May 08, 2013 12:14:49 PM org.apache.juddi.v3.client.config.UDDIClerk checkForErrorInDispositionReport
INFO: entityKey uddi:uddi.joepublisher.com:service_helloworld was not found in the registry
May 08, 2013 12:14:49 PM org.apache.juddi.v3.client.config.UDDIClerk register
INFO: Registering tModel with key uddi:uddi.joepublisher.com:HelloWorld
May 08, 2013 12:14:49 PM org.apache.juddi.v3.client.config.UDDIClerk register
INFO: Registering tModel with key uddi:uddi.joepublisher.com:HelloWorldSoapBinding
May 08, 2013 12:14:49 PM org.apache.juddi.v3.client.config.UDDIClerk register
INFO: Registering service HelloWorld with key uddi:uddi.joepublisher.com:service_helloworld
May 08, 2013 12:14:49 PM org.apache.juddi.v3.client.config.UDDIClerk register
INFO: Registering bindingTemplate with key uddi:uddi.joepublisher.com:binding_localhost_helloworld_helloworldimplport_8080
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------

4. Check that we can find this business in the registry by running from Queries

mvn -Pfind test

[INFO] --- exec-maven-plugin:1.1.1:java (default) @ wsdl2uddi ---
May 08, 2013 12:18:14 PM org.apache.juddi.v3.client.config.ClientConfig loadConfiguration
INFO: Reading UDDI Client properties file file:/Users/kstam/osc/apache/dev/juddi-patch/juddi-examples/wsdl2uddi/target/classes/META-INF/wsdl2uddi-uddi.xml
Found business with name WSDL-Business
Number of services: 1
Service Name        = 'HelloWorld'
Service Key         = 'uddi:uddi.joepublisher.com:service_helloworld'
Service Description = 'The Hello World Service registered using WSDL2UDDI'
BindingTemplates: 1
--BindingTemplate 0:
  bindingKey          = uddi:uddi.joepublisher.com:binding_localhost_helloworld_helloworldimplport_8080
  accessPoint useType = endPoint
  accessPoint value   = http://localhost:8080/uddi-annotations/services/helloworld
  description         = The Hello World Binding registered using WSDL2UDDI
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------

So we got one Service, which one binding. Note that the descriptions were provided in the wsdl file
using <wsdl:documentation> elements.

5. Finally to remove all data structures call 

mvn -Pdelete test

This cleans up the business and the wsdl2uddi data structures.

