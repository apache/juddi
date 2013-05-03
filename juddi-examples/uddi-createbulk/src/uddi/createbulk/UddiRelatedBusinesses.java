/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.createbulk;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Holder;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex
 */
public class UddiRelatedBusinesses {

    private static UDDISecurityPortType security = null;
    private static JUDDIApiPortType juddiApi = null;
    private static UDDIPublicationPortType publish = null;

    public UddiRelatedBusinesses() {
        try {
            // create a manager and read the config in the archive; 
            // you can use your config file name
            UDDIClerkManager clerkManager = new UDDIClerkManager("META-INF/simple-publish-uddi.xml");
            // register the clerkManager with the client side container
            UDDIClientContainer.addClerkManager(clerkManager);
            // a ClerkManager can be a client to multiple UDDI nodes, so 
            // supply the nodeName (defined in your uddi.xml.
            // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
            Transport transport = clerkManager.getTransport("default");
            // Now you create a reference to the UDDI API
            security = transport.getUDDISecurityService();
            juddiApi = transport.getJUDDIApiService();
            publish = transport.getUDDIPublishService();
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



            DatatypeFactory df = DatatypeFactory.newInstance();
            GregorianCalendar gcal = new GregorianCalendar();
            gcal.setTimeInMillis(System.currentTimeMillis());
            XMLGregorianCalendar xcal = df.newXMLGregorianCalendar(gcal);
            for (int i = 0; i < 1; i++) {
                // Creating the parent business entity that will contain our service.
                BusinessEntity myBusEntity = new BusinessEntity();
                Name myBusName = new Name();
                myBusName.setLang("en");
                myBusName.setValue("My Business Dept 1" + " " + xcal.toString());
                myBusEntity.getName().add(myBusName);

                // Adding the business entity to the "save" structure, using our publisher's authentication info and saving away.
                SaveBusiness sb = new SaveBusiness();
                sb.getBusinessEntity().add(myBusEntity);
                sb.setAuthInfo(rootAuthToken.getAuthInfo());
                BusinessDetail bd = publish.saveBusiness(sb);
                String myBusKey1 = bd.getBusinessEntity().get(0).getBusinessKey();
                System.out.println("myBusiness key:  " + myBusKey1);


                myBusEntity = new BusinessEntity();
                myBusName = new Name();
                myBusName.setLang("en");
                myBusName.setValue("My Business Dept 2" + " " + xcal.toString());
                myBusEntity.getName().add(myBusName);

                // Adding the business entity to the "save" structure, using our publisher's authentication info and saving away.
                sb = new SaveBusiness();
                sb.getBusinessEntity().add(myBusEntity);
                sb.setAuthInfo(rootAuthToken.getAuthInfo());
                bd = publish.saveBusiness(sb);
                String myBusKey2 = bd.getBusinessEntity().get(0).getBusinessKey();
                System.out.println("myBusiness key:  " + myBusKey2);

                Holder<List<PublisherAssertion>> x = new Holder<List<PublisherAssertion>>();
                PublisherAssertion pa = new PublisherAssertion();
                pa.setFromKey(myBusKey2);
                pa.setToKey(myBusKey1);
                pa.setKeyedReference(new KeyedReference());
                pa.getKeyedReference().setKeyName("Subsidiary");
                pa.getKeyedReference().setKeyValue("parent-child");
                
                pa.getKeyedReference().setTModelKey("uddi:uddi.org:relationships");
                x.value = new ArrayList<PublisherAssertion>();
                x.value.add(pa);
                publish.setPublisherAssertions(rootAuthToken.getAuthInfo(), x);
                
                
                x = new Holder<List<PublisherAssertion>>();
                pa = new PublisherAssertion();
                pa.setFromKey(myBusKey1);
                pa.setToKey(myBusKey2);
                pa.setKeyedReference(new KeyedReference());
                pa.getKeyedReference().setKeyName("Subsidiary");
                pa.getKeyedReference().setKeyValue("parent-child");
                
                pa.getKeyedReference().setTModelKey("uddi:uddi.org:relationships");
                x.value = new ArrayList<PublisherAssertion>();
                x.value.add(pa);
                publish.setPublisherAssertions(rootAuthToken.getAuthInfo(), x);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        UddiRelatedBusinesses sp = new UddiRelatedBusinesses();
        sp.publish();
    }
}
