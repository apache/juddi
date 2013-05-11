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
import javax.xml.namespace.QName;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.AccessPointType;
import org.apache.juddi.jaxb.PrintUDDI;
import org.apache.juddi.v3.annotations.AnnotationProcessor;
import org.apache.juddi.v3.client.config.Property;
import org.apache.juddi.v3.client.config.UDDIClerk;
import org.apache.juddi.v3.client.config.UDDIKeyConvention;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.AccessPoint;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.CategoryBag;
import org.uddi.api_v3.Description;
import org.uddi.api_v3.FindBinding;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.InstanceDetails;
import org.uddi.api_v3.KeyedReference;
import org.uddi.api_v3.Name;
import org.uddi.api_v3.OverviewDoc;
import org.uddi.api_v3.OverviewURL;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelBag;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelInfo;
import org.uddi.api_v3.TModelInstanceDetails;
import org.uddi.api_v3.TModelInstanceInfo;
import org.uddi.api_v3.TModelList;
import org.w3c.dom.Element;


/**
 * BPEL4WS abstract processes describe the observable behavior of Web services. They 
 * complement abstract WSDL interfaces (port types and operations) and the UDDI model 
 * by defining dependencies between service operations in the context of a message 
 * exchange. The technical note 'uddi-spec-tc-tn-bpel' describes the relationships 
 * between the three models and suggests how BPEL4WS abstract processes can be used 
 * in a UDDI Registry. This class implements the registrations suggestions as put 
 * forward in the technote.
 * 
 * * @author Kurt T Stam <kurt.stam@apache.org>
 *
 */
public class BPEL2UDDI extends AnnotationProcessor {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	private String keyDomainURI;
	private UDDIClerk clerk;
	private String lang;
	private URLLocalizer urlLocalizer;
	private String businessKey;
	private Properties properties = new Properties();

	private WSDL2UDDI wsdl2UDDI;
	
	public BPEL2UDDI(UDDIClerk clerk, URLLocalizer urlLocalizer, Properties properties) throws ConfigurationException {
		super();
		
		this.clerk = clerk;
		this.urlLocalizer = urlLocalizer;
		this.properties = properties;
		
		//Obtaining values from the properties
		this.keyDomainURI = "uddi:" + properties.getProperty("keyDomain") + ":";
		this.businessKey = UDDIKeyConvention.getBusinessKey(properties);
		this.lang = properties.getProperty(Property.LANG,Property.DEFAULT_LANG);
		
		this.wsdl2UDDI = new WSDL2UDDI(clerk, urlLocalizer, properties);
	}
	
	public String getKeyDomainURI() {
		return keyDomainURI;
	}

	public void setKeyDomainURI(String keyDomainURI) {
		this.keyDomainURI = keyDomainURI;
	}
	
	public UDDIClerk getClerk() {
		return clerk;
	}

	public void setClerk(UDDIClerk clerk) {
		this.clerk = clerk;
	}
	
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public URLLocalizer getUrlLocalizer() {
		return urlLocalizer;
	}

	public void setUrlLocalizer(URLLocalizer urlLocalizer) {
		this.urlLocalizer = urlLocalizer;
	}

