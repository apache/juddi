/*
 * Copyright 2001-2008 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.v3.client;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

import org.apache.juddi.v3_service.JUDDIApiPortType;

/**
 * This call is for client side access to the JUDDI API, a web service meant for administrative functions above and beyond the
 * UDDI v3 specification.
 * 
 * @author jUDDI Team
 */
@WebServiceClient(name = "JUDDIApiService", targetNamespace = "urn:juddi-apache-org:v3_service", wsdlLocation = "classpath:/juddi_api_v1.wsdl")
public class JUDDIApiService
    extends Service
{

    private final static URL JUDDIAPISERVICE_WSDL_LOCATION;

    static {
        URL url = ClassUtil.getResource("juddi_api_v1.wsdl",JUDDIApiService.class);
        JUDDIAPISERVICE_WSDL_LOCATION = url;
    }

    public JUDDIApiService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public JUDDIApiService() {
        super(JUDDIAPISERVICE_WSDL_LOCATION, new QName("urn:juddi-apache-org:v3_service", "JUDDIApiService"));
    }

    /**
     * 
     * @return
     *     returns JUDDIApiPortType
     */
    @WebEndpoint(name = "JUDDIApiImplPort")
    public JUDDIApiPortType getJUDDIApiImplPort() {
        return super.getPort(new QName("urn:juddi-apache-org:v3_service", "JUDDIApiImplPort"), JUDDIApiPortType.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns JUDDIApiPortType
     */
    @WebEndpoint(name = "JUDDIApiImplPort")
    public JUDDIApiPortType getJUDDIApiImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("urn:juddi-apache-org:v3_service", "JUDDIApiImplPort"), JUDDIApiPortType.class, features);
    }

}
