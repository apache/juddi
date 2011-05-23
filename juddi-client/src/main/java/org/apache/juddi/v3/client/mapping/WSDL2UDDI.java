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
import javax.wsdl.extensions.http.HTTPBinding;
import javax.wsdl.extensions.soap.SOAPBinding;
import javax.xml.namespace.QName;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.client.config.Property;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInfo;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.api_v3.TModelList;
import org.w3c.dom.Element;


/**
 * This class implements the OASIS <a href="http://www.oasis-open.org/committees/uddi-spec/doc/tn/uddi-spec-tc-tn-wsdl-v202-20040631.htm">
 * 'Using WSDL in a UDDI Registry, Version 2.0.2'</a> technote. This class creates a detailed mapping of 
 * WSDL 1.1 artifacts to the UDDI V3 data model.
 * <ul>
 * <th>Section 2.4 in the technote</th>
 * <li>2.4.1 wsdl:portType -> uddi:tModel - {@link #createWSDLPortTypeTModels(String, Map)}</li>
 * <li>2.4.2 wsdl:binding  -> uddi:tModel - {@link #createWSDLBindingTModels(String, Map)}</li>
 * <li>TODO: 2.4.3 wsdl:service  -> uddi:businessService</li>
 * <li>TODO: 2.4.4 wsdl:port     -> uddi:bindingTemplate</li>
 * <li>TODO: 2.4.5 wsdl:port Address Extensions -> uddi:bindingTemplate</li>
 * </ul>
 * 
 * @author Kurt T Stam
 *
 */
public class WSDL2UDDI {
	
	private Log log = LogFactory.getLog(this.getClass());
	private String keyDomainURI;
	private String businessKey;
	private String lang;
	private UDDIClerk clerk = null;
	private Properties properties = null;
	private URLLocalizer urlLocalizer;
	
	public WSDL2UDDI(UDDIClerk clerk, URLLocalizer urlLocalizer, Properties properties) {
		super();
		
		this.clerk = clerk;
		this.urlLocalizer = urlLocalizer;
		this.properties = properties;
		
		//Obtaining values from the properties
		this.keyDomainURI =  "uddi:" + properties.getProperty("keyDomain") + ":";
		this.businessKey = Property.getBusinessKey(properties);
		this.lang = properties.getProperty(Property.LANG,Property.DEFAULT_LANG);
	}
	
	@SuppressWarnings("unchecked")
	public BindingTemplate register(QName serviceQName, String portName, URL serviceUrl, Definition wsdlDefinition) throws RemoteException, ConfigurationException, TransportException, WSDLException {
		
		String genericWSDLURL  = wsdlDefinition.getDocumentBaseURI();   //TODO maybe point to repository version
		
		String serviceKey = Property.getServiceKey(properties, serviceQName);
		BusinessService service = lookupService(serviceKey);
		if (service==null) {
			List<TModel> tModels = new ArrayList<TModel>();
			// Create the PortType tModels
			Map<QName,PortType> portTypes = (Map<QName,PortType>) wsdlDefinition.getAllPortTypes();
			tModels.addAll(createWSDLPortTypeTModels(genericWSDLURL, portTypes));
			// Create the Binding tModels
			Map<QName,Binding> bindings = (Map<QName,Binding>) wsdlDefinition.getAllBindings();
			tModels.addAll(createWSDLBindingTModels(genericWSDLURL, bindings));
			// Register these tModels
		    for (TModel tModel : tModels) {
				clerk.register(tModel);
			}
		    // Service
		    service = createBusinessService(serviceQName, wsdlDefinition);
		    // Register this Service
		    clerk.register(service);
		}
		//Add the BindingTemplate to this Service
		BindingTemplate binding = createWSDLBinding(serviceQName, portName, serviceUrl, wsdlDefinition);
		// Register BindingTemplate
		clerk.register(binding);
		return binding;
	}
	
