/*
 * Copyright 2001-2011 The Apache Software Foundation.
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
 *
 */
package org.apache.juddi.v3.client.mapping;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.http.HTTPAddress;
import javax.wsdl.extensions.http.HTTPBinding;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.wsdl.extensions.soap12.SOAP12Address;
import javax.xml.namespace.QName;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.config.Property;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIKeyConvention;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BindingTemplates;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.BusinessServices;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;
import org.w3c.dom.Element;

/**
 * This class implements the OASIS <a
 * href="http://www.oasis-open.org/committees/uddi-spec/doc/tn/uddi-spec-tc-tn-wsdl-v202-20040631.htm">
 * 'Using WSDL in a UDDI Registry, Version 2.0.2'</a> technote. This class
 * creates a detailed mapping of WSDL 1.1 artifacts to the UDDI V3 data model.
 * <ul> <th>Section 2.4 in the technote</th> <li>2.4.1 wsdl:portType ->
 * uddi:tModel - {@link #createWSDLPortTypeTModels(String, Map)}</li> <li>2.4.2
 * wsdl:binding -> uddi:tModel -
 * {@link #createWSDLBindingTModels(String, Map)}</li> <li>TODO: 2.4.3
 * wsdl:service -> uddi:businessService</li> <li>TODO: 2.4.4 wsdl:port ->
 * uddi:bindingTemplate</li> <li>TODO: 2.4.5 wsdl:port Address Extensions ->
 * uddi:bindingTemplate</li> </ul>
 *
 * @author Kurt T Stam
 * @Since 3.1.5
 */
public class WSDL2UDDI {

    private static Log log = LogFactory.getLog(WSDL2UDDI.class);
    private String keyDomainURI;
    private String businessKey;
    private String lang;
    private UDDIClerk clerk = null;
    private Properties properties = null;
    private URLLocalizer urlLocalizer;

