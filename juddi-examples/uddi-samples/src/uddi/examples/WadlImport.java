/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.examples;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClient;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl;
import org.apache.juddi.v3.client.mapping.WADL2UDDI;
import org.apache.juddi.v3.client.mappings.wadl.Application;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3_service.JUDDIApiPortType;
import org.uddi.api_v3.AuthToken;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.TModel;
import org.uddi.v3_service.UDDIPublicationPortType;
import org.uddi.v3_service.UDDISecurityPortType;

/**
 * This class shows how to perform a WSDL2UDDI import manually. More
 * specifically, this is WSDL2UDDI without using annotations.
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class WadlImport {

    static PrintUDDI<TModel> pTModel = new PrintUDDI<TModel>();
    static Properties properties = new Properties();
    static String wsdlURL = null;
    private static UDDISecurityPortType security = null;
    private static JUDDIApiPortType juddiApi = null;
    private static UDDIPublicationPortType publish = null;

    public static void main(String[] args) throws Exception {

        // create a manager and read the config in the archive; 
        // you can use your config file name
        UDDIClient clerkManager = new UDDIClient("META-INF/simple-publish-uddi.xml");
        // register the clerkManager with the client side container
        UDDIClientContainer.addClient(clerkManager);            // a ClerkManager can be a client to multiple UDDI nodes, so 
        // a ClerkManager can be a client to multiple UDDI nodes, so 
        // supply the nodeName (defined in your uddi.xml.
        // The transport can be WS, inVM, RMI etc which is defined in the uddi.xml
        Transport transport = clerkManager.getTransport("default");
        // Now you create a reference to the UDDI API
        security = transport.getUDDISecurityService();
        publish = transport.getUDDIPublishService();

        //step one, get a token
        GetAuthToken getAuthTokenRoot = new GetAuthToken();
        getAuthTokenRoot.setUserID("uddi");
        getAuthTokenRoot.setCred("uddi");

        // Making API call that retrieves the authentication token for the 'root' user.
        AuthToken rootAuthToken = null;//security.getAuthToken(getAuthTokenRoot);



        //step two, identify the key used for all your stuff
        //you must have a key generator created already
        //here, we are assuming that you don't have one
        //NOTE: these are some of the publically available WSDLs that were used to test WSDL2UDDI
        
        //publish.saveTModel(stm);


        //step three, we have two options
        //1) import the wsdl's services into a brand new business
        //2) import the wsdl's services into an existing business



        //in either case, we're going to have to parse the WSDL

        Application app = WADL2UDDI.ParseWadl(new File("diggo.wadl"));
        List<URL> urls = WADL2UDDI.GetBaseAddresses(app);
        URL url = urls.get(0);
        String domain = url.getHost();
        PrintUDDI<TModel> tmodelPrinter = new PrintUDDI<TModel>();
        TModel keygen = UDDIClerk.createKeyGenator("uddi:" + domain + ":keygenerator", domain, "en");
        //save the keygen
        SaveTModel stm = new SaveTModel();
//        stm.setAuthInfo(rootAuthToken.getAuthInfo());
        stm.getTModel().add(keygen);
        System.out.println(tmodelPrinter.print(keygen));
        
        properties.put("keyDomain", domain);
        properties.put("businessName", domain);
        properties.put("serverName", url.getHost());
        properties.put("serverPort", url.getPort());
        //wsdlURL = wsdlDefinition.getDocumentBaseURI();
        WADL2UDDI wadl2UDDI = new WADL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
        
        BusinessService businessServices = wadl2UDDI.createBusinessService(new QName("MyWasdl.namespace", "Servicename"), app);
        
        
        Set<TModel> portTypeTModels = wadl2UDDI.createWADLPortTypeTModels(wsdlURL, app);
        
       // Set<TModel> createWSDLBindingTModels = wadl2UDDI.createWSDLBindingTModels(wsdlURL, allBindings);
        //When parsing a WSDL, there's really two things going on
        //1) convert a bunch of stuff (the portTypes) to tModels
        //2) convert the service definition to a BusinessService

        //Since the service depends on the tModel, we have to save the tModels first
        SaveTModel tms = new SaveTModel();
//        tms.setAuthInfo(rootAuthToken.getAuthInfo());

        TModel[] tmodels = portTypeTModels.toArray(new TModel[0]);
        for (int i = 0; i < tmodels.length; i++) {
            System.out.println(tmodelPrinter.print(tmodels[i]));
            tms.getTModel().add(tmodels[i]);
        }

        //tmodels = createWSDLBindingTModels.toArray(new TModel[0]);
        //for (int i = 0; i < tmodels.length; i++) {
         //   System.out.println(tmodelPrinter.print(tmodels[i]));
         //   tms.getTModel().add(tmodels[i]);
        //}

        //important, you'll need to save your new tModels, or else saving the business/service may fail
        //publish.saveTModel(stm);



        //finaly, we're ready to save all of the services defined in the WSDL
        //again, we're creating a new business, if you have one already, look it up using the Inquiry getBusinessDetails

        PrintUDDI<BusinessService> servicePrinter = new PrintUDDI<BusinessService>();
        
            System.out.println(servicePrinter.print(businessServices));
        



        SaveBusiness sb = new SaveBusiness();
      //  sb.setAuthInfo(rootAuthToken.getAuthInfo());
        BusinessEntity be = new BusinessEntity();
        be.setBusinessKey(businessServices.getBusinessKey());
        be.getName().add(new Name());
        //TODO, use some relevant here
        be.getName().get(0).setValue(domain);
        be.getName().get(0).setLang("en");
        be.setBusinessServices(new BusinessServices());
        be.getBusinessServices().getBusinessService().add(businessServices);
        sb.getBusinessEntity().add(be);
        PrintUDDI<SaveBusiness> sbp = new PrintUDDI<SaveBusiness>();
        System.out.println("Request " + sbp.print(sb));
        //publish.saveBusiness(sb);

        //and we're done
        //Be sure to report any problems to the jUDDI JIRA bug tracker at 
        //https://issues.apache.org/jira/browse/JUDDI
    }
}
