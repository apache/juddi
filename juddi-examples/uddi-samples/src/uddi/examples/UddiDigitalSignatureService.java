/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.examples;

import java.util.concurrent.atomic.AtomicReference;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.crypto.DigSigUtil;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This class shows you how to digitally sign a service and verify the signature
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class UddiDigitalSignatureService {

    private static UDDISecurityPortType security = null;
    private static UDDIInquiryPortType inquiry = null;
    private static UDDIPublicationPortType publish = null;
    private static UDDIClient clerkManager = null;

    /**
     * This sets up the ws proxies using uddi.xml in META-INF
     */
    public UddiDigitalSignatureService() {
        try {
            // create a manager and read the config in the archive; 
            // you can use your config file name
            clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
            // register the clerkManager with the client side container
            UDDIClientContainer.addClient(clerkManager);            // a ClerkManager can be a client to multiple UDDI nodes, so 
            // supply the nodeName (defined in your uddi.xml.
            // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
            Transport transport = clerkManager.getTransport("default");
            // Now you create a reference to the UDDI API
            security = transport.getUDDISecurityService();
            inquiry = transport.getUDDIInquiryService();
            publish = transport.getUDDIPublishService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main entry point
     *
     * @param args
     */
    public static void main(String args[]) {

        UddiDigitalSignatureService sp = new UddiDigitalSignatureService();
        sp.Fire(args);
    }

    public void Fire(String[] args) {
        try {

            org.apache.juddi.v3.client.crypto.DigSigUtil ds = null;

            //option 1), set everything manually
            ds = new DigSigUtil();
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE, "keystore.jks");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE, "JKS");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, "password");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS, "selfsigned");
            ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "t");

            //option 2), load it from the juddi config file
            ds = new DigSigUtil(clerkManager.getClientConfig().getDigitalSignatureConfiguration());

            //login
            String token = null;
            //option, load from juddi config
            token = GetAuthKey(clerkManager.getClerk("default").getPublisher(),
                    clerkManager.getClerk("default").getPassword());

            //TODO replace this with something more useful
            String key = "uddi:juddi.apache.org:da314f49-b84f-4ede-a434-0b0178632f10";
            BusinessService be = null;
            be = GetServiceDetails(key);
            be.getSignature().clear();
            //DigSigUtil.JAXB_ToStdOut(be);
            System.out.println("signing");
            BusinessService signUDDI_JAXBObject = ds.signUddiEntity(be);
            DigSigUtil.JAXB_ToStdOut(signUDDI_JAXBObject);
            System.out.println("signed, saving");

            SaveService sb = new SaveService();
            sb.setAuthInfo(token);
            sb.getBusinessService().add(signUDDI_JAXBObject);
            publish.saveService(sb);
            System.out.println("saved, fetching");


            be = GetServiceDetails(key);
            DigSigUtil.JAXB_ToStdOut(be);
            System.out.println("verifing");
            AtomicReference<String> msg = new AtomicReference<String>();
            boolean verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(be, msg);
            if (verifySigned_UDDI_JAXB_Object) {
                System.out.println("signature validation passed (expected)");
            } else {
                System.out.println("signature validation failed (not expected)");
            }
            System.out.println(msg.get());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BusinessService GetServiceDetails(String key) throws Exception {
        //   BusinessInfo get
        GetServiceDetail r = new GetServiceDetail();
        //GetBusinessDetail r = new GetBusinessDetail();
        r.getServiceKey().add(key);
        return inquiry.getServiceDetail(r).getBusinessService().get(0);
    }

    /**
     * Gets a UDDI style auth token, otherwise, appends credentials to the ws
     * proxies (not yet implemented)
     *
     * @param username
     * @param password
     * @param style
     * @return
     */
    private String GetAuthKey(String username, String password) {
        try {

            GetAuthToken getAuthTokenRoot = new GetAuthToken();
            getAuthTokenRoot.setUserID(username);
            getAuthTokenRoot.setCred(password);

            // Making API call that retrieves the authentication token for the 'root' user.
            AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);
            System.out.println("root AUTHTOKEN = " + rootAuthToken.getAuthInfo());
            return rootAuthToken.getAuthInfo();
        } catch (Exception ex) {
            System.out.println("Could not authenticate with the provided credentials " + ex.getMessage());
        }
        return null;
    }
}