    /**
     * Required Properties are: businessName, for example: 'Apache' nodeName,
     * for example: 'uddi.example.org_80' keyDomain, for example:
     * juddi.apache.org
     *
     * Optional Properties are: lang: for example: 'nl'
     *
     * @param clerk - can be null if register/unregister methods are not used.
     * @param urlLocalizer - A reference to an custom
     * @param properties
     * @throws ConfigurationException
     */
    public WSDL2UDDI(UDDIClerk clerk, URLLocalizer urlLocalizer, Properties properties) throws ConfigurationException {
        super();

        this.clerk = clerk;
        this.urlLocalizer = urlLocalizer;
        this.properties = properties;

        if (clerk != null) {
            if (!properties.containsKey("keyDomain")) {
                throw new ConfigurationException("Property keyDomain is a required property when using WSDL2UDDI.");
            }
            if (!properties.containsKey("businessKey") && !properties.containsKey("businessName")) {
                throw new ConfigurationException("Either property businessKey, or businessName, is a required property when using WSDL2UDDI.");
            }
            if (!properties.containsKey("nodeName")) {
                if (properties.containsKey("serverName") && properties.containsKey("serverPort")) {
                    String nodeName = properties.getProperty("serverName") + "_" + properties.getProperty("serverPort");
                    properties.setProperty("nodeName", nodeName);
                } else {
                    throw new ConfigurationException("Property nodeName is not defined and is a required property when using WSDL2UDDI.");
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

    public BusinessServices registerBusinessServices(Definition wsdlDefinition) throws RemoteException, ConfigurationException, TransportException, WSDLException {

        BusinessServices businessServices = new BusinessServices();

        for (Object serviceName : wsdlDefinition.getAllServices().keySet()) {
            QName serviceQName = (QName) serviceName;
            Service service = wsdlDefinition.getService(serviceQName);
            BusinessService businessService = null;
            //add service
            URL serviceUrl = null;
            if (service.getPorts() != null && service.getPorts().size() > 0) {
                for (Object portName : service.getPorts().keySet()) {
                    businessService = registerBusinessService(serviceQName, (String) portName, serviceUrl, wsdlDefinition).getBusinessService();
                }
            }
            if (businessService != null) {
                businessServices.getBusinessService().add(businessService);
            }
        }

        return businessServices;

    }

    @SuppressWarnings("unchecked")
    public ServiceRegistrationResponse registerBusinessService(QName serviceQName, String portName, URL serviceUrl, Definition wsdlDefinition) throws RemoteException, ConfigurationException, TransportException, WSDLException {

        String genericWSDLURL = wsdlDefinition.getDocumentBaseURI();   //TODO maybe point to repository version
        ServiceRegistrationResponse response = new ServiceRegistrationResponse();
        String serviceKey = UDDIKeyConvention.getServiceKey(properties, serviceQName.getLocalPart());
        BusinessService businessService = lookupService(serviceKey);
        if (businessService == null) {
            List<TModel> tModels = new ArrayList<TModel>();
            // Create the PortType tModels
            Map<QName, PortType> portTypes = (Map<QName, PortType>) wsdlDefinition.getAllPortTypes();
            tModels.addAll(createWSDLPortTypeTModels(genericWSDLURL, portTypes));
            // Create the Binding tModels
            Map<QName, Binding> bindings = (Map<QName, Binding>) wsdlDefinition.getAllBindings();
            tModels.addAll(createWSDLBindingTModels(genericWSDLURL, bindings));
            // Register these tModels
            for (TModel tModel : tModels) {
                clerk.register(tModel);
            }
            // Service
            businessService = createBusinessService(serviceQName, wsdlDefinition);
            // Register this Service
            clerk.register(businessService);
        }
        //Add the BindingTemplate to this Service
        BindingTemplate binding = createWSDLBinding(serviceQName, portName, serviceUrl, wsdlDefinition);
        // Register BindingTemplate
        if (binding.getAccessPoint()!=null) {
	        clerk.register(binding);
	        if (businessService.getBindingTemplates() == null) {
	            businessService.setBindingTemplates(new BindingTemplates());
	        }
	        businessService.getBindingTemplates().getBindingTemplate().add(binding);
	        response.setBindingKey(binding.getBindingKey());
        }
        response.setBusinessService(businessService);
        return response;
    }

    public String[] unRegisterBusinessServices(Definition wsdlDefinition) throws RemoteException, ConfigurationException, TransportException, MalformedURLException {

        String[] businessServices = new String[wsdlDefinition.getAllServices().size()];
        int i = 0;
        for (Object serviceName : wsdlDefinition.getAllServices().keySet()) {
            QName serviceQName = (QName) serviceName;
            Service service = wsdlDefinition.getService(serviceQName);
            //unregister service
            URL serviceUrl = null;
            if (service.getPorts() != null && service.getPorts().size() > 0) {
                for (Object portName : service.getPorts().keySet()) {
                    //construct the accessURL
                    serviceUrl = new URL(getBindingURL((Port) service.getPorts().get(portName)));
                    businessServices[i++] = unRegisterBusinessService(serviceQName, (String) portName, serviceUrl);
                }
            }
        }
        return businessServices;
    }

    public String unRegisterBusinessService(QName serviceName, String portName, URL serviceUrl) throws RemoteException, ConfigurationException, TransportException {

        String serviceKey = UDDIKeyConvention.getServiceKey(properties, serviceName.getLocalPart());
        BusinessService service = lookupService(serviceKey);
        boolean isRemoveServiceIfNoTemplates = true;
        String bindingKey = UDDIKeyConvention.getBindingKey(properties, serviceName, portName, serviceUrl);
        //check if this bindingKey is in the service's binding templates
        for (BindingTemplate bindingTemplate : service.getBindingTemplates().getBindingTemplate()) {
            if (bindingKey.equals(bindingTemplate.getBindingKey())) {
                clerk.unRegisterBinding(bindingKey);
                //if this is the last binding for this service, and 
                if (service.getBindingTemplates().getBindingTemplate().size() == 1 && isRemoveServiceIfNoTemplates) {
                    clerk.unRegisterService(serviceKey);
                    if (bindingTemplate.getTModelInstanceDetails() != null
                            && bindingTemplate.getTModelInstanceDetails().getTModelInstanceInfo() != null) {
                        for (TModelInstanceInfo tModelInstanceInfo : bindingTemplate.getTModelInstanceDetails().getTModelInstanceInfo()) {
                            String tModelKey = tModelInstanceInfo.getTModelKey();
                            TModelDetail tModelDetail = clerk.getTModelDetail(tModelKey);
                            //delete all tModels assuming they are the portType and Binding tModels.
                            if (tModelDetail.getTModel() != null && tModelDetail.getTModel().size() > 0) {
                                for (TModel tModel : tModelDetail.getTModel()) {
                                    clerk.unRegisterTModel(tModel.getTModelKey());
                                }
                            }
                        }
                    }
                }
            }
        }
        return serviceKey;
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

    /**
     * <h3>2.4.2 wsdl:binding -> uddi:tModel</h3>
     *
     * <p>A wsdl:binding MUST be modeled as a uddi:tModel. The minimum
     * information that must be captured about a binding is its entity type, its
     * local name, its namespace, the location of the WSDL document that defines
     * the binding, the portType that it implements, its protocol, and,
     * optionally, the transport information. Capturing the entity type enables
     * users to search for tModels that represent binding artifacts. Capturing
     * the local name, namespace, and WSDL location enables users to locate the
     * definition of the specified binding artifact. The link to the portType
     * enables users to search for bindings that implement a particular
     * portType.</p>
     *
     * <p>A wsdl:binding corresponds to a WSDL service interface definition as
     * defined by the mapping in the Version 1 Best Practice. To maintain
     * compatibility with the previous mapping, the binding must also be
     * characterized as type "wsdlSpec".</p>
     *
     * <p>The wsdl:binding information is captured as follows:</p>
     *
     * <p>The uddi:name element of the tModel MUST be the value of the name
     * attribute of the wsdl:binding.</p>
     *
     * <p>The tModel MUST contain a categoryBag, and the categoryBag MUST
     * contain at least the following keyedReference elements:</p> <ol> <li> A
     * keyedReference with a tModelKey of the WSDL Entity Type category system
     * and a keyValue of "binding".</li> <li> A keyedReference with a tModelKey
     * of the WSDL portType Reference category system and a keyValue of the
     * tModelKey that models the wsdl:portType to which the wsdl:binding
     * relates.</li> <li> A keyedReference with a tModelKey of the UDDI Types
     * category system and a keyValue of "wsdlSpec" for backward
     * compatibility[1].</li> <li> One or two keyedReferences as required to
     * capture the protocol and optionally the transport information refer to
     * the next section.</li> </ol>
     *
     * <p>If the wsdl:binding has a targetNamespace then the categoryBag MUST
     * also contain an additional keyedReference with a tModelKey of the XML
     * Namespace category system and a keyValue of the target namespace of the
     * wsdl:definitions element that contains the wsdl:binding. If the
     * targetNamespace is absent from the binding, a categoryBag MUST NOT
     * contain a keyedReference to the XML Namespace category system.</p>
     *
     * <p>The tModel MUST contain an overviewDoc with an overviewURL containing
     * the location of the WSDL document that describes the wsdl:binding.</p>
     *
     * <h4>2.4.2.1 wsdl:binding Extensions</h4>
     *
     * <p>Information about the protocol and transport, if applicable, specified
     * in an extension to the wsdl:binding is used to categorize the binding
     * tModel as described in the following sections. This information is
     * specified using two of the category systems defined in this Technical
     * Note:</p> <ol> <li> Protocol Categorization</li> <li> Transport
     * Categorization</li> </ol> <p>The valid values for the Protocol
     * Categorization category system are tModelKeys of tModels that are
     * categorized as protocol tModels. Similarly, the valid values for the
     * Transport Categorization category system are tModelKeys of tModels that
     * are categorized as transport tModels.</p> <p> The reason for having these
     * two categorization schemes that take tModel keys as values is to allow
     * other standard or proprietary protocols and transports to be defined and
     * used in the same way as the standard SOAP and HTTP protocols and
     * transport.</p>
     *
     * <h4>2.4.2.1.1 soap:binding</h4>
     *
     * <p>If the wsdl:binding contains a soap:binding extensibility element from
     * the http://schemas.xmlsoap.org/wsdl/soap/ namespace then the categoryBag
     * MUST include a keyedReference with a tModelKey of the Protocol
     * Categorization category system and a keyValue of the tModelKey of the
     * SOAP Protocol tModel.</p>
     *
     * <p>If the value of the transport attribute of the soap:binding element is
     * http://schemas.xmlsoap.org/soap/http then the categoryBag MUST include a
     * keyedReference with a tModelKey of the Transport Categorization category
     * system and a keyValue of the tModelKey of the HTTP Transport tModel.</p>
     *
     * <p>If the value of the transport attribute is anything else, then the
     * bindingTemplate MUST include an additional keyedReference with a
     * tModelKey of the Transport Categorization category system and a keyValue
     * of the tModelKey of an appropriate transport tModel.</p>
     *
     * <h4>2.4.2.1.2 http:binding</h4>
     *
     * <p>If the wsdl:binding contains an http:binding extensibility element
     * from the http://schemas.xmlsoap.org/wsdl/http/ namespace then the
     * categoryBag MUST include a keyedReference with a tModelKey of the
     * Protocol Categorization category system and a keyValue of the tModelKey
     * of the HTTP Protocol tModel.</p>
     *
     * <p>Note that this is a different tModel from the HTTP Transport tModel,
     * and in this case there is no separate transport tModel, and therefore no
     * keyedReference in the categoryBag from the Transport Categorization
     * category system.</p>
     *
     * <h4>2.4.2.1.3 Other wsdl:binding Extensions</h4>
     *
     * <p>Other wsdl:binding extensibility elements are handled in a similar
     * fashion. It is assumed that vendors who provide other bindings will
     * provide the appropriate protocol and transport tModels.</p>
     *
     * Example Code
     * <pre>
     * URL url = new URL("http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl");
     * String domain = url.getHost();
     * ReadWSDL rw = new ReadWSDL();
     * Definition wsdlDefinition = rw.readWSDL(url);
     * properties.put("keyDomain", domain);
     * properties.put("businessName", domain);
     * properties.put("serverName", url.getHost());
     * properties.put("serverPort", url.getPort());
     * wsdlURL = wsdlDefinition.getDocumentBaseURI();
     * WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
     * Map allBindings = wsdlDefinition.getAllBindings();
     * Set<TModel> createWSDLBindingTModels = wsdl2UDDI.createWSDLBindingTModels(url.toString(), allBindings);
     * </pre>
     * @param wsdlURL
     * @param binding Map
     * @return set of WSDL Binding tModels
     * @throws WSDLException
     */
    public Set<TModel> createWSDLBindingTModels(String wsdlURL, Map<QName, Binding> bindings) throws WSDLException {

        Set<TModel> tModels = new HashSet<TModel>();
        //Register a tModel for each portType
        for (QName qName : bindings.keySet()) {
            String localpart = qName.getLocalPart();
            String namespace = qName.getNamespaceURI();
            // Build the tModel
            TModel tModel = new TModel();
            // Set the Key
            tModel.setTModelKey(keyDomainURI + localpart);
            // Set the Name
            Name name = new Name();
            name.setLang(lang);
            name.setValue(localpart);
            tModel.setName(name);
            // Set the OverviewURL
            OverviewURL overviewURL = new OverviewURL();
            overviewURL.setUseType(AccessPointType.WSDL_DEPLOYMENT.toString());
            overviewURL.setValue(wsdlURL);
            OverviewDoc overviewDoc = new OverviewDoc();
            overviewDoc.setOverviewURL(overviewURL);
            tModel.getOverviewDoc().add(overviewDoc);
            // Set the categoryBag
            CategoryBag categoryBag = new CategoryBag();

            if (namespace != null && !"".equals(namespace)) {
                // A keyedReference with a tModelKey of the WSDL Entity Type category system and a keyValue of "binding".
                KeyedReference namespaceReference = newKeyedReference(
                        "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", namespace);
                categoryBag.getKeyedReference().add(namespaceReference);
            }

            // A keyedReference with a tModelKey of the WSDL Entity Type category system and a keyValue of "binding".
            KeyedReference typesReference = newKeyedReference(
                    "uddi:uddi.org:wsdl:types", "uddi-org:wsdl:types", "binding");
            categoryBag.getKeyedReference().add(typesReference);

            // A keyedReference with a tModelKey of the WSDL portType Reference category system and a keyValue 
            // of the tModelKey that models the wsdl:portType to which the wsdl:binding relates.
            Binding binding = bindings.get(qName);
            String portTypeKey = keyDomainURI + binding.getPortType().getQName().getLocalPart();
            KeyedReference namespaceReference = newKeyedReference(
                    "uddi:uddi.org:wsdl:porttypereference", "uddi-org:wsdl:portTypeReference",
                    portTypeKey);
            categoryBag.getKeyedReference().add(namespaceReference);

            //  A keyedReference with a tModelKey of the UDDI Types category system and a keyValue of 
            // "wsdlSpec" for backward compatibility.
            KeyedReference typesReferenceBackwardsCompatible = newKeyedReference(
                    "uddi:uddi.org:categorization:types", "uddi-org:types", "wsdlSpec");
            categoryBag.getKeyedReference().add(typesReferenceBackwardsCompatible);

            // One or two keyedReferences as required to capture the protocol
            for (Object object : binding.getExtensibilityElements()) {
                if (SOAPBinding.class.isAssignableFrom(object.getClass())) {
                    // If the wsdl:binding contains a soap:binding extensibility element from the 
                    // 'http://schemas.xmlsoap.org/wsdl/soap/' namespace then the categoryBag MUST 
                    //include a keyedReference with a tModelKey of the Protocol Categorization 
                    // category system and a keyValue of the tModelKey of the SOAP Protocol tModel.
                    SOAPBinding soapBinding = (SOAPBinding) object;
                    KeyedReference soapProtocol = newKeyedReference(
                            "uddi:uddi.org:wsdl:categorization:protocol", "uddi-org:protocol:soap", "uddi:uddi.org:protocol:soap");
                    categoryBag.getKeyedReference().add(soapProtocol);
                    // If the value of the transport attribute of the soap:binding element 
                    // is 'http://schemas.xmlsoap.org/soap/http' then the categoryBag MUST 
                    // include a keyedReference with a tModelKey of the Transport Categorization 
                    // category system and a keyValue of the tModelKey of the HTTP Transport tModel.
                    if ("http://schemas.xmlsoap.org/soap/http".equals(soapBinding.getTransportURI())) {
                        KeyedReference httpTransport = newKeyedReference(
                                "uddi:uddi.org:wsdl:categorization:transport", "uddi-org:http", "uddi:uddi.org:transport:http");
                        categoryBag.getKeyedReference().add(httpTransport);
                    } else if (soapBinding.getTransportURI() != null) {
                        // TODO If the value of the transport attribute is anything else, 
                        // then the bindingTemplate MUST include an additional keyedReference with a tModelKey 
                        // of the Transport Categorization category system and a keyValue of the tModelKey of 
                        // an appropriate transport tModel.
                        log.warn("not implemented");
                    }


                } else if (object.getClass().isInstance(HTTPBinding.class)) {

                    // If the wsdl:binding contains an http:binding extensibility element from the 
                    // http://schemas.xmlsoap.org/wsdl/http/ namespace then the categoryBag MUST 
                    // include a keyedReference with a tModelKey of the Protocol Categorization 
                    // category system and a keyValue of the tModelKey of the HTTP Protocol tModel.
                    KeyedReference soapProtocol = newKeyedReference(
                            "uddi:uddi.org:wsdl:categorization:protocol", "uddi-org:protocol:http", "uddi:uddi.org:protocol:http");
                    categoryBag.getKeyedReference().add(soapProtocol);
                }
            }


            tModel.setCategoryBag(categoryBag);
            tModels.add(tModel);
        }
        return tModels;
    }

    /**
     * <h3>2.4.1 wsdl:portType -> uddi:tModel</h3>
     *
     * <p>A wsdl:portType MUST be modeled as a uddi:tModel.</p>
     *
     * <p>The minimum information that must be captured about a portType is its
     * entity type, its local name, its namespace, and the location of the WSDL
     * document that defines the portType. Capturing the entity type enables
     * users to search for tModels that represent portType artifacts. Capturing
     * the local name, namespace, and WSDL location enables users to locate the
     * definition of the specified portType artifact.</p>
     *
     * <p>The wsdl:portType information is captured as follows:</p>
     *
     * <p>The uddi:name element of the tModel MUST be the value of the name
     * attribute of the wsdl:portType.</p>
     *
     * <p>The tModel MUST contain a categoryBag, and the categoryBag MUST
     * contain a keyedReference with a tModelKey of the WSDL Entity Type
     * category system and a keyValue of "portType".</p>
     *
     * <p>If the wsdl:portType has a targetNamespace then the categoryBag MUST
     * also contain an additional keyedReference with a tModelKey of the XML
     * Namespace category system and a keyValue of the target namespace of the
     * wsdl:definitions element that contains the wsdl:portType. If the
     * targetNamespace is absent from the portType, a categoryBag MUST NOT
     * contain a keyedReference to the XML Namespace category system.</p>
     *
     * <p>The tModel MUST contain an overviewDoc with an overviewURL containing
     * the location of the WSDL document that describes the wsdl:portType.</p>
     * Example Code
     * <pre>
     * URL url = new URL("http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl");
     * String domain = url.getHost();
     * ReadWSDL rw = new ReadWSDL();
     * Definition wsdlDefinition = rw.readWSDL(url);
     * properties.put("keyDomain", domain);
     * properties.put("businessName", domain);
     * properties.put("serverName", url.getHost());
     * properties.put("serverPort", url.getPort());
     * wsdlURL = wsdlDefinition.getDocumentBaseURI();
     * WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
     * Map<QName, PortType> portTypes = (Map<QName, PortType>) wsdlDefinition.getAllPortTypes();
     * Set<TModel> portTypeTModels = wsdl2UDDI.createWSDLPortTypeTModels(wsdlURL, portTypes);
     * </pre>
     *
     * @param wsdlURL This is used to set the Overview URL
     * @param portType Map
     * @return set of WSDL PortType tModels
     * @throws WSDLException
     */
    public Set<TModel> createWSDLPortTypeTModels(String wsdlURL, Map<QName, PortType> portTypes) throws WSDLException {
        Set<TModel> tModels = new HashSet<TModel>();
        // Create a tModel for each portType
        for (QName qName : portTypes.keySet()) {
            // Build the tModel
            TModel tModel = new TModel();
            String localpart = qName.getLocalPart();
            String namespace = qName.getNamespaceURI();
            // Set the Key
            tModel.setTModelKey(keyDomainURI + localpart);
            // Set the Name. The uddi:name element of the tModel MUST be the value of
            // the name attribute of the wsdl:portType.
            Name name = new Name();
            name.setLang(lang);
            name.setValue(localpart);
            tModel.setName(name);
            // Set the OverviewURL. The tModel MUST contain an overviewDoc with an 
            // overviewURL containing the location of the WSDL document that 
            // describes the wsdl:portType.
            OverviewURL overviewURL = new OverviewURL();
            overviewURL.setUseType(AccessPointType.WSDL_DEPLOYMENT.toString());
            overviewURL.setValue(wsdlURL);
            OverviewDoc overviewDoc = new OverviewDoc();
            overviewDoc.setOverviewURL(overviewURL);
            tModel.getOverviewDoc().add(overviewDoc);
            // Create the categoryBag, The tModel MUST contain a categoryBag
            CategoryBag categoryBag = new CategoryBag();

            // the categoryBag MUST contain a keyedReference with a tModelKey of the WSDL 
            // Entity Type category system and a keyValue of "portType".
            KeyedReference typesReference = newKeyedReference(
                    "uddi:uddi.org:wsdl:types", "uddi-org:wsdl:types", "portType");
            categoryBag.getKeyedReference().add(typesReference);

            // If the wsdl:portType has a targetNamespace then the categoryBag MUST also contain an 
            // additional keyedReference with a tModelKey of the XML Namespace category system and a 
            // keyValue of the target namespace of the wsdl:definitions element that contains the 
            // wsdl:portType. If the targetNamespace is absent from the portType, a categoryBag 
            // MUST NOT contain a keyedReference to the XML Namespace category system.
            if (namespace != null && !"".equals(namespace)) {
                KeyedReference namespaceReference = newKeyedReference(
                        "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", namespace);
                categoryBag.getKeyedReference().add(namespaceReference);
            }

            tModel.setCategoryBag(categoryBag);
            tModels.add(tModel);
        }
        return tModels;
    }

    protected static KeyedReference newKeyedReference(String tModelKey, String keyName, String value) {
        KeyedReference typesReference = new KeyedReference();
        typesReference.setTModelKey(tModelKey);
        typesReference.setKeyName(keyName);
        typesReference.setKeyValue(value);
        return typesReference;
    }

    /**
     * Builds a finder to find the binding tModels for a portType.
     *
     * @param processName
     * @return
     */
    public static FindTModel createFindBindingTModelForPortType(String portType, String namespace) {

        FindTModel findTModel = new FindTModel();
        CategoryBag categoryBag = new CategoryBag();

        if (namespace != null && namespace.length() != 0) {
            KeyedReference namespaceReference = newKeyedReference(
                    "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", namespace);
            categoryBag.getKeyedReference().add(namespaceReference);
        }
        KeyedReference bindingReference = newKeyedReference(
                "uddi:uddi.org:wsdl:types", "uddi-org:wsdl:types", "binding");
        categoryBag.getKeyedReference().add(bindingReference);

        KeyedReference portTypeReference = newKeyedReference(
                "uddi:uddi.org:wsdl:porttypereference", "uddi-org:wsdl:portTypeReference", portType);
        categoryBag.getKeyedReference().add(portTypeReference);

        findTModel.setCategoryBag(categoryBag);

        if (log.isDebugEnabled()) {
            log.debug(new PrintUDDI<FindTModel>().print(findTModel));
        }
        return findTModel;
    }

    /**
     * Builds a finder to find the portType tModels for a portType.
     *
     * @param processName
     * @return
     */
    public static FindTModel createFindPortTypeTModelForPortType(String portTypeName, String namespace) {

        FindTModel findTModel = new FindTModel();
        Name name = new Name();
        name.setLang("en");
        name.setValue(portTypeName);
        findTModel.setName(name);

        CategoryBag categoryBag = new CategoryBag();
        if (namespace != null && namespace.length() != 0) {
            KeyedReference namespaceReference = newKeyedReference(
                    "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", namespace);
            categoryBag.getKeyedReference().add(namespaceReference);
        }
        KeyedReference bindingReference = newKeyedReference(
                "uddi:uddi.org:wsdl:types", "uddi-org:wsdl:types", "portType");
        categoryBag.getKeyedReference().add(bindingReference);

        findTModel.setCategoryBag(categoryBag);

        if (log.isDebugEnabled()) {
            log.debug(new PrintUDDI<FindTModel>().print(findTModel));
        }
        return findTModel;
    }

    /**
     * Perform a lookup by serviceKey, and will return null if not found.
     *
     * @param serviceKey
     * @return
     * @throws RemoteException
     * @throws ConfigurationException
     * @throws TransportException
     */
    private BusinessService lookupService(String serviceKey) throws RemoteException, ConfigurationException, TransportException {

        //Checking if this serviceKey already exist
        BusinessService service = clerk.findService(serviceKey);
        return service;
    }

    /**
     * Creates a business service based off of a WSDL definition<Br>No changes are made to the UDDI
     * endpoints using this method
     *<br>
     * Example Code:
     * <pre>
     * URL url = new URL("http://graphical.weather.gov/xml/SOAP_server/ndfdXMLserver.php?wsdl");
     * String domain = url.getHost();
     * ReadWSDL rw = new ReadWSDL();
     * Definition wsdlDefinition = rw.readWSDL(url);
     * properties.put("keyDomain", domain);
     * properties.put("businessName", domain);
     * properties.put("serverName", url.getHost());
     * properties.put("serverPort", url.getPort());
     * wsdlURL = wsdlDefinition.getDocumentBaseURI();
     * WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(null, new URLLocalizerDefaultImpl(), properties);
     * BusinessServices businessServices = wsdl2UDDI.createBusinessServices(wsdlDefinition);
     * </pre>
     * @param wsdlDefinition must not be null
     * @return a business service
     * @throws IllegalArgumentException if the wsdlDefinition is null
     */
    public BusinessServices createBusinessServices(Definition wsdlDefinition) {
        if (wsdlDefinition == null) {
            throw new IllegalArgumentException();
        }
        BusinessServices businessServices = new BusinessServices();
        for (Object serviceName : wsdlDefinition.getAllServices().keySet()) {
            QName serviceQName = (QName) serviceName;
            Service service = wsdlDefinition.getService(serviceQName);
            BusinessService businessService = createBusinessService(serviceQName, wsdlDefinition);
            //service.getExtensibilityElements().
            //add the bindingTemplates
            URL serviceUrl = null;
            if (service.getPorts() != null && service.getPorts().size() > 0) {
                businessService.setBindingTemplates(new BindingTemplates());
                for (Object portName : service.getPorts().keySet()) {
                    BindingTemplate bindingTemplate = createWSDLBinding(serviceQName, (String) portName, serviceUrl, wsdlDefinition);
                    businessService.getBindingTemplates().getBindingTemplate().add(bindingTemplate);
                }
            }
            businessServices.getBusinessService().add(businessService);
        }

        return businessServices;
    }

    /**
     * Creates a UDDI Business Service.
     *
     * @param serviceName
     * @param wsldDefinition
     * @return
     */
    protected BusinessService createBusinessService(QName serviceQName, Definition wsdlDefinition) {

        log.debug("Constructing Service UDDI Information for " + serviceQName);
        BusinessService service = new BusinessService();
        // BusinessKey
        service.setBusinessKey(businessKey);
        // ServiceKey
        service.setServiceKey(UDDIKeyConvention.getServiceKey(properties, serviceQName.getLocalPart()));
        // Description
        String serviceDescription = properties.getProperty(Property.SERVICE_DESCRIPTION, Property.DEFAULT_SERVICE_DESCRIPTION);
        // Override with the service description from the WSDL if present
        if (wsdlDefinition.getService(serviceQName) != null) {
            Element docElement = wsdlDefinition.getService(serviceQName).getDocumentationElement();
            if (docElement != null && docElement.getTextContent() != null) {
                serviceDescription = docElement.getTextContent();
            }
        }
        Description description = new Description();
        description.setLang(lang);
        description.setValue(serviceDescription);
        service.getDescription().add(description);
        // Service name
        Name sName = new Name();
        sName.setLang(lang);
        sName.setValue(serviceQName.getLocalPart());
        service.getName().add(sName);

        CategoryBag categoryBag = new CategoryBag();

        String namespace = serviceQName.getNamespaceURI();
        if (namespace != null && namespace != "") {
            KeyedReference namespaceReference = newKeyedReference(
                    "uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", namespace);
            categoryBag.getKeyedReference().add(namespaceReference);
        }

        KeyedReference serviceReference = newKeyedReference(
                "uddi:uddi.org:wsdl:types", "uddi-org:wsdl:types", "service");
        categoryBag.getKeyedReference().add(serviceReference);

        KeyedReference localNameReference = newKeyedReference(
                "uddi:uddi.org:xml:localname", "uddi-org:xml:localName", serviceQName.getLocalPart());
        categoryBag.getKeyedReference().add(localNameReference);

        service.setCategoryBag(categoryBag);

        return service;
    }

    protected BindingTemplate createWSDLBinding(QName serviceQName, String portName, URL serviceUrl, Definition wsdlDefinition) {

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
        }

        Service service = wsdlDefinition.getService(serviceQName);
        if (service != null) {
            TModelInstanceDetails tModelInstanceDetails = new TModelInstanceDetails();

            Port port = service.getPort(portName);
            if (port != null) {
                if (serviceUrl == null) {
                    for (Object element : port.getExtensibilityElements()) {
                    	String location = null;
                        if (element instanceof SOAPAddress) {
                            SOAPAddress address = (SOAPAddress) element;
                            location = address.getLocationURI();
                        } 
                        else if (element instanceof HTTPAddress) {
                        	HTTPAddress address = (HTTPAddress) element;
                            location = address.getLocationURI();
                        }
                        else if (element instanceof SOAP12Address) {
                        	SOAP12Address address = (SOAP12Address) element;
                            location = address.getLocationURI();
                        }
                        if (location != null ) {
                            try {
                            	URL locationURI = new URL(location);
                                AccessPoint accessPoint = new AccessPoint();
                                accessPoint.setUseType(AccessPointType.END_POINT.toString());
                                accessPoint.setValue(urlLocalizer.rewrite(locationURI));
                                bindingTemplate.setAccessPoint(accessPoint);
                                // Set Binding Key
                                String bindingKey = UDDIKeyConvention.getBindingKey(properties, serviceQName, portName, locationURI);
                                bindingTemplate.setBindingKey(bindingKey);
                                break;
                            } catch (MalformedURLException e) {
                                log.error(e.getMessage());
                            }
                        }
                    }

                }
                Binding binding = port.getBinding();
                // Set the Binding Description
                String bindingDescription = properties.getProperty(Property.BINDING_DESCRIPTION, Property.DEFAULT_BINDING_DESCRIPTION);
                // Override with the service description from the WSDL if present
                Element docElement = binding.getDocumentationElement();
                if (docElement != null && docElement.getTextContent() != null) {
                    bindingDescription = docElement.getTextContent();
                }
                Description description = new Description();
                description.setLang(lang);
                description.setValue(bindingDescription);
                bindingTemplate.getDescription().add(description);

                // reference wsdl:binding tModel
                TModelInstanceInfo tModelInstanceInfoBinding = new TModelInstanceInfo();
                tModelInstanceInfoBinding.setTModelKey(keyDomainURI + binding.getQName().getLocalPart());
                InstanceDetails instanceDetails = new InstanceDetails();
                instanceDetails.setInstanceParms(portName);
                tModelInstanceInfoBinding.setInstanceDetails(instanceDetails);
                Description descriptionB = new Description();
                descriptionB.setLang(lang);
                descriptionB.setValue("The wsdl:binding that this wsdl:port implements. " + bindingDescription
                        + " The instanceParms specifies the port local name.");
                tModelInstanceInfoBinding.getDescription().add(descriptionB);
                tModelInstanceDetails.getTModelInstanceInfo().add(tModelInstanceInfoBinding);

                // reference wsdl:portType tModel
                PortType portType = binding.getPortType();
                TModelInstanceInfo tModelInstanceInfoPortType = new TModelInstanceInfo();
                tModelInstanceInfoPortType.setTModelKey(keyDomainURI + portType.getQName().getLocalPart());
                String portTypeDescription = "";
                docElement = portType.getDocumentationElement();
                if (docElement != null && docElement.getTextContent() != null) {
                    portTypeDescription = docElement.getTextContent();
                }
                Description descriptionPT = new Description();
                descriptionPT.setLang(lang);
                descriptionPT.setValue("The wsdl:portType that this wsdl:port implements." + portTypeDescription);
                tModelInstanceInfoPortType.getDescription().add(descriptionPT);
                tModelInstanceDetails.getTModelInstanceInfo().add(tModelInstanceInfoPortType);

                bindingTemplate.setTModelInstanceDetails(tModelInstanceDetails);
            } else {
                log.error("Could not find Port with portName: " + portName);
            }
        } else {
            log.error("Could not find Service with serviceName: " + serviceQName.getLocalPart());
        }

        return bindingTemplate;
    }

    /**
     * Obtains the accessUrl from the WSDL
     *
     * @param port
     * @return
     * @throws MalformedURLException
     */
    private String getBindingURL(Port port) throws MalformedURLException {

        String bindingUrl = null;
        for (Object element : port.getExtensibilityElements()) {
            if (element instanceof SOAPAddress) {
                SOAPAddress address = (SOAPAddress) element;
                URL locationURI = new URL(address.getLocationURI());
                if (locationURI != null) {
                    bindingUrl = urlLocalizer.rewrite(locationURI);
                    break;
                }
            }
        }
        return bindingUrl;
    }
}