	public String unRegister(QName serviceName, String portName, URL serviceUrl) throws RemoteException, ConfigurationException, TransportException {
		
		String serviceKey = Property.getServiceKey(properties, serviceName);
		BusinessService service = lookupService(serviceKey);
		boolean isRemoveServiceIfNoTemplates = true; 
		String bindingKey = Property.getBindingKey(properties, serviceName, portName, serviceUrl);
		//check if this bindingKey is in the service's binding templates
		for (BindingTemplate bindingTemplate : service.getBindingTemplates().getBindingTemplate()) {
			if (bindingKey.equals(bindingTemplate.getBindingKey())) {
				clerk.unRegisterBinding(bindingKey);
				//if this is the last binding for this service, and 
				if (service.getBindingTemplates().getBindingTemplate().size()==1 && isRemoveServiceIfNoTemplates) {
					clerk.unRegisterService(serviceKey);
					if (bindingTemplate.getTModelInstanceDetails()!=null 
							&& bindingTemplate.getTModelInstanceDetails().getTModelInstanceInfo() != null) {
						for (TModelInstanceInfo tModelInstanceInfo : bindingTemplate.getTModelInstanceDetails().getTModelInstanceInfo()) {
							String tModelKey = tModelInstanceInfo.getTModelKey();
							TModelDetail tModelDetail = clerk.getTModelDetail(tModelKey);
							//delete all tModels assuming they are the portType and Binding tModels.
							if (tModelDetail.getTModel()!=null && tModelDetail.getTModel().size()>0) {
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

		<p>A wsdl:binding MUST be modeled as a uddi:tModel. The minimum information that must be captured about a binding is 
		its entity type, its local name, 
		its namespace, the location of the WSDL document that defines the binding, the portType that it implements, 
		its protocol, and, optionally, the transport information. Capturing the entity type enables users 
		to search for tModels that represent binding artifacts. Capturing the local name, namespace, 
		and WSDL location enables users to locate the definition of the specified binding artifact. 
		The link to the portType enables users to search for bindings that implement a particular portType.</p>
		
		<p>A wsdl:binding corresponds to a WSDL service interface definition as defined by the mapping in the 
		Version 1 Best Practice. To maintain compatibility with the previous mapping, the binding must 
		also be characterized as type "wsdlSpec".</p>
		
		<p>The wsdl:binding information is captured as follows:</p>
		
		<p>The uddi:name element of the tModel MUST be the value of the name attribute of the wsdl:binding.</p>
		
		<p>The tModel MUST contain a categoryBag, and the categoryBag MUST contain at least the following keyedReference elements:</p>
		<ol>
		   <li> A keyedReference with a tModelKey of the WSDL Entity Type category system and a keyValue of "binding".</li>
		   <li> A keyedReference with a tModelKey of the WSDL portType Reference category system and a keyValue of the tModelKey that models the wsdl:portType to which the wsdl:binding relates.</li>
		   <li> A keyedReference with a tModelKey of the UDDI Types category system and a keyValue of "wsdlSpec" for 
		      backward compatibility[1].</li>
		   <li> One or two keyedReferences as required to capture the protocol and optionally the transport 
		      information refer to the next section.</li>
		</ol>
		
		<p>If the wsdl:binding has a targetNamespace then the categoryBag MUST also contain an additional keyedReference 
		with a tModelKey of the XML Namespace category system and a keyValue of the target namespace of the wsdl:definitions 
		element that contains the wsdl:binding. If the targetNamespace is absent from the binding, a categoryBag MUST NOT 
		contain a keyedReference to the XML Namespace category system.</p>
		
		<p>The tModel MUST contain an overviewDoc with an overviewURL containing the location of the WSDL document that 
		describes the wsdl:binding.</p>

		<h4>2.4.2.1      wsdl:binding Extensions</h4>
		
		<p>Information about the protocol and transport, if applicable, specified in an extension to the wsdl:binding 
		is used to categorize the binding tModel as described in the following sections. This information is specified 
		using two of the category systems defined in this Technical Note:</p>
		<ol>
		   <li> Protocol Categorization</li>
		   <li> Transport Categorization</li>
		</ol>
		<p>The valid values for the Protocol Categorization category system are tModelKeys of tModels that 
		are categorized as protocol tModels.  Similarly, the valid values for the Transport Categorization 
		category system are tModelKeys of tModels that are categorized as transport tModels.</p>
		<p>
		The reason for having these two categorization schemes that take tModel keys as values is to allow other 
		standard or proprietary protocols and transports to be defined and used in the same way as the standard 
		SOAP and HTTP protocols and transport.</p>
		
		<h4>2.4.2.1.1               soap:binding</h4>
		
		<p>If the wsdl:binding contains a soap:binding extensibility element from the 
		http://schemas.xmlsoap.org/wsdl/soap/ namespace then the categoryBag MUST include a keyedReference with
		 a tModelKey of the Protocol Categorization category system and a keyValue of the tModelKey of the 
		 SOAP Protocol tModel.</p>
		
		<p>If the value of the transport attribute of the soap:binding element is 
		http://schemas.xmlsoap.org/soap/http then the categoryBag MUST include a keyedReference with a 
		tModelKey of the Transport Categorization category system and a keyValue of the tModelKey of the 
		HTTP Transport tModel.</p>
		
		<p>If the value of the transport attribute is anything else, then the bindingTemplate MUST include
		 an additional keyedReference with a tModelKey of the Transport Categorization category system and 
		 a keyValue of the tModelKey of an appropriate transport tModel.</p>
		 
		<h4>2.4.2.1.2               http:binding</h4>
		
		<p>If the wsdl:binding contains an http:binding extensibility element from the 
		http://schemas.xmlsoap.org/wsdl/http/ namespace then the categoryBag MUST include a keyedReference 
		with a tModelKey of the Protocol Categorization category system and a keyValue of the tModelKey 
		of the HTTP Protocol tModel.</p>
		
		<p>Note that this is a different tModel from the HTTP Transport tModel, and in this case there is 
		no separate transport tModel, and therefore no keyedReference in the categoryBag from the Transport 
		Categorization category system.</p>
		
		<h4>2.4.2.1.3               Other wsdl:binding Extensions</h4>
		
		<p>Other wsdl:binding extensibility elements are handled in a similar fashion. It is assumed 
		that vendors who provide other bindings will provide the appropriate protocol and transport tModels.</p>
	
	 * @param wsdlURL
	 * @param binding Map
	 * @return set of WSDL Binding tModels
	 * @throws WSDLException
	 */
	public Set<TModel> createWSDLBindingTModels(String wsdlURL, Map<QName,Binding> bindings) throws WSDLException 
	{
		
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
	    	
	    	if (namespace!=null && !"".equals(namespace)) {
	    		// A keyedReference with a tModelKey of the WSDL Entity Type category system and a keyValue of "binding".
	    		KeyedReference namespaceReference = newKeyedReference(
		    			"uddi:uddi.org:xml:namespace", "uddi-org:xml:binding:namespace", namespace); 
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
	    			"uddi:uddi-org:types", "uddi-org:types", "wsdlSpec");
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
					} else if (soapBinding.getTransportURI()!=null) {
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
	 * <h3>2.4.1            wsdl:portType -> uddi:tModel</h3>

		<p>A wsdl:portType MUST be modeled as a uddi:tModel.</p>

		<p>The minimum information that must be captured about a portType is its entity type, 
		its local name, its namespace, and the location of the WSDL document that defines the 
		portType. Capturing the entity type enables users to search for tModels that represent 
		portType artifacts. Capturing the local name, namespace, and WSDL location enables users 
		to locate the definition of the specified portType artifact.</p>

		<p>The wsdl:portType information is captured as follows:</p>

		<p>The uddi:name element of the tModel MUST be the value of the name attribute of the 
		wsdl:portType.</p>

		<p>The tModel MUST contain a categoryBag, and the categoryBag MUST contain a keyedReference 
		with a tModelKey of the WSDL Entity Type category system and a keyValue of "portType".</p>

		<p>If the wsdl:portType has a targetNamespace then the categoryBag MUST also contain an 
		additional keyedReference with a tModelKey of the XML Namespace category system and a 
		keyValue of the target namespace of the wsdl:definitions element that contains the 
		wsdl:portType. If the targetNamespace is absent from the portType, a categoryBag 
		MUST NOT contain a keyedReference to the XML Namespace category system.</p>

		<p>The tModel MUST contain an overviewDoc with an overviewURL containing the location 
		of the WSDL document that describes the wsdl:portType.</p>

	 * @param wsdlURL
	 * @param portType Map
	 * @return set of WSDL PortType tModels 
	 * @throws WSDLException
	 */
	public Set<TModel> createWSDLPortTypeTModels(String wsdlURL, Map<QName,PortType> portTypes) throws WSDLException 
	{
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
	    	if (namespace!=null && !"".equals(namespace)) {
		    	KeyedReference namespaceReference = newKeyedReference(
		    			"uddi:uddi.org:xml:namespace", "uddi-org:xml:porttype:namespace", namespace); 
		    	categoryBag.getKeyedReference().add(namespaceReference);
	    	}
	    	
	    	tModel.setCategoryBag(categoryBag);
	    	tModels.add(tModel);
	    }
	    return tModels;
	}
	
	protected KeyedReference newKeyedReference(String tModelKey, String value) 
	{
		KeyedReference typesReference = new KeyedReference();
    	typesReference.setTModelKey(tModelKey);
    	typesReference.setKeyValue(value);
    	return typesReference;
	}
	
	protected KeyedReference newKeyedReference(String tModelKey, String keyName, String value) 
	{
		KeyedReference typesReference = new KeyedReference();
    	typesReference.setTModelKey(tModelKey);
    	typesReference.setKeyName(keyName);
    	typesReference.setKeyValue(value);
    	return typesReference;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	/** Builds a finder to find the binding tModels for a portType.
     * 
     * @param processName
     * @return
     */
    public FindTModel createFindBindingTModelForPortType (String portType, String namespace) {
    	
    	FindTModel findTModel = new FindTModel();
    	CategoryBag categoryBag = new CategoryBag();
    	
    	if (namespace!=null && namespace!="") {
    		KeyedReference namespaceReference = newKeyedReference(
    			"uddi:uddi.org:xml:namespace", namespace);
    		categoryBag.getKeyedReference().add(namespaceReference);
    	}
    	KeyedReference bindingReference = newKeyedReference(
    			"uddi:uddi.org:wsdl:types", "binding");
    	categoryBag.getKeyedReference().add(bindingReference);
    	
    	KeyedReference portTypeReference = newKeyedReference(
    			"uddi:uddi.org:wsdl:porttypereference", portType);
    	categoryBag.getKeyedReference().add(portTypeReference);
    	
    	findTModel.setCategoryBag(categoryBag);
    	
    	if (log.isInfoEnabled()) {
    		System.out.println(new PrintUDDI<FindTModel>().print(findTModel));
    	}
    	return findTModel;
    }
    
	/** Builds a finder to find the portType tModels for a portType.
     * 
     * @param processName
     * @return
     */
    public FindTModel createFindPortTypeTModelForPortType (String portType, String namespace) {
    	
    	FindTModel findTModel = new FindTModel();
    	Name name = new Name();
    	name.setLang(lang);
    	name.setValue(portType);
    	findTModel.setName(name);
    	
    	CategoryBag categoryBag = new CategoryBag();
    	if (namespace!=null && namespace!="") {
    		KeyedReference namespaceReference = newKeyedReference(
    			"uddi:uddi.org:xml:namespace", namespace);
    		categoryBag.getKeyedReference().add(namespaceReference);
    	}
    	KeyedReference bindingReference = newKeyedReference(
    			"uddi:uddi.org:wsdl:types", "portType");
    	categoryBag.getKeyedReference().add(bindingReference);
    	
    	KeyedReference portTypeReference = newKeyedReference(
    			"uddi:uddi.org:wsdl:porttypereference", portType);
    	categoryBag.getKeyedReference().add(portTypeReference);
    	
    	findTModel.setCategoryBag(categoryBag);
    	
    	if (log.isInfoEnabled()) {
    		System.out.println(new PrintUDDI<FindTModel>().print(findTModel));
    	}
    	return findTModel;
    }
    
    /**
	 * Perform a lookup by serviceKey, and will return null if not found.
	 * @param serviceKey
	 * @return
	 * @throws RemoteException
	 * @throws ConfigurationException
	 * @throws TransportException
	 */
	public BusinessService lookupService(String serviceKey) throws RemoteException, ConfigurationException, TransportException {
		
		//Checking if this serviceKey already exist
		BusinessService service = clerk.findService(serviceKey);
		return service;
	}
	
	/**
	 * Creates a UDDI Business Service.
	 * 
	 * @param serviceName
	 * @param wsldDefinition
	 * @return
	 */
	public BusinessService createBusinessService(QName serviceQName, Definition wsdlDefinition) {
		
		log.debug("Constructing Service UDDI Information for " + serviceQName);
		BusinessService service = new BusinessService();
		// BusinessKey
		service.setBusinessKey(businessKey);
		// ServiceKey
		service.setServiceKey(Property.getServiceKey(properties, serviceQName));
		// Description
		String serviceDescription = properties.getProperty(Property.SERVICE_DESCRIPTION, Property.DEFAULT_SERVICE_DESCRIPTION);
		// Override with the service description from the WSDL if present
		Element docElement = wsdlDefinition.getService(serviceQName).getDocumentationElement();
		if (docElement!=null && docElement.getTextContent()!=null) {
			serviceDescription = docElement.getTextContent();
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
		if (namespace!=null && namespace!="") {
    		KeyedReference namespaceReference = newKeyedReference(
    			"uddi:uddi.org:xml:namespace", "service namespace", namespace);
    		categoryBag.getKeyedReference().add(namespaceReference);
    	}
		
		KeyedReference serviceReference = newKeyedReference(
    			"uddi:uddi.org:wsdl:types", "WSDL type", "service");
    	categoryBag.getKeyedReference().add(serviceReference);
    	
    	KeyedReference localNameReference = newKeyedReference(
    			"uddi:uddi.org:xml:localname", "service local name", serviceQName.getLocalPart());
    	categoryBag.getKeyedReference().add(localNameReference);
    	
		service.setCategoryBag(categoryBag);
    	
		return service;
	}
	
	public BindingTemplate createWSDLBinding(QName serviceName, String portName, URL serviceUrl, Definition wsdlDefinition) {
			
    	BindingTemplate bindingTemplate = new BindingTemplate();
		// Set BusinessService Key
		bindingTemplate.setServiceKey(Property.getServiceKey(properties, serviceName));
		// Set Binding Key
		String bindingKey = Property.getBindingKey(properties, serviceName, portName, serviceUrl);
		bindingTemplate.setBindingKey(bindingKey);
		// Set AccessPoint
		AccessPoint accessPoint = new AccessPoint();
		accessPoint.setUseType(AccessPointType.END_POINT.toString());
		accessPoint.setValue(urlLocalizer.rewrite(serviceUrl));
		bindingTemplate.setAccessPoint(accessPoint);
		
		Service service =  wsdlDefinition.getService(serviceName);
		if (service!=null) {
			TModelInstanceDetails tModelInstanceDetails = new TModelInstanceDetails();
			
			Port port = service.getPort(portName);
			if (port!=null) {
				Binding binding = port.getBinding();
				// Set the Binding Description
				String bindingDescription = properties.getProperty(Property.BINDING_DESCRIPTION, Property.DEFAULT_BINDING_DESCRIPTION);
				// Override with the service description from the WSDL if present
				Element docElement = binding.getDocumentationElement();
				if (docElement!=null && docElement.getTextContent()!=null) {
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
				descriptionB.setValue("The wsdl:binding that this wsdl:port implements. " + bindingDescription +
						" The instanceParms specifies the port local name.");
				tModelInstanceInfoBinding.getDescription().add(descriptionB);
				tModelInstanceDetails.getTModelInstanceInfo().add(tModelInstanceInfoBinding);
				
				// reference wsdl:portType tModel
				PortType portType = binding.getPortType();
				TModelInstanceInfo tModelInstanceInfoPortType = new TModelInstanceInfo();
				tModelInstanceInfoPortType.setTModelKey(keyDomainURI + portType.getQName().getLocalPart());
				String portTypeDescription = "";
				docElement = portType.getDocumentationElement();
				if (docElement!=null && docElement.getTextContent()!=null) {
					portTypeDescription = docElement.getTextContent();
				}
				Description descriptionPT = new Description();
				descriptionPT.setLang(lang);
				descriptionPT.setValue("The wsdl:portType that this wsdl:port implements." + portTypeDescription );
				tModelInstanceInfoPortType.getDescription().add(descriptionPT);
				tModelInstanceDetails.getTModelInstanceInfo().add(tModelInstanceInfoPortType);
				
				bindingTemplate.setTModelInstanceDetails(tModelInstanceDetails);
			} else {
				log.error("Could not find Port with portName: " + portName);
			}
		} else {
			log.error("Could not find Service with serviceName: " + serviceName.getLocalPart());
		}
		
		return bindingTemplate;
	}
	
}
