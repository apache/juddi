/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.createbulk;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.crypto.DigSigUtil;
import org.apache.juddi.v3.client.transport.Transport;
import org.uddi.api_v3.*;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

public class UddiDigitalSignatureService {

    private static UDDISecurityPortType security = null;
    private static UDDIInquiryPortType inquiry = null;
    private static UDDIPublicationPortType publish = null;

    /**
     * This sets up the ws proxies using uddi.xml in META-INF
     */
    public UddiDigitalSignatureService() {
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
        UddiDigitalSignatureService sp = new UddiDigitalSignatureService();
        sp.Browse(args);
    }

    public void Browse(String[] args) {
        try {
            Properties prop = ParseArgs(args);
            if (prop.containsKey("AuthStyle")) {
                //TODO, determine a way to pass parameters from the command line, hardcoded for now
                //UDDI Token
                //HTTP Username/Password (basic or digest)
                //HTTP Client Cert
            }
            org.apache.juddi.v3.client.crypto.DigSigUtil ds = new DigSigUtil();
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE, "keystore.jks");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILETYPE, "JKS");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_FILE_PASSWORD, "password");
            ds.put(DigSigUtil.SIGNATURE_KEYSTORE_KEY_ALIAS, "selfsigned");
            ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "t");
            String token = GetAuthKey("root", "root", AuthStyle.UDDI_AUTH);
            /*BusinessList findBusiness = GetBusinessList(token);
             BusinessEntity be = GetBusinessDetails(findBusiness.getBusinessInfos().getBusinessInfo().get(0));
            
             //todo provider name
             /*ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SERIAL, "t");
             ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_SUBJECTDN, "t");
             ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_THUMBPRINT, "t");
             ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_X500_PRINICPAL, "t");

             * /
             ds.put(DigSigUtil.SIGNATURE_OPTION_CERT_INCLUSION_BASE64, "t");

             // org.apache.xml.security.utils.
             System.out.println("signing");
             BusinessEntity signUDDI_JAXBObject = ds.signUddiEntity(be);
             System.out.println("signed");
             DigSigUtil.JAXB_ToStdOut(signUDDI_JAXBObject);
             AtomicReference<String> msg = new AtomicReference<String>();
             boolean verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(signUDDI_JAXBObject, msg);
             if (verifySigned_UDDI_JAXB_Object) {
             System.out.println("signature validation passed (as expected)");
             } else {
             System.out.println("signature validation failed");
             }
             System.out.println(msg.get());
             msg = new AtomicReference<String>();
             System.out.println("signing it again for the hell of it");
             signUDDI_JAXBObject = ds.signUddiEntity(signUDDI_JAXBObject);
             DigSigUtil.JAXB_ToStdOut(signUDDI_JAXBObject);

             verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(signUDDI_JAXBObject, msg);
             if (verifySigned_UDDI_JAXB_Object) {
             System.out.println("signature validation passed (as expected)");
             } else {
             System.out.println("signature validation failed");
             }
             System.out.println(msg.get());
             msg = new AtomicReference<String>();
             System.out.println("signing");
             //start from scratcha again
             signUDDI_JAXBObject = ds.signUddiEntity(be);
             System.out.println("signed");
             //tweak it, then revalidate
             System.out.println("tweak it, then verify again");
             Description d = new Description();
             d.setValue("Hello world");
             signUDDI_JAXBObject.getDescription().add(d);
             DigSigUtil.JAXB_ToStdOut(signUDDI_JAXBObject);

             verifySigned_UDDI_JAXB_Object = ds.verifySignedUddiEntity(signUDDI_JAXBObject, msg);
             if (verifySigned_UDDI_JAXB_Object) {
             System.out.println("signature validation passed (not expected)");
             } else {
             System.out.println("signature validation failed (expected)");
             }
             System.out.println(msg.get());
             //PrintBusinessInfo(findBusiness.getBusinessInfos());
             //PrintBusinessDetails(findBusiness.getBusinessInfos(), token);
             //Pr                    intServiceDetailsByBusiness(findBusiness.getBusinessInfos(), token);
             */
            System.out.println("this is the real test =====================");

            // BusinessList findBusiness = GetBusinessList(token);

            BusinessService be = null;
            be = GetBusinessDetails();//findBusiness.getBusinessInfos().getBusinessInfo().get(0));
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

            // findBusiness = GetBusinessList(token);
            be = GetBusinessDetails();//findBusiness.getBusinessInfos().getBusinessInfo().get(0));
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

    /**
     * Find all of the registered businesses. This list may be filtered based on
     * access control rules
     *
     * @param token
     * @return
     * @throws Exception
     */
    private BusinessList GetBusinessList(String token) throws Exception {
        FindBusiness fb = new FindBusiness();
        fb.setAuthInfo(token);
        Name n = new Name();
        n.setValue("My Business 0 2013-04-11T12:40:52.753-04:00");
        fb.getName().add(n);
        org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
        fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);

        fb.setFindQualifiers(fq);
        Name searchname = new Name();
        searchname.setValue(UDDIConstants.WILDCARD);
        //fb.getName().add(searchname);
        BusinessList findBusiness = inquiry.findBusiness(fb);
        return findBusiness;

    }

    /**
     * Converts category bags of tmodels to a readable string
     *
     * @param categoryBag
     * @return
     */
    private String CatBagToString(CategoryBag categoryBag) {
        StringBuilder sb = new StringBuilder();
        if (categoryBag == null) {
            return "no data";
        }
        for (int i = 0; i < categoryBag.getKeyedReference().size(); i++) {
            sb.append(KeyedReferenceToString(categoryBag.getKeyedReference().get(i)));
        }
        for (int i = 0; i < categoryBag.getKeyedReferenceGroup().size(); i++) {
            sb.append("Key Ref Grp: TModelKey=");
            for (int k = 0; k < categoryBag.getKeyedReferenceGroup().get(i).getKeyedReference().size(); k++) {
                sb.append(KeyedReferenceToString(categoryBag.getKeyedReferenceGroup().get(i).getKeyedReference().get(k)));
            }
        }
        return sb.toString();
    }

    private String KeyedReferenceToString(KeyedReference item) {
        StringBuilder sb = new StringBuilder();
        sb.append("Key Ref: Name=").
                append(item.getKeyName()).
                append(" Value=").
                append(item.getKeyValue()).
                append(" tModel=").
                append(item.getTModelKey()).
                append(System.getProperty("line.separator"));
        return sb.toString();
    }

    private void PrintContacts(Contacts contacts) {
        if (contacts == null) {
            return;
        }
        for (int i = 0; i < contacts.getContact().size(); i++) {
            System.out.println("Contact " + i + " type:" + contacts.getContact().get(i).getUseType());
            for (int k = 0; k < contacts.getContact().get(i).getPersonName().size(); k++) {
                System.out.println("Name: " + contacts.getContact().get(i).getPersonName().get(k).getValue());
            }
            for (int k = 0; k < contacts.getContact().get(i).getEmail().size(); k++) {
                System.out.println("Email: " + contacts.getContact().get(i).getEmail().get(k).getValue());
            }
            for (int k = 0; k < contacts.getContact().get(i).getAddress().size(); k++) {
                System.out.println("Address sort code " + contacts.getContact().get(i).getAddress().get(k).getSortCode());
                System.out.println("Address use type " + contacts.getContact().get(i).getAddress().get(k).getUseType());
                System.out.println("Address tmodel key " + contacts.getContact().get(i).getAddress().get(k).getTModelKey());
                for (int x = 0; x < contacts.getContact().get(i).getAddress().get(k).getAddressLine().size(); x++) {
                    System.out.println("Address line value " + contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getValue());
                    System.out.println("Address line key name " + contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getKeyName());
                    System.out.println("Address line key value " + contacts.getContact().get(i).getAddress().get(k).getAddressLine().get(x).getKeyValue());
                }
            }
            for (int k = 0; k < contacts.getContact().get(i).getDescription().size(); k++) {
                System.out.println("Desc: " + contacts.getContact().get(i).getDescription().get(k).getValue());
            }
            for (int k = 0; k < contacts.getContact().get(i).getPhone().size(); k++) {
                System.out.println("Phone: " + contacts.getContact().get(i).getPhone().get(k).getValue());
            }
        }

    }

    private void PrintServiceDetail(BusinessService get) {
        if (get == null) {
            return;
        }
        System.out.println("Name " + ListToString(get.getName()));
        System.out.println("Desc " + ListToDescString(get.getDescription()));
        System.out.println("Key " + (get.getServiceKey()));
        System.out.println("Cat bag " + CatBagToString(get.getCategoryBag()));
        if (!get.getSignature().isEmpty()) {
            System.out.println("Item is digitally signed");
        } else {
            System.out.println("Item is not digitally signed");
        }
        PrintBindingTemplates(get.getBindingTemplates());
    }

    /**
     * This function is useful for translating UDDI's somewhat complex data
     * format to something that is more useful.
     *
     * @param bindingTemplates
     */
    private void PrintBindingTemplates(BindingTemplates bindingTemplates) {
        if (bindingTemplates == null) {
            return;
        }
        for (int i = 0; i < bindingTemplates.getBindingTemplate().size(); i++) {
            System.out.println("Binding Key: " + bindingTemplates.getBindingTemplate().get(i).getBindingKey());
            //TODO The UDDI spec is kind of strange at this point.
            //An access point could be a URL, a reference to another UDDI binding key, a hosting redirector (which is 
            //esscentially a pointer to another UDDI registry) or a WSDL Deployment
            //From an end client's perspective, all you really want is the endpoint.
            //http://uddi.org/pubs/uddi_v3.htm#_Ref8977716
            //So if you have a wsdlDeployment useType, fetch the wsdl and parse for the invocation URL
            //If its hosting director, you'll have to fetch that data from uddi recursively until the leaf node is found
            //Consult the UDDI specification for more information

            if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint() != null) {
                System.out.println("Access Point: " + bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getValue() + " type " + bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType());
                if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType() != null) {
                    if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType().equalsIgnoreCase(AccessPointType.END_POINT.toString())) {
                        System.out.println("Use this access point value as an invocation endpoint.");
                    }
                    if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType().equalsIgnoreCase(AccessPointType.BINDING_TEMPLATE.toString())) {
                        System.out.println("Use this access point value as a reference to another binding template.");
                    }
                    if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType().equalsIgnoreCase(AccessPointType.WSDL_DEPLOYMENT.toString())) {
                        System.out.println("Use this access point value as a URL to a WSDL document, which presumably will have a real access point defined.");
                    }
                    if (bindingTemplates.getBindingTemplate().get(i).getAccessPoint().getUseType().equalsIgnoreCase(AccessPointType.HOSTING_REDIRECTOR.toString())) {
                        System.out.println("Use this access point value as an Inquiry URL of another UDDI registry, look up the same binding template there (usage varies).");
                    }
                }
            }

        }
    }

    private BusinessService GetBusinessDetails() throws Exception {
        //   BusinessInfo get
        GetServiceDetail r = new GetServiceDetail();
        //GetBusinessDetail r = new GetBusinessDetail();
        r.getServiceKey().add("uddi:juddi.apache.org:da314f49-b84f-4ede-a434-0b0178632f10");
        return inquiry.getServiceDetail(r).getBusinessService().get(0);
    }

    private enum AuthStyle {

        HTTP_BASIC,
        HTTP_DIGEST,
        HTTP_NTLM,
        UDDI_AUTH,
        HTTP_CLIENT_CERT
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
    private String GetAuthKey(String username, String password, AuthStyle style) {
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

    /**
     * Converts command line args into a simple property structure
     *
     * @param args
     * @return
     */
    private Properties ParseArgs(String[] args) {

        Properties p = new Properties();
        if (args == null) {
            return p;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null && args[i].length() >= 3) {
                p.put(args[i].split("=")[0], args[i].split("=")[1]);
            }
        }
        return p;
    }

    private void PrintBusinessInfo(BusinessInfos businessInfos) {
        if (businessInfos == null) {
            System.out.println("No data returned");
        } else {
            for (int i = 0; i < businessInfos.getBusinessInfo().size(); i++) {
                System.out.println("===============================================");
                System.out.println("Business Key: " + businessInfos.getBusinessInfo().get(i).getBusinessKey());
                System.out.println("Name: " + ListToString(businessInfos.getBusinessInfo().get(i).getName()));

                System.out.println("Description: " + ListToDescString(businessInfos.getBusinessInfo().get(i).getDescription()));
                System.out.println("Services:");
                PrintServiceInfo(businessInfos.getBusinessInfo().get(i).getServiceInfos());
            }
        }
    }

    private String ListToString(List<Name> name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.size(); i++) {
            sb.append(name.get(i).getValue()).append(" ");
        }
        return sb.toString();
    }

    private String ListToDescString(List<Description> name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.size(); i++) {
            sb.append(name.get(i).getValue()).append(" ");
        }
        return sb.toString();
    }

    private void PrintServiceInfo(ServiceInfos serviceInfos) {
        for (int i = 0; i < serviceInfos.getServiceInfo().size(); i++) {
            System.out.println("-------------------------------------------");
            System.out.println("Service Key: " + serviceInfos.getServiceInfo().get(i).getServiceKey());
            System.out.println("Owning Business Key: " + serviceInfos.getServiceInfo().get(i).getBusinessKey());
            System.out.println("Name: " + ListToString(serviceInfos.getServiceInfo().get(i).getName()));
        }
    }

    private void PrintBusinessDetails(BusinessInfos businessInfos, String token) throws Exception {
        GetBusinessDetail gbd = new GetBusinessDetail();
        gbd.setAuthInfo(token);
        for (int i = 0; i < businessInfos.getBusinessInfo().size(); i++) {
            gbd.getBusinessKey().add(businessInfos.getBusinessInfo().get(i).getBusinessKey());
        }
        BusinessDetail businessDetail = inquiry.getBusinessDetail(gbd);
        for (int i = 0; i < businessDetail.getBusinessEntity().size(); i++) {
            System.out.println("Business Detail - key: " + businessDetail.getBusinessEntity().get(i).getBusinessKey());
            System.out.println("Name: " + ListToString(businessDetail.getBusinessEntity().get(i).getName()));
            System.out.println("CategoryBag: " + CatBagToString(businessDetail.getBusinessEntity().get(i).getCategoryBag()));
            PrintContacts(businessDetail.getBusinessEntity().get(i).getContacts());
        }
    }

    private void PrintServiceDetailsByBusiness(BusinessInfos businessInfos, String token) throws Exception {
        for (int i = 0; i < businessInfos.getBusinessInfo().size(); i++) {
            GetServiceDetail gsd = new GetServiceDetail();
            for (int k = 0; k < businessInfos.getBusinessInfo().get(i).getServiceInfos().getServiceInfo().size(); k++) {
                gsd.getServiceKey().add(businessInfos.getBusinessInfo().get(i).getServiceInfos().getServiceInfo().get(k).getServiceKey());
            }
            gsd.setAuthInfo(token);
            System.out.println("Fetching data for business " + businessInfos.getBusinessInfo().get(i).getBusinessKey());
            ServiceDetail serviceDetail = inquiry.getServiceDetail(gsd);
            for (int k = 0; k < serviceDetail.getBusinessService().size(); k++) {
                PrintServiceDetail(serviceDetail.getBusinessService().get(k));
            }
            System.out.println("................");

        }
    }
}
