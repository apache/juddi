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
package org.apache.juddi.v3.client.mapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.v3.client.UDDIConstants;
import org.apache.juddi.v3.client.config.Property;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIKeyConvention;
import org.apache.juddi.v3.client.mappings.wadl.Application;
import org.apache.juddi.v3.client.mappings.wadl.Doc;
import org.apache.juddi.v3.client.mappings.wadl.Resource;
import org.apache.juddi.v3.client.mappings.wadl.Resources;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;
import org.w3c.dom.Element;

/**
 * This class is incomplete
 *
 * @author Alex O'Ree
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
     * @param waldDefinition
     * @return
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
        // Override with the service description from the WSDL if present
        boolean lengthwarn = false;
        if (!wadlDefinition.getDoc().isEmpty()) {

            for (int i = 0; i < wadlDefinition.getDoc().size(); i++) {
                Description description = new Description();
                if (wadlDefinition.getDoc().get(i).getLang() != null) {
                    description.setLang(wadlDefinition.getDoc().get(i).getLang());
                } else {
                    description.setLang(lang);
                }
                if (description.getLang() != null && description.getLang().length() > UDDIConstants.MAX_xml_lang_length) {
                    lengthwarn = true;
                    description.setLang(description.getLang().substring(0, UDDIConstants.MAX_xml_lang_length - 1));
                }

                StringBuilder sb = new StringBuilder();
                sb.append(wadlDefinition.getDoc().get(i).getTitle()).append(" ");
                sb.append(ContentToString(wadlDefinition.getDoc().get(i).getContent()));

                description.setValue(wadlDefinition.getDoc().get(i).getTitle());
                if (description.getValue() != null && description.getValue().length() > UDDIConstants.MAX_description_length) {
                    lengthwarn = true;
                    description.setValue(description.getValue().substring(0, UDDIConstants.MAX_description_length - 1));
                }

            }
        } else {

            Description description = new Description();
            description.setLang(lang);
            if (description.getLang() != null && description.getLang().length() > UDDIConstants.MAX_xml_lang_length) {
                lengthwarn = true;
                description.setLang(description.getLang().substring(0, UDDIConstants.MAX_xml_lang_length - 1));
            }
            description.setValue(serviceDescription);
            service.getDescription().add(description);
            if (description.getValue() != null && description.getValue().length() > UDDIConstants.MAX_description_length) {
                lengthwarn = true;
                description.setValue(description.getValue().substring(0, UDDIConstants.MAX_description_length - 1));
            }
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
        if (namespace != null && namespace != "") {
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
    
    public static List<URL> GetBaseAddresses(Application app)
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

            Description description = new Description();
            description.setLang(lang);
            description.setValue(getDescription(res.getDoc()));
            bindingTemplate.getDescription().add(description);

            // reference wsdl:binding tModel
            TModelInstanceInfo tModelInstanceInfoBinding = new TModelInstanceInfo();
            tModelInstanceInfoBinding.setTModelKey(keyDomainURI + "binding");
            InstanceDetails instanceDetails = new InstanceDetails();
            instanceDetails.setInstanceParms(portName);
            tModelInstanceInfoBinding.setInstanceDetails(instanceDetails);
            Description descriptionB = new Description();
            descriptionB.setLang(lang);
            descriptionB.setValue("The binding that this endpoint implements. " + bindingTemplate.getDescription().get(0).getValue()
                    + " The instanceParms specifies the port local name.");
            tModelInstanceInfoBinding.getDescription().add(descriptionB);
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

    public static Application ParseWadl(InputStream stream) {
        Application unmarshal = JAXB.unmarshal(stream, Application.class);
        return unmarshal;
    }
    public static final String PACKAGE = "org.apache.juddi.v3.client.mappings.wadl";

    public static Application ParseWadl(URL file) {
        Application unmarshal = JAXB.unmarshal(file, Application.class);
        return unmarshal;
    }

    public static Application ParseWadl(File file) throws FileNotFoundException, IOException {
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
        return sb.toString().trim();
    }
}
