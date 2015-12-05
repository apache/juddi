/*
 * Copyright 2015 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.client.config.model;

import java.text.SimpleDateFormat;
import javax.jws.WebService;
import org.apache.juddi.v3.annotations.UDDIService;
import org.apache.juddi.v3.annotations.UDDIServiceBinding;

/**
 *
 * @author alex
 */
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
public class JUDDI_943_testService implements JUDDI_943_testInterface{

     static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
     @Override
     public String sayHell(String name) {
          return "Hi " + name + "! It is now " ;
     }
     
}
