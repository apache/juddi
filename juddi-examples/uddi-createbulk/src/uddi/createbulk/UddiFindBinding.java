/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.createbulk;

import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex
 */
public class UddiFindBinding {

    private static UDDISecurityPortType security = null;
    private static JUDDIApiPortType juddiApi = null;
    private static UDDIPublicationPortType publish = null;
    private static UDDIInquiryPortType inquiry = null;

    public UddiFindBinding() {
        try {
            // create a manager and read the config in the archive; 
            // you can use your config file name
            UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
            // register the clerkManager with the client side container
            UDDIClientContainer.addClient(clerkManager);            // a ClerkManager can be a client to multiple UDDI nodes, so 
            // supply the nodeName (defined in your uddi.xml.
            // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
            Transport transport = clerkManager.getTransport("default");
            // Now you create a reference to the UDDI API
            security = transport.getUDDISecurityService();
            juddiApi = transport.getJUDDIApiService();
            publish = transport.getUDDIPublishService();
            inquiry = transport.getUDDIInquiryService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish() {
        try {
            // Setting up the values to get an authentication token for the 'root' user ('root' user has admin privileges
            // and can save other publishers).
            GetAuthToken getAuthTokenRoot = new GetAuthToken();
            getAuthTokenRoot.setUserID("root");
            getAuthTokenRoot.setCred("root");

            // Making API call that retrieves the authentication token for the 'root' user.
            AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
            System.out.println("root AUTHTOKEN = " + rootAuthToken.getAuthInfo());

            FindService fs = new FindService();
            fs.getName().add(new Name());
            fs.getName().get(0).setValue("%");
            fs.setFindQualifiers(new FindQualifiers());
            fs.getFindQualifiers().getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);

            ServiceList findService = inquiry.findService(fs);
            System.out.println(findService.getServiceInfos().getServiceInfo().size());
            GetServiceDetail gs = new GetServiceDetail();
            for (int i = 0; i < findService.getServiceInfos().getServiceInfo().size(); i++) {
                gs.getServiceKey().add(findService.getServiceInfos().getServiceInfo().get(i).getServiceKey());
            }

            ServiceDetail serviceDetail = inquiry.getServiceDetail(gs);
            for (int i = 0; i < serviceDetail.getBusinessService().size(); i++) {
                System.out.println(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size());
                for (int k = 0; k < serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                    System.out.println(serviceDetail.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).getAccessPoint().getValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        UddiFindBinding sp = new UddiFindBinding();
        sp.publish();
    }
}
