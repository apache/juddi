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
