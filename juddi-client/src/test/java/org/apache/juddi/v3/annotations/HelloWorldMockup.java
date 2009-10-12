package org.apache.juddi.v3.annotations;

import javax.jws.WebService;

import org.apache.juddi.v3.annotations.UDDIService;
import org.apache.juddi.v3.annotations.UDDIServiceBinding;


@UDDIService(
		businessKey="uddi:juddi.apache.org:businesses-asf",
		serviceKey="uddi:juddi.apache.org:services-helloworld", 
		description = "Hello World test service")
@UDDIServiceBinding(
		bindingKey="uddi:juddi.apache.org:bindings-helloworld-wsdl",
	    description="WSDL endpoint for the HelloWorld Service. This service is used for "
				  + "testing the jUDDI annotation functionality",
		tModelKeys="tModelKey1,tModelKey2",
	    categoryBag="keyedReference=keyName=uddi-org:types:wsdl;keyValue=wsdlDeployment;tModelKey=uddi:uddi.org:categorization:types," +
	    		    "keyedReference=keyName=uddi-org:types:wsdl2;keyValue=wsdlDeployment2;tModelKey=uddi:uddi.org:categorization:types2",
	    accessPointType="wsdlDeployment",
	    accessPoint="http://${serverName}:${serverPort}/subscription-listener/helloworld")
@WebService(
		endpointInterface = "org.apache.juddi.test.HelloWorld",
        serviceName = "HelloWorld")

public class HelloWorldMockup {
    
    public String sayHi(String text) {
        System.out.println("sayHi called");
        return "Hello " + text;
    }
	
}
