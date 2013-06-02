/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uddi.examples;

import java.rmi.RemoteException;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Endpoint;
import org.uddi.api_v3.DispositionReport;
import org.uddi.subr_v3.NotifySubscriptionListener;
import org.uddi.v3_service.DispositionReportFaultMessage;

/**
 * this class is a very basic callback service for UDDI.
 * Fire it up with the EndPoint api
 * @see  Endpoint
 * @author Alex O'Ree
 */
@WebService(serviceName="UDDISubscriptionListenerService", 
			endpointInterface="org.uddi.v3_service.UDDISubscriptionListenerPortType",
			targetNamespace = "urn:uddi-org:v3_service")
@XmlSeeAlso({org.uddi.subr_v3.NotifySubscriptionListener.class,DispositionReportFaultMessage.class, DispositionReport.class})
public class ClientSubscriptionCallback implements org.uddi.v3_service.UDDISubscriptionListenerPortType{
    
    UddiSubscribe ref=null;
    public ClientSubscriptionCallback(UddiSubscribe callback){
        ref = callback;
    }
    Object j;
    @Override
    public DispositionReport notifySubscriptionListener(NotifySubscriptionListener nl) throws DispositionReportFaultMessage, RemoteException {
        DispositionReport r = new DispositionReport();
        System.out.println("call back recieved.");
        System.out.println(org.apache.juddi.jaxb.JAXBMarshaller.marshallToString(nl, org.apache.juddi.jaxb.JAXBMarshaller.PACKAGE_SUBSCR_RES));
        ref.callbackRecieved=true;
        return r;
    }

}
