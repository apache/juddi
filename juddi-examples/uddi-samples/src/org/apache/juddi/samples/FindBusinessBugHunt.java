/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.apache.juddi.samples;

import java.io.File;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.apache.juddi.jaxb.PrintUDDI;
import static org.apache.juddi.samples.WadlImport.properties;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl;
import org.apache.juddi.v3.client.mapping.WADL2UDDI;
import org.apache.juddi.v3.client.mappings.wadl.Application;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessInfos;
import org.uddi.api_v3.BusinessList;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.Contact;
import org.uddi.api_v3.Contacts;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.DiscoveryURL;
import org.uddi.api_v3.DiscoveryURLs;
import org.uddi.api_v3.FindBusiness;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.PersonName;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.v3_service.DispositionReportFaultMessage;
import org.uddi.v3_service.UDDIInquiryPortType;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 *
 * @author Alex O'Ree
 */
public class FindBusinessBugHunt {

    static PrintUDDI<TModel> pTModel = new PrintUDDI<TModel>();
    static Properties properties = new Properties();
    static String wsdlURL = null;
    private static UDDISecurityPortType security = null;
    private static JUDDIApiPortType juddiApi = null;
    private static UDDIPublicationPortType publish = null;
    private static UDDIInquiryPortType inquiry;

    public static void main(String[] args) throws Exception {

        // create a manager and read the config in the archive; 
        // you can use your config file name
        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
        UDDIClerk clerk = clerkManager.getClerk("default");
        // register the clerkManager with the client side container
        UDDIClientContainer.addClient(clerkManager);            // a ClerkManager can be a client to multiple UDDI nodes, so 
        // a ClerkManager can be a client to multiple UDDI nodes, so 
        // supply the nodeName (defined in your uddi.xml.
        // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
        Transport transport = clerkManager.getTransport("default");
        // Now you create a reference to the UDDI API
        security = transport.getUDDISecurityService();
        publish = transport.getUDDIPublishService();
        inquiry = transport.getUDDIInquiryService();
        //step one, get a token
        GetAuthToken getAuthTokenRoot = new GetAuthToken();
        getAuthTokenRoot.setUserID("uddi");
        getAuthTokenRoot.setCred("uddi");

        // Making API call that retrieves the authentication token for the 'root' user.
        String rootAuthToken = clerk.getAuthToken(clerk.getUDDINode().getSecurityUrl());
        String uddi = security.getAuthToken(getAuthTokenRoot).getAuthInfo();

        System.out.println("killing mary's business if it exists");
        //first check is Mary's business exists and delete
        DeleteIfExists("uddi:uddi.marypublisher.com:marybusinessone", uddi);

        System.out.println("making mary's tmodel key gen");
        //make the key gen since our test case uses some custom keys
        TModel createKeyGenator = UDDIClerk.createKeyGenator("uddi.marypublisher.com", "mary key gen", "en");
        //clerk.register(createKeyGenator);
        System.out.println("saving...");
        SaveTM(createKeyGenator, uddi);


        System.out.println("fetching business list");
        BusinessList before = getBusinessList(uddi);
        if (before.getBusinessInfos() == null) {
            System.out.println("before no businesses returned!");
            before.setBusinessInfos(new BusinessInfos());
        } else {
            System.out.println(before.getBusinessInfos().getBusinessInfo().size() + " businesses returned before");
        }

        System.out.println("saving mary");
        SaveMary(uddi);

        BusinessList after = getBusinessList(uddi);
        if (after.getBusinessInfos() == null) {
            System.out.println("after no businesses returned!");
            after.setBusinessInfos(new BusinessInfos());
        } else {
            System.out.println(after.getBusinessInfos().getBusinessInfo().size() + " businesses returned after");
        }
        PrintUDDI<BusinessList> p = new PrintUDDI<BusinessList>();
        if (before.getBusinessInfos().getBusinessInfo().size()
                < after.getBusinessInfos().getBusinessInfo().size()) {
            System.out.println("hey it worked as advertised, double checking");
            if (CheckFor(after, "uddi:uddi.marypublisher.com:marybusinessone")) {
                System.out.println("ok!");
            } else {
                System.out.println("no good!");
            }
        } else {

            System.out.println("something's not right, here's the before service listing");
            System.out.println(p.print(before));
            System.out.println(p.print(after));
        }

    }

    private static void DeleteIfExists(String key, String authInfo) {
        GetBusinessDetail gbd = new GetBusinessDetail();
        gbd.setAuthInfo(authInfo);
        gbd.getBusinessKey().add(key);
        boolean found = false;
        try {
            BusinessDetail businessDetail = inquiry.getBusinessDetail(gbd);
            if (businessDetail != null
                    && !businessDetail.getBusinessEntity().isEmpty()
                    && businessDetail.getBusinessEntity().get(0).getBusinessKey().equals(key)) {
                found = true;
            }
        } catch (Exception ex) {
        }
        if (found) {
            DeleteBusiness db = new DeleteBusiness();
            db.setAuthInfo(authInfo);
            db.getBusinessKey().add(key);
            try {
                publish.deleteBusiness(db);
            } catch (Exception ex) {
                Logger.getLogger(FindBusinessBugHunt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private static BusinessList getBusinessList(String token) throws Exception {
        FindBusiness fb = new FindBusiness();
        fb.setAuthInfo(token);
        org.uddi.api_v3.FindQualifiers fq = new org.uddi.api_v3.FindQualifiers();
        fq.getFindQualifier().add(UDDIConstants.APPROXIMATE_MATCH);
        fb.setFindQualifiers(fq);
        fb.getName().add((new Name(UDDIConstants.WILDCARD, "")));
        return inquiry.findBusiness(fb);
    }

    private static void SaveMary(String rootAuthToken) throws Exception {
        BusinessEntity be = new BusinessEntity();
        be.setBusinessKey("uddi:uddi.marypublisher.com:marybusinessone");
        be.setDiscoveryURLs(new DiscoveryURLs());
        be.getDiscoveryURLs().getDiscoveryURL().add(new DiscoveryURL("home", "http://www.marybusinessone.com"));
        be.getDiscoveryURLs().getDiscoveryURL().add(new DiscoveryURL("serviceList", "http://www.marybusinessone.com/services"));
        be.getName().add(new Name("Mary Doe Enterprises", "en"));
        be.getName().add(new Name("Maria Negocio Uno", "es"));
        be.getDescription().add(new Description("This is the description for Mary Business One.", "en"));
        be.setContacts(new Contacts());
        Contact c = new Contact();
        c.setUseType("administrator");
        c.getPersonName().add(new PersonName("Mary Doe", "en"));
        c.getPersonName().add(new PersonName("Juan Doe", "es"));
        c.getDescription().add(new Description("This is the administrator of the service offerings.", "en"));
        be.getContacts().getContact().add(c);
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(rootAuthToken);
        sb.getBusinessEntity().add(be);
        publish.saveBusiness(sb);
    }

    private static boolean CheckFor(BusinessList list, String key) {
        for (int i = 0; i < list.getBusinessInfos().getBusinessInfo().size(); i++) {
            if (list.getBusinessInfos().getBusinessInfo().get(i).getBusinessKey().equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    private static void SaveTM(TModel createKeyGenator, String uddi) throws Exception {
        SaveTModel stm = new SaveTModel();
        stm.setAuthInfo(uddi);
        stm.getTModel().add(createKeyGenator);
        publish.saveTModel(stm);
    }
}
