/*
 * Copyright 2001-2013 The Apache Software Foundation.
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
package org.apache.juddi.samples;

import java.rmi.RemoteException;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Endpoint;
import org.uddi.api_v3.DispositionReport;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * this class is a very basic callback service for UDDI. Fire it up with the
 * EndPoint api
 *
 * @see Endpoint
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
@WebService(serviceName = "UDDISubscriptionListenerService",
        endpointInterface = "org.uddi.v3_service.UDDISubscriptionListenerPortType",
        targetNamespace = "urn:uddi-org:v3_service")
@XmlSeeAlso({org.uddi.subr_v3.NotifySubscriptionListener.class, DispositionReportFaultMessage.class, DispositionReport.class})
public class ClientSubscriptionCallback implements org.uddi.v3_service.UDDISubscriptionListenerPortType {

    UddiSubscribe ref = null;

    public ClientSubscriptionCallback(UddiSubscribe callback) {
        ref = callback;
    }
    Object j;

    @Override
    public DispositionReport notifySubscriptionListener(NotifySubscriptionListener nl) throws DispositionReportFaultMessage, RemoteException {
        DispositionReport r = new DispositionReport();
        System.out.println("call back recieved.");
        System.out.println(org.apache.juddi.jaxb.JAXBMarshaller.marshallToString(nl, org.apache.juddi.jaxb.JAXBMarshaller.PACKAGE_SUBSCR_RES));
        ref.callbackRecieved = true;
        return r;
    }
}
