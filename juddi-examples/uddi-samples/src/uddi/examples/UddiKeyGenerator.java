/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.examples;

import javax.xml.bind.JAXB;
import org.apache.juddi.v3.client.config.UDDIClerk;
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
public class UddiKeyGenerator {

    private static UDDISecurityPortType security = null;
    private static JUDDIApiPortType juddiApi = null;
    private static UDDIPublicationPortType publish = null;
    private static UDDIInquiryPortType inquiry = null;

    public UddiKeyGenerator() {
        try {
            // create a manager and read the config in the archive; 
            // you can use your config file name
            UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
            // register the clerkManager with the client side container
            UDDIClientContainer.addClient(clerkManager);
            // a ClerkManager can be a client to multiple UDDI nodes, so 
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

    public void find() {
        try {
            // Setting up the values to get an authentication token for the 'root' user ('root' user has admin privileges
            // and can save other publishers).
            GetAuthToken getAuthTokenRoot = new GetAuthToken();
            getAuthTokenRoot.setUserID("uddi");
            getAuthTokenRoot.setCred("uddi");

            // Making API call that retrieves the authentication token for the 'root' user.
            AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
            System.out.println("uddi AUTHTOKEN = " + rootAuthToken.getAuthInfo());
            SaveTModel st = new SaveTModel();
            st.setAuthInfo(rootAuthToken.getAuthInfo());
            st.getTModel().add(UDDIClerk.createKeyGenator("uddi:bea.com:keygenerator", "uddi:bea.com:keygenerator", "en"));
            publish.saveTModel(st);

            //Hey! tModel Key Generators can be nested too!

            st = new SaveTModel();
            st.setAuthInfo(rootAuthToken.getAuthInfo());
            st.getTModel().add(UDDIClerk.createKeyGenator("uddi:bea.com:servicebus.default:keygenerator", "bea.com:servicebus.default", "en"));
            publish.saveTModel(st);


            // This code block proves that you can create tModels based on a nested keygen
            st = new SaveTModel();
            TModel m = new TModel();
            m.setTModelKey("uddi:bea.com:servicebus.default.proxytest2");
            m.setName(new Name("name", "lang"));
            st.setAuthInfo(rootAuthToken.getAuthInfo());
            st.getTModel().add(m);
            publish.saveTModel(st);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        UddiKeyGenerator sp = new UddiKeyGenerator();
        sp.find();
    }
}
