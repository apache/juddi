/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.createbulk;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.*;
import org.uddi.sub_v3.CoveragePeriod;
import org.uddi.sub_v3.GetSubscriptionResults;
import org.uddi.sub_v3.Subscription;
import org.uddi.sub_v3.SubscriptionFilter;
import org.uddi.sub_v3.SubscriptionResultsList;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;
import org.uddi.v3_service.UDDISubscriptionListenerPortType;
import org.uddi.v3_service.UDDISubscriptionPortType;

/**
 *
 * @author Alex
 */
public class UddiSubscribeValidate {

    private static UDDISecurityPortType security = null;
    private static JUDDIApiPortType juddiApi = null;
    private static UDDIPublicationPortType publish = null;
    private static UDDIInquiryPortType uddiInquiryService = null;
    private static UDDISubscriptionPortType uddiSubscriptionService = null;
    private static UDDISubscriptionListenerPortType uddiSubscriptionListenerService = null;

    public UddiSubscribeValidate() {
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
            uddiInquiryService = transport.getUDDIInquiryService();
            uddiSubscriptionService = transport.getUDDISubscriptionService();
            uddiSubscriptionListenerService = transport.getUDDISubscriptionListenerService();
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

//uddiSubscriptionListenerService.notifySubscriptionListener()

            DatatypeFactory df = DatatypeFactory.newInstance();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);
            for (int i = 0; i < 1; i++) {
                //
                GetSubscriptionResults req = new GetSubscriptionResults();
                req.setAuthInfo(rootAuthToken.getAuthInfo());
                req.setSubscriptionKey("uddi:juddi.apache.org:72619170-d391-41cb-99a0-238cb0b76eb9");
                req.setCoveragePeriod(new CoveragePeriod());
                req.getCoveragePeriod().setEndPoint(xcal);


                gcal = new GregorianCalendar();
                gcal.add(Calendar.MONTH, -1);
                req.getCoveragePeriod().setStartPoint(df.newXMLGregorianCalendar(gcal));
                SubscriptionResultsList subscriptionResults = uddiSubscriptionService.getSubscriptionResults(req);
                System.out.println("items modified: " + subscriptionResults.getBusinessDetail().getBusinessEntity().size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        UddiSubscribeValidate sp = new UddiSubscribeValidate();
        sp.publish();
    }
}
