/*
 * Copyright 2013 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.juddi.v3.client.mapping.wadl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.Property;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIKeyConvention;
import org.apache.juddi.v3.client.mapping.Common2UDDI;
import org.apache.juddi.v3.client.mapping.MockSSLSocketFactory;
import org.apache.juddi.v3.client.mapping.URLLocalizer;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;

/**
 * This class converts a WADL document, web application description language into a
 * structure that more or less works within the UDDI data structures.<br><br>
 * <h1>Example Usage Scenario</h1>
 * <pre>
        Application app = WADL2UDDI.parseWadl(new File("A path to your file.wadl"));
        List<URL> urls = WADL2UDDI.getBaseAddresses(app);
        URL url = urls.get(0);
        String domain = url.getHost();
        TModel keygen = UDDIClerk.createKeyGenator("uddi:" + domain + ":keygenerator", domain, "en");
        //save the keygen
        SaveTModel stm = new SaveTModel();
        stm.setAuthInfo(rootAuthToken.getAuthInfo());
        stm.getTModel().add(keygen);
        
        properties.put("keyDomain", domain);
        properties.put("businessName", domain);
        properties.put("serverName", url.getHost());
        properties.put("serverPort", url.getPort());
        WADL2UDDI wadl2UDDI = new WADL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
       
        BusinessService businessServices = wadl2UDDI.createBusinessService(new QName("MyWasdl.namespace", "Servicename"), app);
        Set<TModel> portTypeTModels = wadl2UDDI.createWADLPortTypeTModels(wsdlURL, app);
        
        //Since the service depends on the tModel, we have to save the tModels first
        SaveTModel tms = new SaveTModel();

        TModel[] tmodels = portTypeTModels.toArray(new TModel[0]);
        for (int i = 0; i < tmodels.length; i++) {
            System.out.println(tmodelPrinter.print(tmodels[i]));
            tms.getTModel().add(tmodels[i]);
        }


        //important, you'll need to save your new tModels, or else saving the business/service may fail
        publish.saveTModel(stm);

        //finaly, we're ready to save all of the services defined in the WSDL
        //again, we're creating a new business, if you have one already, look it up using the Inquiry getBusinessDetails

        PrintUDDI<BusinessService> servicePrinter = new PrintUDDI<BusinessService>();
        System.out.println(servicePrinter.print(businessServices));
        
        SaveBusiness sb = new SaveBusiness();
        sb.setAuthInfo(rootAuthToken.getAuthInfo());
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
        publish.saveBusiness(sb);

        //and we're done
        //Be sure to report any problems to the jUDDI JIRA bug tracker at 
        //https://issues.apache.org/jira/browse/JUDDI
</pre>
 *
 * @author <a href="mailto:alexoree@apache.org">Alex O'Ree</a>
 */
public class WADL2UDDI {

    private static Log log = LogFactory.getLog(WADL2UDDI.class);
    private String keyDomainURI;
    private String businessKey;
    private String lang;
    private UDDIClerk clerk = null;
    private Properties properties = null;
    private URLLocalizer urlLocalizer;

    public WADL2UDDI(UDDIClerk clerk, URLLocalizer urlLocalizer, Properties properties) throws ConfigurationException {
        super();
        this.clerk = clerk;
        this.urlLocalizer = urlLocalizer;
        this.properties = properties;

        if (clerk != null) {
            if (!properties.containsKey("keyDomain")) {
                throw new ConfigurationException("Property keyDomain is a required property when using WADL2UDDI.");
            }
            if (!properties.containsKey("businessKey") && !properties.containsKey("businessName")) {
                throw new ConfigurationException("Either property businessKey, or businessName, is a required property when using WADL2UDDI.");
            }
            if (!properties.containsKey("nodeName")) {
                if (properties.containsKey("serverName") && properties.containsKey("serverPort")) {
                    String nodeName = properties.getProperty("serverName") + "_" + properties.getProperty("serverPort");
                    properties.setProperty("nodeName", nodeName);
                } else {
                    throw new ConfigurationException("Property nodeName is not defined and is a required property when using WADL2UDDI.");
                }
            }
        }

        //Obtaining values from the properties
        this.keyDomainURI = "uddi:" + properties.getProperty("keyDomain") + ":";
        if (properties.contains(Property.BUSINESS_KEY)) {
            this.businessKey = properties.getProperty(Property.BUSINESS_KEY);
        } else {
            //using the BusinessKey Template, and the businessName to construct the key 
            this.businessKey = UDDIKeyConvention.getBusinessKey(properties);
        }
        this.lang = properties.getProperty(Property.LANG, Property.DEFAULT_LANG);
    }

