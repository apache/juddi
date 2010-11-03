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


package org.uddi.v3_service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;
import org.uddi.vscache_v3.ValidValue;


/**
 * This portType defines all of the UDDI value set caching operations.
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.5-b03-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "UDDI_ValueSetCaching_PortType", targetNamespace = "urn:uddi-org:v3_service")
@XmlSeeAlso({
    org.uddi.custody_v3.ObjectFactory.class,
    org.uddi.repl_v3.ObjectFactory.class,
    org.uddi.subr_v3.ObjectFactory.class,
    org.uddi.api_v3.ObjectFactory.class,
    org.uddi.vscache_v3.ObjectFactory.class,
    org.uddi.vs_v3.ObjectFactory.class,
    org.uddi.sub_v3.ObjectFactory.class,
    org.w3._2000._09.xmldsig_.ObjectFactory.class,
    org.uddi.policy_v3.ObjectFactory.class,
    org.uddi.policy_v3_instanceparms.ObjectFactory.class
})
public interface UDDIValueSetCachingPortType extends Remote {


    /**
     * 
     * @param tModelKey
     * @param authInfo
     * @param validValue
     * @param chunkToken
     * @throws DispositionReportFaultMessage
     */
    @WebMethod(operationName = "get_allValidValues", action = "get_allValidValues")
    @RequestWrapper(localName = "get_allValidValues", targetNamespace = "urn:uddi-org:vscache_v3", className = "org.uddi.vscache_v3.GetAllValidValues")
    @ResponseWrapper(localName = "validValuesList", targetNamespace = "urn:uddi-org:vscache_v3", className = "org.uddi.vscache_v3.ValidValuesList")
    public void getAllValidValues(
        @WebParam(name = "authInfo", targetNamespace = "urn:uddi-org:api_v3")
        String authInfo,
        @WebParam(name = "tModelKey", targetNamespace = "urn:uddi-org:api_v3")
        String tModelKey,
        @WebParam(name = "chunkToken", targetNamespace = "urn:uddi-org:vscache_v3", mode = WebParam.Mode.INOUT)
        Holder<String> chunkToken,
        @WebParam(name = "validValue", targetNamespace = "urn:uddi-org:vscache_v3", mode = WebParam.Mode.OUT)
        Holder<List<ValidValue>> validValue)
        throws DispositionReportFaultMessage, RemoteException
    ;

}
