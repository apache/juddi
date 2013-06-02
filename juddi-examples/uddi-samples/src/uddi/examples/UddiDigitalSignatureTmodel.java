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
 * This class shows you how to digitally sign a tmodel and verify the signature
 * @author Alex O'Ree
 */
public class UddiDigitalSignatureTmodel {

    private static UDDISecurityPortType security = null;
    private static UDDIInquiryPortType inquiry = null;
    private static UDDIPublicationPortType publish = null;

    /**
     * This sets up the ws proxies using uddi.xml in META-INF
     */
    public UddiDigitalSignatureTmodel() {
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
            inquiry = transport.getUDDIInquiryService();
            publish = transport.getUDDIPublishService();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void DisplayHelp() {
        //TODO
    }

    /**
     * Main entry point
     *
     * @param args
     */
    public static void main(String args[]) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            DisplayHelp();
            return;
        }
        UddiDigitalSignatureTmodel sp = new UddiDigitalSignatureTmodel();
        sp.Fire(args);
    }

    public void Fire(String[] args) {
        try {
            org.apache.juddi.v3.client.crypto.DigSigUtil ds = new DigSigUtil();
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE, "keystore.jks");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE, "JKS");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, "password");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS, "selfsigned");
            
            //obmit this statement if you don't need the certificate to be included.
            ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "t");
            String token = GetAuthKey("root", "root");
            
            String key ="uddi:juddi.apache.org:23748881-bb2f-4896-8283-4a15be1d0bc1";
            
            
            TModel be = GetTmodelDetails(key);
            be.getSignature().clear();
            //DigSigUtil.JAXB_ToStdOut(be);
            System.out.println("signing");
            TModel signUDDI_JAXBObject = ds.signUddiEntity(be);
            DigSigUtil.JAXB_ToStdOut(signUDDI_JAXBObject);
            System.out.println("signed, saving");

            SaveTModel sb = new SaveTModel();
            sb.setAuthInfo(token);
            sb.getTModel().add(signUDDI_JAXBObject);
            publish.saveTModel(sb);
            System.out.println("saved, fetching");


            be = GetTmodelDetails(key);
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

    
    private TModel GetTmodelDetails(String key) throws Exception {
        //   BusinessInfo get
        GetTModelDetail r = new GetTModelDetail();
        r.getTModelKey().add(key);
        return inquiry.getTModelDetail(r).getTModel().get(0);
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