    public Set<TModel> createWADLTModels(String wadlURL, Application app) throws Exception {
        Set<TModel> tModels = new HashSet<TModel>();
        TModel binding = new TModel();
        binding.setTModelKey(keyDomainURI + "binding");
        
        Name sName = new Name();
        sName.setLang(lang);
        if (!app.getDoc().isEmpty()) {
            sName.setValue(app.getDoc().get(0).getTitle());
        }
        if (sName.getValue() == null) {
            sName.setValue(keyDomainURI + " Binding tModel");
        }
        binding.setName(sName);
        tModels.add(binding);
        
        
         binding = new TModel();
        binding.setTModelKey(keyDomainURI + "rest");
        
        sName = new Name();
        sName.setLang(lang);
        if (!app.getDoc().isEmpty()) {
            sName.setValue(app.getDoc().get(0).getTitle());
        }
        if (sName.getValue() == null) {
            sName.setValue(keyDomainURI + " Rest tModel");
        }
        binding.setName(sName);
        tModels.add(binding);
        

        //keyDomainURI + "binding"
        return tModels;
    }

    public Set<TModel> createWADLPortTypeTModels(String wadlURL, Application app) throws Exception {
        Set<TModel> tModels = new HashSet<TModel>();
        // Create a tModel for each portType

        return tModels;
    }

    public String getKeyDomainURI() {
        return keyDomainURI;
    }