	/**
	 * 1. Register PortType tModels
	 * 2. Register WSDL BPEL4WS Process
	 * 3. Register WSDL Port
	 * 4. Register Process Service
	 * 5. Register Binding
	 * 
	 * @param serviceName - QName of the service
	 * @param portName - portName of the service
	 * @param serviceUrl - URL at which the service can be invoked
	 * @param wsdlDefinition - WSDL Definition of the Web Service
	 * @throws WSDLException 
	 * @throws MalformedURLException 
	 * @throws TransportException 
	 * @throws ConfigurationException 
	 * @throws RemoteException 
	 */
	@SuppressWarnings("unchecked")
	public BindingTemplate register(QName serviceName, String portName, URL serviceUrl, Definition wsdlDefinition) 
		throws WSDLException, MalformedURLException, RemoteException, ConfigurationException, TransportException 
	{
		String targetNamespace = wsdlDefinition.getTargetNamespace();
		String genericWSDLURL  = wsdlDefinition.getDocumentBaseURI();   //TODO maybe point to repository version
		String bpelOverviewURL = "http://localhost:8080/bpel-console/"; //TODO maybe point to bpel in console
		
		String serviceKey = UDDIKeyConvention.getServiceKey(properties, serviceName.getLocalPart());
		BusinessService service = lookupService(serviceKey);
		if (service==null) {
			List<TModel> tModels = new ArrayList<TModel>();
			// Create the PortType tModels
			Map<QName,PortType> portTypes = (Map<QName,PortType>) wsdlDefinition.getAllPortTypes();
			tModels.addAll(createWSDLPortTypeTModels(genericWSDLURL, portTypes));
			// Create the Binding tModels
			Map<QName,Binding> bindings = (Map<QName,Binding>) wsdlDefinition.getAllBindings();
			tModels.addAll(createWSDLBindingTModels(genericWSDLURL, bindings));
			// Create the BPEL4WS tModel
			TModel bpel4WSTModel = createBPEL4WSProcessTModel(serviceName, targetNamespace, portTypes, bpelOverviewURL);
		    tModels.add(bpel4WSTModel);
		    // Register these tModels
		    for (TModel tModel : tModels) {
				clerk.register(tModel);
			}
		    // BPEL Service
		    service = createBusinessService(serviceName, wsdlDefinition);
		    // Register this BPEL Service
		    clerk.register(service);
		}
		//Add the BindingTemplate to this Service
		BindingTemplate binding = createBPELBinding(serviceName, portName, serviceUrl, wsdlDefinition);
		// Register BindingTemplate
		clerk.register(binding);
		return binding;
	}
	
