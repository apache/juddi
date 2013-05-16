/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uddi.createbulk;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIClerkManager;
import org.apache.juddi.v3.client.config.UDDIClientContainer;
import org.apache.juddi.v3.client.mapping.ReadWSDL;
import org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl;
import org.apache.juddi.v3.client.mapping.WSDL2UDDI;
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
 *
 * @author Alex O'Ree
 */
public class WsdlImport {

    static PrintUDDI<TModel> pTModel = new PrintUDDI<TModel>();
    static Properties properties = new Properties();
    static String wsdlURL = null;
    private static UDDISecurityPortType security = null;
    private static JUDDIApiPortType juddiApi = null;
    private static UDDIPublicationPortType publish = null;

    public static void main(String[] args) throws Exception {

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
        publish = transport.getUDDIPublishService();

        //step one, get a token
        GetAuthToken getAuthTokenRoot = new GetAuthToken();
        getAuthTokenRoot.setUserID("uddi");
        getAuthTokenRoot.setCred("uddi");

        // Making API call that retrieves the authentication token for the 'root' user.
        AuthToken rootAuthToken = security.getAuthToken(getAuthTokenRoot);



        //step two, identify the key used for all your stuff
        //you must have a key generator created already
        //here, we are assuming that you don't have one
        URL url = new URL("http://wsf.cdyne.com/WeatherWS/Weather.asmx?WSDL");
        //http://www.bccs.uni.no/~pve002/wsdls/ebi-mafft.wsdl");
        //http://www.webservicex.net/GenericNAICS.asmx?WSDL");
//http://www.webservicex.net/stockquote.asmx?WSDL");
        //http://www.webservicex.com/globalweather.asmx?WSDL");
        //http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl");
        String domain = url.getHost();
        PrintUDDI<TModel> tmodelPrinter = new PrintUDDI<TModel>();
        TModel keygen = UDDIClerk.createKeyGenator("uddi:" + domain + ":keygenerator", domain, "en");
        //save the keygen
        SaveTModel stm = new SaveTModel();
        stm.setAuthInfo(rootAuthToken.getAuthInfo());
        stm.getTModel().add(keygen);
        System.out.println(tmodelPrinter.print(keygen));
        //publish.saveTModel(stm);


        //step three, we have two options
        //1) import the wsdl's services into a brand new business
        //2) import the wsdl's services into an existing business



        //in either case, we're going to have to parse the WSDL

        ReadWSDL rw = new ReadWSDL();
        Definition wsdlDefinition = rw.readWSDL(url);

        properties.put("keyDomain", domain);
        properties.put("businessName", domain);
        properties.put("serverName", url.getHost());
        properties.put("serverPort", url.getPort());
        wsdlURL = wsdlDefinition.getDocumentBaseURI();
        WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
        BusinessServices businessServices = wsdl2UDDI.createBusinessServices(wsdlDefinition);
        @SuppressWarnings("unchecked")
        Map<QName, PortType> portTypes = (Map<QName, PortType>) wsdlDefinition.getAllPortTypes();
        Set<TModel> portTypeTModels = wsdl2UDDI.createWSDLPortTypeTModels(wsdlURL, portTypes);
        Map allBindings = wsdlDefinition.getAllBindings();
        Set<TModel> createWSDLBindingTModels = wsdl2UDDI.createWSDLBindingTModels(wsdlURL, allBindings);
        //When parsing a WSDL, there's really two things going on
        //1) convert a bunch of stuff (the portTypes) to tModels
        //2) convert the service definition to a BusinessService

        //Since the service depends on the tModel, we have to save the tModels first
        SaveTModel tms = new SaveTModel();
        tms.setAuthInfo(rootAuthToken.getAuthInfo());

        TModel[] tmodels = portTypeTModels.toArray(new TModel[0]);
        for (int i = 0; i < tmodels.length; i++) {
            System.out.println(tmodelPrinter.print(tmodels[i]));
            tms.getTModel().add(tmodels[i]);
        }

        tmodels = createWSDLBindingTModels.toArray(new TModel[0]);
        for (int i = 0; i < tmodels.length; i++) {
            System.out.println(tmodelPrinter.print(tmodels[i]));
            tms.getTModel().add(tmodels[i]);
        }

        //publish.saveTModel(stm);



        //finaly, we're ready to save all of the services defined in the WSDL
        //again, we're creating a new business, if you have one already, look it up using the Inquiry getBusinessDetails

        PrintUDDI<BusinessService> servicePrinter = new PrintUDDI<BusinessService>();
        for (int i = 0; i < businessServices.getBusinessService().size(); i++) {
            System.out.println(servicePrinter.print(businessServices.getBusinessService().get(i)));
        }



        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(rootAuthToken.getAuthInfo());
        BusinessEntity be = new BusinessEntity();
        be.setBusinessKey(businessServices.getBusinessService().get(0).getBusinessKey());
        be.getName().add(new Name());
        be.getName().get(0).setValue(domain);
        be.getName().get(0).setLang("en");
        be.setBusinessServices(businessServices);
        sb.getBusinessEntity().add(be);
        PrintUDDI<SaveBusiness> sbp = new PrintUDDI<SaveBusiness>();
        System.out.println("Request " + sbp.print(sb));
       // publish.saveBusiness(sb);

        //and we're done

        //Be sure to report any problems to the jUDDI JIRA bug tracker at 
        //https://issues.apache.org/jira/browse/JUDDI
    }

    private static void StripKeys(BusinessServices businessServices) {

        for (int i = 0; i < businessServices.getBusinessService().size(); i++) {
            businessServices.getBusinessService().get(i).setBusinessKey(null);
            for (int k = 0; k < businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().size(); k++) {
                businessServices.getBusinessService().get(i).getBindingTemplates().getBindingTemplate().get(k).setBindingKey(null);
            }
        }
    }

    private void someting() throws Exception{
        URL url = new URL("http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl");
        String domain = url.getHost();
        ReadWSDL rw = new ReadWSDL();
        Definition wsdlDefinition = rw.readWSDL(url);
        properties.put("keyDomain", domain);
        properties.put("businessName", domain);
        properties.put("serverName", url.getHost());
        properties.put("serverPort", url.getPort());
        wsdlURL = wsdlDefinition.getDocumentBaseURI();
        WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
        BusinessServices businessServices = wsdl2UDDI.createBusinessServices(wsdlDefinition);
        Map<QName, PortType> portTypes = (Map<QName, PortType>) wsdlDefinition.getAllPortTypes();
        Set<TModel> portTypeTModels = wsdl2UDDI.createWSDLPortTypeTModels(wsdlURL, portTypes);
        Map allBindings = wsdlDefinition.getAllBindings();
        Set<TModel> createWSDLBindingTModels = wsdl2UDDI.createWSDLBindingTModels(wsdlURL, allBindings);
    }
}