    public void setKeyDomain(String keyDomainURI) {
        this.keyDomainURI = keyDomainURI;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    private static String ContentToString(List<Object> content) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.size(); i++) {
            sb.append(content.get(i).toString()).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Creates a UDDI Business Service.
     *
     * @param serviceQName This must be specified to identify the namespace of
     * the service, which is used to set the service uddi key
     * @param wadlDefinition
     * @return BusinessService
     * @throws MalformedURLException
     */
    public BusinessService createBusinessService(QName serviceQName, Application wadlDefinition) throws MalformedURLException {

        log.debug("Constructing Service UDDI Information for " + serviceQName);
        BusinessService service = new BusinessService();
        // BusinessKey
        service.setBusinessKey(businessKey);
        // ServiceKey
        service.setServiceKey(UDDIKeyConvention.getServiceKey(properties, serviceQName.getLocalPart()));
        // Description
        String serviceDescription = properties.getProperty(Property.SERVICE_DESCRIPTION, Property.DEFAULT_SERVICE_DESCRIPTION);
        // Override with the service description from the WADL if present
        boolean lengthwarn = false;
        if (!wadlDefinition.getDoc().isEmpty()) {

            for (int i = 0; i < wadlDefinition.getDoc().size(); i++) {
                String locallang=lang;
                if (wadlDefinition.getDoc().get(i).getLang() != null) {
                   locallang=(wadlDefinition.getDoc().get(i).getLang());
                } 
                if (locallang.length() > UDDIConstants.MAX_xml_lang_length) {
                    lengthwarn = true;
                    locallang=(locallang.substring(0, UDDIConstants.MAX_xml_lang_length - 1));
                }

                StringBuilder sb = new StringBuilder();
                sb.append(wadlDefinition.getDoc().get(i).getTitle()).append(" ");
                sb.append(ContentToString(wadlDefinition.getDoc().get(i).getContent()));

                service.getDescription().addAll(Common2UDDI.mapDescription(sb.toString(), locallang));

            }
        } else {
                service.getDescription().addAll(Common2UDDI.mapDescription(serviceDescription, lang));
        }



        // Service name
        Name sName = new Name();
        sName.setLang(lang);
        if (!wadlDefinition.getDoc().isEmpty()) {
            sName.setValue(wadlDefinition.getDoc().get(0).getTitle());
        }
        if (sName.getValue() == null) {
            sName.setValue(serviceQName.getLocalPart());
        }
        service.getName().add(sName);

        CategoryBag categoryBag = new CategoryBag();

        String namespace = serviceQName.getNamespaceURI();
        if (namespace != null && namespace.length()!=0) {
            KeyedReference namespaceReference = newKeyedReference(
                    "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", namespace);
            categoryBag.getKeyedReference().add(namespaceReference);
        }

        KeyedReference serviceReference = newKeyedReference(
                "uddi:uddi.org:wadl:types", "uddi-org:wadl:types", "service");
        categoryBag.getKeyedReference().add(serviceReference);

        KeyedReference localNameReference = newKeyedReference(
                "uddi:uddi.org:xml:localname", "uddi-org:xml:localName", serviceQName.getLocalPart());
        categoryBag.getKeyedReference().add(localNameReference);

        service.setCategoryBag(categoryBag);

        for (int i = 0; i < wadlDefinition.getResources().size(); i++) {
            BindingTemplate bindingTemplate = createWADLBinding(serviceQName, getDocTitle(wadlDefinition.getResources().get(i).getDoc()), new URL(wadlDefinition.getResources().get(i).getBase()), wadlDefinition.getResources().get(i));
            service.setBindingTemplates(new BindingTemplates());
            service.getBindingTemplates().getBindingTemplate().add(bindingTemplate);
        }


        if (lengthwarn) {
            log.warn("Some object descriptions are longer than the maximum allowed by UDDI and have been truncated.");
        }
        return service;
    }
    
    public static List<URL> getBaseAddresses(Application app)
    {
        List<URL> urls = new ArrayList<URL>();
        if (app==null) return urls;
        for (int i=0; i < app.getResources().size(); i++){
            try {
                urls.add(new URL(app.getResources().get(i).getBase()));
            } catch (MalformedURLException ex) {
                log.warn("The base URL " + app.getResources().get(i).getBase() + " is invalid or could not be parsed", ex);
            }
        }
        return urls;
    }

    protected static KeyedReference newKeyedReference(String tModelKey, String keyName, String value) {
        KeyedReference typesReference = new KeyedReference();
        typesReference.setTModelKey(tModelKey);
        typesReference.setKeyName(keyName);
        typesReference.setKeyValue(value);
        return typesReference;
    }

    protected BindingTemplate createWADLBinding(QName serviceQName, String portName, URL serviceUrl, Resources res) {

        BindingTemplate bindingTemplate = new BindingTemplate();
        // Set BusinessService Key
        bindingTemplate.setServiceKey(UDDIKeyConvention.getServiceKey(properties, serviceQName.getLocalPart()));

        if (serviceUrl != null) {
            // Set AccessPoint
            AccessPoint accessPoint = new AccessPoint();
            accessPoint.setUseType(AccessPointType.END_POINT.toString());
            accessPoint.setValue(urlLocalizer.rewrite(serviceUrl));
            bindingTemplate.setAccessPoint(accessPoint);
            // Set Binding Key
            String bindingKey = UDDIKeyConvention.getBindingKey(properties, serviceQName, portName, serviceUrl);
            bindingTemplate.setBindingKey(bindingKey);

            
            bindingTemplate.getDescription().addAll(Common2UDDI.mapDescription(getDescription(res.getDoc()), lang));

            // reference wsdl:binding tModel
            TModelInstanceInfo tModelInstanceInfoBinding = new TModelInstanceInfo();
            tModelInstanceInfoBinding.setTModelKey(keyDomainURI + "binding");
            InstanceDetails instanceDetails = new InstanceDetails();
            instanceDetails.setInstanceParms(portName);
            tModelInstanceInfoBinding.setInstanceDetails(instanceDetails);
           
            tModelInstanceInfoBinding.getDescription().addAll(Common2UDDI.mapDescription("The binding that this endpoint implements. " + bindingTemplate.getDescription().get(0).getValue()
                    + " The instanceParms specifies the \" port local name.", lang));
            TModelInstanceDetails tModelInstanceDetails = new TModelInstanceDetails();
            tModelInstanceDetails.getTModelInstanceInfo().add(tModelInstanceInfoBinding);

            // reference wsdl:portType tModel

            TModelInstanceInfo tModelInstanceInfoPortType = new TModelInstanceInfo();
            tModelInstanceInfoPortType.setTModelKey(keyDomainURI + "rest");
            //String portTypeDescription = "";
           
            Description descriptionPT = new Description();
            descriptionPT.setLang(lang);
            descriptionPT.setValue("The wadl:Resource:base implements." );
            tModelInstanceInfoPortType.getDescription().add(descriptionPT);
            tModelInstanceDetails.getTModelInstanceInfo().add(tModelInstanceInfoPortType);

            bindingTemplate.setTModelInstanceDetails(tModelInstanceDetails);

        }
        return bindingTemplate;
    }

    /**
     * parses a wadl from stream
     * @param stream
     * @return Application instance (WADL FILE)
     */
    public static Application parseWadl(InputStream stream) {
        Application unmarshal = JAXB.unmarshal(stream, Application.class);
        return unmarshal;
    }
    public static final String PACKAGE = "org.apache.juddi.v3.client.mapping.wadl";

    /**
     * parses a wadl from a URL or file
     * @param file
     * @return  Application instance (WADL FILE)
     */
    public static Application parseWadl(URL file) {
        Application unmarshal = JAXB.unmarshal(file, Application.class);
        return unmarshal;
    }
    
    /**
     * Parses a web accessible WADL file
     * @param weburl
     * @param username
     * @param password
     * @param ignoreSSLErrors if true, SSL errors are ignored
     * @return a non-null "Application" object, represeting a WADL's application root XML 
     * Sample code:<br>
     * <pre>
     * Application app = WADL2UDDI.parseWadl(new URL("http://server/wsdl.wsdl"), "username", "password", 
     *      clerkManager.getClientConfig().isX_To_Wsdl_Ignore_SSL_Errors() );
     * </pre>
     */
    public static Application parseWadl(URL weburl, String username, String password, boolean ignoreSSLErrors){
            DefaultHttpClient httpclient = null;
            Application unmarshal=null;
        try {
            String url = weburl.toString();
            if (!url.toLowerCase().startsWith("http")) {
                return parseWadl(weburl);
            }
            
            boolean usessl = false;
            int port = 80;
            if (url.toLowerCase().startsWith("https://")) {
                port = 443;
                usessl = true;
            }

            if (weburl.getPort() > 0) {
                port = weburl.getPort();
            }

            if (ignoreSSLErrors && usessl) {
                SchemeRegistry schemeRegistry = new SchemeRegistry();
                schemeRegistry.register(new Scheme("https", port, new MockSSLSocketFactory()));
                ClientConnectionManager cm = new BasicClientConnectionManager(schemeRegistry);
                httpclient = new DefaultHttpClient(cm);
            } else {
                httpclient = new DefaultHttpClient();
            }

            if (username != null && username.length() > 0
                    && password != null && password.length() > 0) {


                httpclient.getCredentialsProvider().setCredentials(
                        new AuthScope(weburl.getHost(), port),
                        new UsernamePasswordCredentials(username, password));
            }
            HttpGet httpGet = new HttpGet(url);
            try {

                HttpResponse response1 = httpclient.execute(httpGet);
                //System.out.println(response1.getStatusLine());
                // HttpEntity entity1 = response1.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String handleResponse = responseHandler.handleResponse(response1);
                StringReader sr = new StringReader(handleResponse);
                unmarshal = JAXB.unmarshal(sr, Application.class);
                

            } finally {
                httpGet.releaseConnection();

            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (httpclient != null) {
                httpclient.getConnectionManager().shutdown();
            }
        }
        return unmarshal;
    }

    public static Application parseWadl(File file) throws FileNotFoundException, IOException {
        Application unmarshal = JAXB.unmarshal(file, Application.class);
        return unmarshal;
    }

    
    private String getDocTitle(List<Doc> doc) {
        if (doc.isEmpty() || doc.get(0) == null || doc.get(0).getTitle() == null) {
            return "A resource base URL without a description";
        }
        return (doc.get(0).getTitle());
    }

    private String getDescription(List<Doc> doc) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < doc.size(); i++) {
            sb.append(doc.get(i).getTitle()).append(" ");
            sb.append(ContentToString(doc.get(i).getContent()));
        }
        String ret= sb.toString().trim();
        
        if (ret.length()==0)
                return "No Description";
        return ret;
    }
}