	public String unRegister(QName serviceName, String portName, URL serviceUrl) throws RemoteException, ConfigurationException, TransportException {
		
		String serviceKey = UDDIKeyConvention.getServiceKey(properties, serviceName.getLocalPart());
		BusinessService service = lookupService(serviceKey);
		boolean isRemoveServiceIfNoTemplates = true; 
		String bindingKey = UDDIKeyConvention.getBindingKey(properties, serviceName, portName, serviceUrl);
		//check if this bindingKey is in the service's binding templates
		for (BindingTemplate bindingTemplate : service.getBindingTemplates().getBindingTemplate()) {
			if (bindingKey.equals(bindingTemplate.getBindingKey())) {
				clerk.unRegisterBinding(bindingKey);
				//if this is the last binding for this service, and 
				if (service.getBindingTemplates().getBindingTemplate().size()==1 && isRemoveServiceIfNoTemplates) {
					clerk.unRegisterService(serviceKey);
					
					FindTModel findTmodelForProcessName = createFindTModelForProcessName(serviceName);
					TModelList tModelList = clerk.findTModel(findTmodelForProcessName);
					if (tModelList!=null && tModelList.getTModelInfos()!=null && tModelList.getTModelInfos().getTModelInfo()!=null) {
						TModelInfo tModelInfo = tModelList.getTModelInfos().getTModelInfo().get(0);
						String bpel4WSTModelKey = tModelInfo.getTModelKey();
						clerk.unRegisterTModel(bpel4WSTModelKey);
						// now use this key to find the portType TModels
						GetTModelDetail findAllPortTypesForProcess = createFindAllPortTypesForProcess_1(bpel4WSTModelKey);
						TModelDetail tModelDetail = clerk.getTModelDetail(findAllPortTypesForProcess);
						if (tModelDetail!=null) {
							List<TModel> tModelPortTypeList = tModelDetail.getTModel();
							if (tModelPortTypeList!=null && tModelPortTypeList.size()>0) {
								TModel bpel4WSTModel = tModelPortTypeList.get(0);
								CategoryBag categoryBag = bpel4WSTModel.getCategoryBag();
								if (categoryBag!=null && categoryBag.getKeyedReference()!=null) {
									List<KeyedReference> portTypeTModelKeys = new ArrayList<KeyedReference>();
									KeyedReference namespaceRef = null;
									for (KeyedReference keyedReference : categoryBag.getKeyedReference()) {
										if ("uddi:uddi.org:wsdl:porttypereference".equals(keyedReference.getTModelKey()) ) {
											portTypeTModelKeys.add(keyedReference);
										}
										if ("uddi:uddi.org:xml:namespace".equals(keyedReference.getTModelKey()) ) {
											namespaceRef = keyedReference;
										}
									}
									String namespace = null;
									if (namespaceRef!=null) namespace = namespaceRef.getKeyValue();
									//find the bindingTModel
									for (KeyedReference keyedReference : portTypeTModelKeys) {
										FindTModel findBindingTModel = WSDL2UDDI.createFindBindingTModelForPortType(keyedReference.getKeyValue(), namespace);
										TModelList bindingTmodels = clerk.findTModel(findBindingTModel);
										if (bindingTmodels!=null && bindingTmodels.getTModelInfos()!=null && bindingTmodels.getTModelInfos().getTModelInfo()!=null) {
											for (TModelInfo bindingTModelInfo : bindingTmodels.getTModelInfos().getTModelInfo()) {
												//delete the Binding TModel
												clerk.unRegisterTModel(bindingTModelInfo.getTModelKey());
											}
										}
										//delete the PortType TModel
										clerk.unRegisterTModel(keyedReference.getKeyValue());	
									}
 								}
							}
						}
					}
				}
				break;
			}
		}
		return service.getServiceKey();	
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
	 * Registers the Service into UDDI.
	 * 
	 * @param serviceName
	 * @param wsldDefinition
	 * @return
	 */
	public BusinessService createBusinessService(QName serviceName, Definition wsdlDefinition) {
		
		log.debug("Constructing Service UDDI Information for " + serviceName);
		BusinessService service = new BusinessService();
		// BusinessKey
		service.setBusinessKey(businessKey);
		// ServiceKey
		service.setServiceKey(UDDIKeyConvention.getServiceKey(properties, serviceName.getLocalPart()));
		// Description
		String serviceDescription = properties.getProperty(Property.SERVICE_DESCRIPTION, Property.DEFAULT_SERVICE_DESCRIPTION);
		if (wsdlDefinition.getService(serviceName) !=null) {
			// Override with the service description from the WSDL if present
			Element docElement = wsdlDefinition.getService(serviceName).getDocumentationElement();
			if (docElement!=null && docElement.getTextContent()!=null) {
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
		sName.setValue(serviceName.getLocalPart());
		service.getName().add(sName);
		
		//customization to add KeyedReferences into the categoryBag of the service
		if (properties.containsKey(Property.SERVICE_CATEGORY_BAG)) {
			String serviceCategoryBag = properties.getProperty(Property.SERVICE_CATEGORY_BAG);
			log.debug("Adding KeyedReferences '" +  serviceCategoryBag + "' to service " + serviceName.getLocalPart());
			CategoryBag categoryBag = parseCategoryBag(serviceCategoryBag);
	        service.setCategoryBag(categoryBag);
		}
		
		return service;
	}
	
	public Set<TModel> createWSDLPortTypeTModels(String wsdlURL, Map<QName,PortType> portTypes) throws WSDLException 
	{
		return wsdl2UDDI.createWSDLPortTypeTModels(wsdlURL, portTypes);
	}
	
	public Set<TModel> createWSDLBindingTModels(String wsdlURL, Map<QName,Binding> bindings) throws WSDLException 
	{
		return wsdl2UDDI.createWSDLBindingTModels(wsdlURL, bindings);
	}
	
	/**
	 * BPEL4WS abstract processes are published as separate UDDI tModels. They are named with the BPEL4WS process 
	 * name. They are categorized as BPEL4WS process definitions, using a category system defined in this 
	 * technical note. Their overviewDoc references an external BPEL4WS document that contains the process definition.
	 * All WSDL portTypes that are used in the BPEL4WS process definition (via the referenced BPEL4WS partnerLinkTypes) 
	 * are published as portType tModels according to [WSDL2UDDI]. The process tModel references all such WSDL portType
	 * tModels, using the WSDL portType Reference tModel defined in [WSDL2UDDI]. Note that it is a characteristic 
	 * of the BPEL4WS process that it defines a conversation based on WSDL portTypes. Thus, the relationship 
	 * between process tModel and portType tModel is to be published by the process tModel publisher, not by 
	 * the portType tModel publisher, which may be a different person.
	 * 
	 * In the current implementation it is all registered by the same publisher. 
	 * 
	 * @param serviceName
	 * @param targetNamespace
	 * @param portTypes
	 * @param bpelOverviewURL
	 * @return
	 */
    public TModel createBPEL4WSProcessTModel(QName serviceName, String targetNamespace, Map<QName,PortType> portTypes, String bpelOverviewURL) {
    	TModel tModel = new TModel();
    	// Set the Key
    	tModel.setTModelKey(keyDomainURI + serviceName.getLocalPart() + "Process");
    	// Set the Name
    	Name name = new Name();
    	name.setLang("en");
    	name.setValue(serviceName.getLocalPart());
    	tModel.setName(name);
    	// Set the OverviewURL
    	OverviewURL overviewURL = new OverviewURL();
    	overviewURL.setValue("http://localhost:8080/bpel-console/"); //should point to the bpel of this process, maybe in guvnor
    	OverviewDoc overviewDoc = new OverviewDoc();
    	overviewDoc.setOverviewURL(overviewURL);
    	tModel.getOverviewDoc().add(overviewDoc);
    	// Set the categoryBag
    	CategoryBag categoryBag = new CategoryBag();
    	
    	if (targetNamespace!=null) {
    		KeyedReference namespaceReference = WSDL2UDDI.newKeyedReference(
    			"uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", targetNamespace);
    		categoryBag.getKeyedReference().add(namespaceReference);
    	}
    	KeyedReference typesReference = WSDL2UDDI.newKeyedReference(
    			"uddi:uddi.org:bpel:types", "uddi-org:bpel:types", "process");
    	categoryBag.getKeyedReference().add(typesReference);
    	for (QName qName : portTypes.keySet()) {
    		String portTypeKey = keyDomainURI + qName.getLocalPart();
	    	KeyedReference portTypeReference = WSDL2UDDI.newKeyedReference(
	    			"uddi:uddi.org:wsdl:porttypereference", "uddi-org:wsdl:portTypeReference", portTypeKey);
	    	categoryBag.getKeyedReference().add(portTypeReference);
    	}
    	
    	tModel.setCategoryBag(categoryBag);
    	
    	if (log.isDebugEnabled()) {
    		log.debug(new PrintUDDI<TModel>().print(tModel));
    	}
    	
    	return tModel;
    }
    
    public BindingTemplate createBPELBinding(QName serviceName, String portName, URL serviceUrl, Definition wsdlDefinition) {
		
    	BindingTemplate bindingTemplate = new BindingTemplate();
		// Set BusinessService Key
		bindingTemplate.setServiceKey(UDDIKeyConvention.getServiceKey(properties, serviceName.getLocalPart()));
		// Set Binding Key
		String bindingKey = UDDIKeyConvention.getBindingKey(properties, serviceName, portName, serviceUrl);
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
				
				//reference bpel:process tModel
				TModelInstanceInfo tModelInstanceInfoBPEL = new TModelInstanceInfo();
				tModelInstanceInfoBPEL.setTModelKey(keyDomainURI + service.getQName().getLocalPart() + "Process");
				Description descriptionBPEL = new Description();
				// Description
				String serviceDescription = properties.getProperty(Property.SERVICE_DESCRIPTION, Property.DEFAULT_SERVICE_DESCRIPTION);
				// Override with the service description from the WSDL if present
				docElement = wsdlDefinition.getService(serviceName).getDocumentationElement();
				if (docElement!=null && docElement.getTextContent()!=null) {
					serviceDescription = docElement.getTextContent();
				}
				descriptionBPEL.setLang(lang);
				descriptionBPEL.setValue("The bpel:process this wsdl:port supports." + serviceDescription);
				tModelInstanceInfoBPEL.getDescription().add(descriptionBPEL);
				tModelInstanceDetails.getTModelInstanceInfo().add(tModelInstanceInfoBPEL);
				
				bindingTemplate.setTModelInstanceDetails(tModelInstanceDetails);
			} else {
				log.error("Could not find Port with portName: " + portName);
			}
		} else {
			log.error("Could not find Service with serviceName: " + serviceName.getLocalPart());
		}
		
		if (log.isDebugEnabled()) {
    		log.debug(new PrintUDDI<BindingTemplate>().print(bindingTemplate));
    	}
		
		return bindingTemplate;
	}
    
    /** Finds and returns ALL the tModels related to the process, so that i.e. they
     * can be removed on undeployment of the service.
     * 
     * @param processName
     * @return
     */
    public FindTModel createFindTModelForProcessName (QName serviceName) {
    	
    	FindTModel findTModel = new FindTModel();
    	Name name = new Name();
    	//name.setLang(lang);
    	name.setValue(serviceName.getLocalPart());
    	findTModel.setName(name);
    	CategoryBag categoryBag = new CategoryBag();
    	
    	String namespace = serviceName.getNamespaceURI();
    	if (namespace!=null && namespace!="") {
    		KeyedReference namespaceReference = WSDL2UDDI.newKeyedReference(
    			"uddi:uddi.org:xml:namespace", "uddi-org:xml:namespace", namespace);
    		categoryBag.getKeyedReference().add(namespaceReference);
    	}
    	KeyedReference typesReference = WSDL2UDDI.newKeyedReference(
    			"uddi:uddi.org:bpel:types", "uddi-org:bpel:types", "process");
    	categoryBag.getKeyedReference().add(typesReference);
    	findTModel.setCategoryBag(categoryBag);
    	
    	if (log.isDebugEnabled()) {
    		log.debug(new PrintUDDI<FindTModel>().print(findTModel));
    	}
    	return findTModel;
    }
    /**
     * Find all processes that use the given portType.
     * 
     * @param portTypeKey
     * @return
     */
    public FindTModel createFindProcessesForPortTypes(String portTypeKey) {
    	FindTModel findTModel = new FindTModel();
    	CategoryBag categoryBag = new CategoryBag();
    	
    	KeyedReference typesReference = WSDL2UDDI.newKeyedReference(
    			"uddi:uddi.org:bpel:types", "uddi-org:bpel:types", "process");
    	categoryBag.getKeyedReference().add(typesReference);
    	
    	KeyedReference portTypeReference = WSDL2UDDI.newKeyedReference(
    			"uddi:uddi.org:wsdl:porttypereference", "uddi-org:wsdl:portTypeReference", portTypeKey);
    	categoryBag.getKeyedReference().add(portTypeReference);
    	
    	findTModel.setCategoryBag(categoryBag);
    	
    	return findTModel;
    }
    /**
     * Find all portTypes used in the given process. This should return the 
     * tModel registration for the process tModel. The tModelKeys for the 
     * portTypes used in the process can be obtained from the process tModels 
     * categoryBag, and passed into the second call.
     * 
     * @param processKey
     * @return GetTModelDetail
     */
    public GetTModelDetail createFindAllPortTypesForProcess_1(String processKey) {
    	GetTModelDetail getTModelDetail = new GetTModelDetail();
    	getTModelDetail.getTModelKey().add(processKey);
    	return getTModelDetail;
    }
    /**
     * Once retrieved, the second call is made to get the tModel registrations 
     * for the portTypes with the keys found in the first step.
     * 
     * @param tModelKeys - List of portType tModels found in the first step.
     * @return GetTModelDetail
     */
    public GetTModelDetail createFindAllPortTypesForProcess_2(List<String> portTypeTModelKeys) {
    	GetTModelDetail getTModelDetail = new GetTModelDetail();
    	for (String tModelKey : portTypeTModelKeys) {
    		getTModelDetail.getTModelKey().add(tModelKey);
    	}
    	return getTModelDetail;
    }
    /**
     * Find all implementations of the given process.
     * @param processKey
     * @return FindBinding
     */
    public FindBinding createFindImplementationsForProcess(String processKey) {
    	FindBinding findBinding = new FindBinding();
    	TModelBag tModelBag = new TModelBag();
    	tModelBag.getTModelKey().add(processKey);
    	return findBinding;
    }
	
}
