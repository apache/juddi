/*
 * Copyright 2001-2010 The Apache Software Foundation.
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
package org.apache.juddi.v3.client.config;

import java.io.Serializable;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.api_v3.Clerk;
import org.apache.juddi.api_v3.ClerkDetail;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.api_v3.NodeDetail;
import org.apache.juddi.api_v3.SaveClerk;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.v3.client.mapping.ReadWSDL;
import org.apache.juddi.v3.client.mapping.URLLocalizerDefaultImpl;
import org.apache.juddi.v3.client.mapping.WSDL2UDDI;
import org.apache.juddi.v3.client.transport.TransportException;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DeleteBusiness;
import org.uddi.api_v3.DeleteService;
import org.uddi.api_v3.DeleteTModel;
import org.uddi.api_v3.DiscardAuthToken;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.FindTModel;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.GetTModelDetail;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.Result;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.SaveTModel;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.api_v3.TModel;
import org.uddi.api_v3.TModelDetail;
import org.uddi.api_v3.TModelList;
import org.uddi.sub_v3.DeleteSubscription;
import org.uddi.sub_v3.Subscription;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class UDDIClerk implements Serializable {

	private static final long serialVersionUID = -8597375975981358134L;
	private Log log = LogFactory.getLog(this.getClass());
	
	protected String name;
	protected UDDINode uddiNode;
	protected String publisher;
	protected String password;
	
	private Date tokenBirthDate;
	private String authToken;
	private String[] classWithAnnotations;
	private WSDL[] wsdls;
	private String managerName;

	private Map<String,Properties> services = new HashMap<String,Properties>(); 

	
	
	public UDDIClerk() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public UDDIClerk(Clerk clerk) {
		super();
		this.name = clerk.getName();
		this.password = clerk.getPassword();
		this.publisher = clerk.getPublisher();
		this.uddiNode = new UDDINode(clerk.getNode());
	}

	public String[] getClassWithAnnotations() {
		return classWithAnnotations;
	}

	public void setClassWithAnnotations(String[] classWithAnnotations) {
		this.classWithAnnotations = classWithAnnotations;
	}

	public Map<String, Properties> getServices() {
		return services;
	}

	public void setServices(Map<String, Properties> services) {
		this.services = services;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	
	public void registerWsdls() {
		if (this.getWsdls()!=null) {
			Properties properties = new Properties();
			properties.putAll(this.getUDDINode().getProperties());
			
			for (WSDL wsdl : this.getWsdls()) {
				try {
					URL wsdlUrl = this.getClass().getClassLoader().getResource(wsdl.getFileName());
					ReadWSDL rw = new ReadWSDL();
					Definition wsdlDefinition = rw.readWSDL(wsdlUrl);
					if (wsdl.keyDomain!=null) properties.setProperty("keyDomain", wsdl.keyDomain);
					if (wsdl.businessKey!=null) properties.setProperty("businessKey", wsdl.getBusinessKey());
					
					WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(this, new URLLocalizerDefaultImpl(), properties);
					wsdl2UDDI.registerBusinessServices(wsdlDefinition);
				} catch (Exception e) {
					log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + e.getMessage(),e);
				} catch (Throwable t) {
					log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + t.getMessage(),t);
				}
			}
		}
	}
	
	public void unRegisterWsdls() {
		if (this.getWsdls()!=null) {
			Properties properties = new Properties();
			properties.putAll(this.getUDDINode().getProperties());
			
			for (WSDL wsdl : this.getWsdls()) {
				try {
					URL wsdlUrl = this.getClass().getClassLoader().getResource(wsdl.getFileName());
					ReadWSDL rw = new ReadWSDL();
					Definition wsdlDefinition = rw.readWSDL(wsdlUrl);
					if (wsdl.keyDomain!=null) properties.setProperty("keyDomain", wsdl.keyDomain);
					if (wsdl.businessKey!=null) properties.setProperty("businessKey", wsdl.getBusinessKey());
					
					WSDL2UDDI wsdl2UDDI = new WSDL2UDDI(this, new URLLocalizerDefaultImpl(), properties);
					wsdl2UDDI.unRegisterBusinessServices(wsdlDefinition);
				} catch (Exception e) {
					log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + e.getMessage(),e);
				} catch (Throwable t) {
					log.error("Unable to register wsdl " + wsdl.getFileName() + " ." + t.getMessage(),t);
				}
			}
		}
	}
	
	public Subscription register(Subscription subscription) {
		return register(subscription, this.getUDDINode().getApiNode());
	}
	/**
	 * Register a Subscription.
	 */
	public Subscription register(Subscription subscription, Node node) {
		
		log.info("Registering subscription with key " + subscription.getSubscriptionKey());
		Holder<List<Subscription>> holder = new Holder<List<Subscription>>();
		try {
			String authToken = getAuthToken(node.getSecurityUrl());
			
			List<Subscription> subscriptions = new ArrayList<Subscription>();
			subscriptions.add(subscription);
			holder.value = subscriptions;
			getUDDINode().getTransport().getUDDISubscriptionService(node.getSubscriptionUrl()).saveSubscription(authToken, holder);
			if (log.isDebugEnabled()) log.debug("Registering subscription " +  subscription.getSubscriptionKey() + " completed.");
		} catch (Exception e) {
			log.error("Unable to register subscription " +  subscription.getSubscriptionKey()
					+ " ." + e.getMessage(),e);
		} catch (Throwable t) {
			log.error("Unable to register subscriptionl " +  subscription.getSubscriptionKey()
					+ " ." + t.getMessage(),t);
		}
		subscription = holder.value.get(0);
		return subscription;
	}
	/**
	 * Register a tModel, using the node of current clerk ('this').
	 * 
	 * @param tModel
	 * @return the TModelDetail of the newly registered TModel
	 */
	public TModelDetail register(TModel tModel) {
		return register(tModel, this.getUDDINode().getApiNode());
	}
	/**
	 * Register a tModel.
	 */
	public TModelDetail register(TModel tModel, Node node) {
		TModelDetail tModelDetail = null;
		log.info("Registering tModel with key " + tModel.getTModelKey());
		try {
			String authToken = getAuthToken(node.getSecurityUrl());
			SaveTModel saveTModel = new SaveTModel();
			saveTModel.setAuthInfo(authToken);
			saveTModel.getTModel().add(tModel);
			tModelDetail = getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).saveTModel(saveTModel);
			if (log.isDebugEnabled()) log.debug("Registering tModel " +  tModel.getTModelKey() + " completed.");
		} catch (Exception e) {
			log.error("Unable to register tModel " +  tModel.getTModelKey()
					+ " ." + e.getMessage(),e);
		} catch (Throwable t) {
			log.error("Unable to register tModel " +  tModel.getTModelKey()
					+ " ." + t.getMessage(),t);
		}
		return tModelDetail;
	}
	
	/**
	 * Register a BindingTemplate, using the node of current clerk ('this').
	 * 
	 */
	public BindingTemplate register(BindingTemplate binding) {
		return register(binding, this.getUDDINode().getApiNode());
	}
	/**
	 * Register a BindingTemplate.
	 * 
	 */
	public BindingTemplate register(BindingTemplate binding, Node node) {
		
		BindingTemplate bindingTemplate=null;
		log.info("Registering bindingTemplate with key " + binding.getBindingKey());
		try {
			String authToken = getAuthToken(node.getSecurityUrl());
			SaveBinding saveBinding = new SaveBinding();
			saveBinding.setAuthInfo(authToken);
			saveBinding.getBindingTemplate().add(binding);
			BindingDetail bindingDetail = getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).saveBinding(saveBinding);
			bindingTemplate = bindingDetail.getBindingTemplate().get(0);
			if (log.isDebugEnabled()) log.debug("Registering template binding " + binding.getBindingKey() + " completed.");
		} catch (Exception e) {
			log.error("Unable to register template binding " + bindingTemplate.getBindingKey()
					+ " ." + e.getMessage(),e);
		} catch (Throwable t) {
			log.error("Unable to register template binding " + bindingTemplate.getBindingKey()
					+ " ." + t.getMessage(),t);
		}
		return bindingTemplate;
	}
	/**
	 * Register a service, using the node of current clerk ('this').
	 * 
	 */
	public BusinessService register(BusinessService service) {
		return register(service, this.getUDDINode().getApiNode());
	}
	/**
	 * Register a service.
	 * 
	 */
	public BusinessService register(BusinessService service, Node node) {
		
		BusinessService businessService=null;
		log.info("Registering service " + service.getName().get(0).getValue()
				+ " with key " + service.getServiceKey());
		try {
			String authToken = getAuthToken(node.getSecurityUrl());
			SaveService saveService = new SaveService();
			saveService.setAuthInfo(authToken);
			saveService.getBusinessService().add(service);
			ServiceDetail serviceDetail = getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).saveService(saveService);
			businessService = serviceDetail.getBusinessService().get(0);
			if (log.isDebugEnabled()) log.debug("Registering service " + service.getName().get(0).getValue() + " completed.");
		} catch (Exception e) {
			log.error("Unable to register service " + service.getName().get(0).getValue()
					+ " ." + e.getMessage(),e);
		} catch (Throwable t) {
			log.error("Unable to register service " + service.getName().get(0).getValue()
					+ " ." + t.getMessage(),t);
		}
		return businessService;
	}
	public BusinessEntity register(BusinessEntity business) {
		return register(business, this.getUDDINode().getApiNode());
	}
	/**
	 * Register a service.
	 * returns null if not successful 
	 */
	public BusinessEntity register(BusinessEntity business, Node node) {
		
		BusinessEntity businessEntity=null;
		log.info("Registering business " + business.getName().get(0).getValue()
				+ " with key " + business.getBusinessKey());
		try {
			String authToken = getAuthToken(node.getSecurityUrl());
			SaveBusiness saveBusiness = new SaveBusiness();
			saveBusiness.setAuthInfo(authToken);
			saveBusiness.getBusinessEntity().add(business);
			BusinessDetail businessDetail = getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).saveBusiness(saveBusiness);
			businessEntity = businessDetail.getBusinessEntity().get(0);
			if (log.isDebugEnabled()) log.debug("Registering businessEntity " + businessEntity.getName().get(0).getValue() + " completed.");
		} catch (Exception e) {
			log.error("Unable to register business " + business.getName().get(0).getValue()
					+ " ." + e.getMessage(),e);
		} catch (Throwable t) {
			log.error("Unable to register business " + business.getName().get(0).getValue()
					+ " ." + t.getMessage(),t);
		}
		return businessEntity;
	}
	
	public void unRegisterBusiness(String businessKey) {
		unRegisterBusiness(businessKey, this.getUDDINode().getApiNode());
	}
	/**
	 * Unregisters the service with specified serviceKey.
	 * @param service
	 */
	public void unRegisterBusiness(String businessKey, Node node) {
		log.info("UnRegistering the business " + businessKey);
		try {
			String authToken = getAuthToken(node.getSecurityUrl()); 
			DeleteBusiness deleteBusiness = new DeleteBusiness();
			deleteBusiness.setAuthInfo(authToken);
			deleteBusiness.getBusinessKey().add(businessKey);
			getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).deleteBusiness(deleteBusiness);
		} catch (Exception e) {
			log.error("Unable to register service " + businessKey
					+ " ." + e.getMessage(),e);
		}
	}
	
	public void unRegisterService(String serviceKey) {
		unRegisterService(serviceKey, this.getUDDINode().getApiNode());
	}
	/**
	 * Unregisters the service with specified serviceKey.
	 * @param service
	 */
	public void unRegisterService(String serviceKey, Node node) {
		log.info("UnRegistering the service " + serviceKey);
		try {
			String authToken = getAuthToken(node.getSecurityUrl()); 
			DeleteService deleteService = new DeleteService();
			deleteService.setAuthInfo(authToken);
			deleteService.getServiceKey().add(serviceKey);
			getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).deleteService(deleteService);
		} catch (Exception e) {
			log.error("Unable to register service " + serviceKey
					+ " ." + e.getMessage(),e);
		}
	}
	public void unRegisterBinding(String bindingKey) {
		unRegisterBinding(bindingKey, this.getUDDINode().getApiNode());
	}
	/**
	 * Unregisters the BindingTemplate with specified bindingKey. 
	 * @param bindingTemplate
	 * @param node
	 */
	public void unRegisterBinding(String bindingKey, Node node) {
		log.info("UnRegistering binding key " + bindingKey);
		try {
			String authToken = getAuthToken(node.getSecurityUrl());
			DeleteBinding deleteBinding = new DeleteBinding();
			deleteBinding.setAuthInfo(authToken);
			deleteBinding.getBindingKey().add(bindingKey);
			getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).deleteBinding(deleteBinding);
		} catch (Exception e) {
			log.error("Unable to unregister bindingkey " + bindingKey
					+ " ." + e.getMessage(),e);
		}
	}
	
	public void unRegisterTModel(String tModelKey) {
		unRegisterTModel(tModelKey, this.getUDDINode().getApiNode());
	}
	/**
	 * Unregisters the BindingTemplate with specified bindingKey. 
	 * @param bindingTemplate
	 * @param node
	 */
	public void unRegisterTModel(String tModelKey, Node node) {
		log.info("UnRegistering tModel key " + tModelKey);
		try {
			String authToken = getAuthToken(node.getSecurityUrl());
			DeleteTModel deleteTModel = new DeleteTModel();
			deleteTModel.setAuthInfo(authToken);
			deleteTModel.getTModelKey().add(tModelKey);
			getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).deleteTModel(deleteTModel);
		} catch (Exception e) {
			log.error("Unable to unregister tModelkey " + tModelKey
					+ " ." + e.getMessage(),e);
		}
	}
	
	public void unRegisterSubscription(String subscriptionKey) {
		unRegisterSubscription(subscriptionKey, this.getUDDINode().getApiNode());
	}
	
	public void unRegisterSubscription(String subscriptionKey, Node node) {
		log.info("UnRegistering subscription with key " + subscriptionKey);
		try {
			String authToken = getAuthToken(node.getSecurityUrl());
			DeleteSubscription deleteSubscription = new DeleteSubscription();
			deleteSubscription.setAuthInfo(authToken);
			deleteSubscription.getSubscriptionKey().add(subscriptionKey);
			getUDDINode().getTransport().getUDDISubscriptionService(node.getSubscriptionUrl()).deleteSubscription(deleteSubscription);
		} catch (Exception e) {
			log.error("Unable to unregister subscription key " + subscriptionKey
					+ " ." + e.getMessage(),e);
		}
	}
	
	
	public TModelList findTModel(FindTModel findTModel) throws RemoteException, ConfigurationException, TransportException {
		return findTModel(findTModel, this.getUDDINode().getApiNode());
	}
			
	public TModelList findTModel(FindTModel findTModel, Node node) throws RemoteException, 
	TransportException, ConfigurationException  {
		
		findTModel.setAuthInfo(getAuthToken(node.getSecurityUrl()));
		try {
			TModelList tModelList = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).findTModel(findTModel);
			return tModelList;
		} catch (DispositionReportFaultMessage dr) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
			checkForErrorInDispositionReport(report, null, null);
		} catch (SOAPFaultException sfe) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
			checkForErrorInDispositionReport(report, null, null);
		} catch (UndeclaredThrowableException ute) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
			checkForErrorInDispositionReport(report, null, null);
		}
		return null;
	}
	
	public TModelDetail getTModelDetail(String tModelKey) throws RemoteException, ConfigurationException, TransportException {
		GetTModelDetail getTModelDetail = new GetTModelDetail();
		getTModelDetail.getTModelKey().add(tModelKey);
		return getTModelDetail(getTModelDetail);
	}
	
	public TModelDetail getTModelDetail(GetTModelDetail getTModelDetail) throws RemoteException, ConfigurationException, TransportException {
		return getTModelDetail(getTModelDetail, this.getUDDINode().getApiNode());
	}
	
	public TModelDetail getTModelDetail(GetTModelDetail getTModelDetail, Node node) throws RemoteException, 
	TransportException, ConfigurationException  {
		
		getTModelDetail.setAuthInfo(getAuthToken(node.getSecurityUrl()));
		try {
			TModelDetail tModelDetail = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).getTModelDetail(getTModelDetail);
			return tModelDetail;
		} catch (DispositionReportFaultMessage dr) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
			checkForErrorInDispositionReport(report, null, null);
		} catch (SOAPFaultException sfe) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
			checkForErrorInDispositionReport(report, null, null);
		} catch (UndeclaredThrowableException ute) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
			checkForErrorInDispositionReport(report, null, null);
		}
		return null;
	}
   
	public BusinessService findService(String serviceKey) throws RemoteException, 
	TransportException, ConfigurationException  {
		return findService(serviceKey, this.getUDDINode().getApiNode());
	}
	
	public BusinessService findService(String serviceKey, Node node) throws RemoteException, 
	TransportException, ConfigurationException  {
		GetServiceDetail getServiceDetail = new GetServiceDetail();
		getServiceDetail.getServiceKey().add(serviceKey);
		getServiceDetail.setAuthInfo(getAuthToken(node.getSecurityUrl()));
		try {
			ServiceDetail sd = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).getServiceDetail(getServiceDetail);
			List<BusinessService> businessServiceList = sd.getBusinessService();
			if (businessServiceList.size() == 0) throw new ConfigurationException("Could not find Service with key=" + serviceKey);
			return businessServiceList.get(0);
		} catch (DispositionReportFaultMessage dr) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, serviceKey);
		} catch (SOAPFaultException sfe) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, serviceKey);
		} catch (UndeclaredThrowableException ute) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, serviceKey);
		}
		return null;
	}
	
	public BindingTemplate findServiceBinding(String bindingKey) throws DispositionReportFaultMessage, RemoteException, 
	TransportException, ConfigurationException  {
		return findServiceBinding(bindingKey, this.getUDDINode().getApiNode());
	}
	
	public BindingTemplate findServiceBinding(String bindingKey, Node node) throws DispositionReportFaultMessage, RemoteException, 
			TransportException, ConfigurationException  {
		GetBindingDetail getBindingDetail = new GetBindingDetail();
		getBindingDetail.getBindingKey().add(bindingKey);
		getBindingDetail.setAuthInfo(getAuthToken(node.getSecurityUrl()));
		try {
			BindingDetail bd = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).getBindingDetail(getBindingDetail);
			List<BindingTemplate> bindingTemplateList = bd.getBindingTemplate();
			if (bindingTemplateList.size() == 0) throw new ConfigurationException("Could not find ServiceBinding with key=" + bindingKey);
			return bindingTemplateList.get(0);
		} catch (DispositionReportFaultMessage dr) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, bindingKey);
		} catch (SOAPFaultException sfe) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, bindingKey);
		} catch (UndeclaredThrowableException ute) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, bindingKey);
		}
		return null;
	}
	
	public BusinessEntity findBusiness(String businessKey) throws RemoteException, 
	TransportException, ConfigurationException  {
		return findBusiness(businessKey, this.getUDDINode().getApiNode());
	}
	
	/**
	 * Looks up the BusinessEntiry in the registry, will return null if is not found.
	 * 
	 * @param businessKey - the key we are looking for
	 * @param node - the node which is going to be queried
	 * @return BusinessEntity is found, or null if not found.
	 * @throws RemoteException
	 * @throws TransportException
	 * @throws ConfigurationException
	 */
	public BusinessEntity findBusiness(String businessKey, Node node) throws RemoteException, 
			TransportException, ConfigurationException  {
		GetBusinessDetail getBusinessDetail = new GetBusinessDetail();
		getBusinessDetail.getBusinessKey().add(businessKey);
		getBusinessDetail.setAuthInfo(node.getSecurityUrl());
		try {
			BusinessDetail bd = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).getBusinessDetail(getBusinessDetail);
			return bd.getBusinessEntity().get(0);
		} catch (DispositionReportFaultMessage dr) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
		} catch (SOAPFaultException sfe) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
		} catch (UndeclaredThrowableException ute) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
		}
		return null;
	}
	
	/**
	 * Looks up the BusinessEntiry in the registry, will return null if is not found.
	 * 
	 * @param businessKey - the key we are looking for
	 * @param node - the node which is going to be queried
	 * @return BusinessEntity is found, or null if not found.
	 * @throws RemoteException
	 * @throws TransportException
	 * @throws ConfigurationException
	 */
	public RelatedBusinessesList findRelatedBusinesses(String businessKey, Node node) throws RemoteException, 
			TransportException, ConfigurationException  {
		FindRelatedBusinesses findRelatedBusinesses = new FindRelatedBusinesses();
		findRelatedBusinesses.setBusinessKey(businessKey);
		findRelatedBusinesses.setAuthInfo(node.getSecurityUrl());
		try {
			RelatedBusinessesList rbl = getUDDINode().getTransport().getUDDIInquiryService(node.getInquiryUrl()).findRelatedBusinesses(findRelatedBusinesses);
			return rbl;
		} catch (DispositionReportFaultMessage dr) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(dr);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
		} catch (SOAPFaultException sfe) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(sfe);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
		} catch (UndeclaredThrowableException ute) {
			DispositionReport report = DispositionReportFaultMessage.getDispositionReport(ute);
			checkForErrorInDispositionReport(report, DispositionReport.E_INVALID_KEY_PASSED, businessKey);
		}
		return null;
	}
	
	private void checkForErrorInDispositionReport(DispositionReport report, String Error, String entityKey) {
		
		if (entityKey!=null && report!=null && report.countainsErrorCode(DispositionReport.E_INVALID_KEY_PASSED)) {
			log.info("entityKey " + entityKey + " was not found in the registry");
		} else {
			if (report == null) {
				log.info("Missing DispositionReport");
			} else {
				for (Result result : report.getResult()) {
					log.error(result.getErrInfo().getErrCode() + " " + result.getErrInfo().getValue());
				}
			}
		}
	}
	
	private String getAuthToken(String endpointURL) throws TransportException, DispositionReportFaultMessage, RemoteException {
		//if the token is older then 10 minutes discard it, and create a new one.
		if ((authToken!=null && !"".equals(authToken)) && (tokenBirthDate !=null && System.currentTimeMillis() > tokenBirthDate.getTime() + 600000 )) {
			DiscardAuthToken discardAuthToken = new DiscardAuthToken();
			discardAuthToken.setAuthInfo(authToken);
			getUDDINode().getTransport().getUDDISecurityService(endpointURL).discardAuthToken(discardAuthToken);
			authToken=null;
		}
		if (authToken==null || "".equals(authToken)) {
			tokenBirthDate = new Date();
			GetAuthToken getAuthToken = new GetAuthToken();
			getAuthToken.setUserID(getPublisher());
			getAuthToken.setCred(getPassword());
			authToken = getUDDINode().getTransport().getUDDISecurityService(endpointURL).getAuthToken(getAuthToken).getAuthInfo();
		}
		return authToken;
	}
	
	public NodeDetail saveNode(Node node)  {
		NodeDetail nodeDetail = null;
		try {
			log.info("Sending Node " + node.getName() + " info to jUDDI " + getUDDINode().getName());
			SaveNode saveNode = new SaveNode();
			saveNode.setAuthInfo(getAuthToken(node.getSecurityUrl()));
			saveNode.getNode().add(node);
			nodeDetail =getUDDINode().getTransport().getJUDDIApiService(node.getJuddiApiUrl()).saveNode(saveNode);
		} catch (Exception e) {
			log.error("Unable to save node " + node.getName()
					+ " ." + e.getMessage(),e);
		} catch (Throwable t) {
			log.error("Unable to save node " + node.getName()
					+ " ." + t.getMessage(),t);
		}
		return nodeDetail;
	}
	
	public ClerkDetail saveClerk(UDDIClerk senderClerk)  {
		ClerkDetail clerkDetail = null;
		try {
			log.debug("Sending Clerk " + senderClerk.getName() + " info to jUDDI " + getUDDINode().getName());
			SaveClerk saveClerk = new SaveClerk();
			saveClerk.setAuthInfo(getAuthToken(senderClerk.getUDDINode().getSecurityUrl()));
			saveClerk.getClerk().add(getApiClerk());
			clerkDetail = getUDDINode().getTransport().getJUDDIApiService(senderClerk.getUDDINode().getJuddiApiUrl()).saveClerk(saveClerk);
		} catch (Exception e) {
			log.error("Unable to save clerk " + getName()
					+ " ." + e.getMessage(),e);
		} catch (Throwable t) {
			log.error("Unable to save clerk " + getName()
					+ " ." + t.getMessage(),t);
		}
		return clerkDetail;
	}
	
	public Clerk getApiClerk() {
		Clerk apiClerk = new Clerk();
		apiClerk.setName(name);
		apiClerk.setNode(uddiNode.getApiNode());
		apiClerk.setPassword(password);
		apiClerk.setPublisher(publisher);
		return apiClerk;
	}
	
	public UDDINode getUDDINode() {
		return uddiNode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUDDINode(UDDINode uddiNode) {
		this.uddiNode = uddiNode;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public WSDL[] getWsdls() {
		return wsdls;
	}

	public void setWsdls(WSDL[] wsdls) {
		this.wsdls = wsdls;
	}

	public class WSDL {
		
		private String businessKey;
		private String keyDomain;
		private String fileName;
		public String getBusinessKey() {
			return businessKey;
		}
		public void setBusinessKey(String businessKey) {
			this.businessKey = businessKey;
		}
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getKeyDomain() {
			return keyDomain;
		}
		public void setKeyDomain(String keyDomain) {
			this.keyDomain = keyDomain;
		}
	}

}
