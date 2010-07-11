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
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.api_v3.Clerk;
import org.apache.juddi.api_v3.ClerkDetail;
import org.apache.juddi.api_v3.Node;
import org.apache.juddi.api_v3.NodeDetail;
import org.apache.juddi.api_v3.SaveClerk;
import org.apache.juddi.api_v3.SaveNode;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.log4j.Logger;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessDetail;
import org.uddi.api_v3.BusinessEntity;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.DispositionReport;
import org.uddi.api_v3.FindRelatedBusinesses;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetBusinessDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.RelatedBusinessesList;
import org.uddi.api_v3.Result;
import org.uddi.api_v3.SaveBinding;
import org.uddi.api_v3.SaveBusiness;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class UDDIClerk implements Serializable {

	private static final long serialVersionUID = -8597375975981358134L;
	private Logger log = Logger.getLogger(this.getClass());
	
	protected String name;
	protected UDDINode uddiNode;
	protected String publisher;
	protected String password;
	
	private String authToken;
	private String[] classWithAnnotations;
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
	/**
	 * Register a service binding.
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
		} catch (Exception e) {
			log.error("Unable to register template binding " + bindingTemplate.getBindingKey()
					+ " ." + e.getMessage(),e);
		} catch (Throwable t) {
			log.error("Unable to register template binding " + bindingTemplate.getBindingKey()
					+ " ." + t.getMessage(),t);
		}
		log.info("Registering template binding " + binding.getBindingKey() + " completed.");
		return bindingTemplate;
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
		} catch (Exception e) {
			log.error("Unable to register service " + service.getName().get(0).getValue()
					+ " ." + e.getMessage(),e);
		} catch (Throwable t) {
			log.error("Unable to register service " + service.getName().get(0).getValue()
					+ " ." + t.getMessage(),t);
		}
		log.info("Registering service " + service.getName().get(0).getValue() + " completed.");
		return businessService;
	}
	/**
	 * Register a service.
	 * 
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
		} catch (Exception e) {
			log.error("Unable to register business " + business.getName().get(0).getValue()
					+ " ." + e.getMessage(),e);
		} catch (Throwable t) {
			log.error("Unable to register business " + business.getName().get(0).getValue()
					+ " ." + t.getMessage(),t);
		}
		log.info("Registering businessEntity " + businessEntity.getName().get(0).getValue() + " completed.");
		return businessEntity;
	}
	/**
	 * Unregisters the BindingTemplates for this service.
	 * @param service
	 */
	public void unRegister(BusinessService service, Node node) {
		log.info("UnRegistering binding for service " + service.getName().get(0).getValue());
		try {
			String authToken = getAuthToken(node.getSecurityUrl());
			DeleteBinding deleteBinding = new DeleteBinding();
			deleteBinding.setAuthInfo(authToken);
			for (BindingTemplate binding : service.getBindingTemplates().getBindingTemplate()) {
				deleteBinding.getBindingKey().add(binding.getBindingKey());
			}
			getUDDINode().getTransport().getUDDIPublishService(node.getPublishUrl()).deleteBinding(deleteBinding);
		} catch (Exception e) {
			log.error("Unable to register service " + service.getName().get(0).getValue()
					+ " ." + e.getMessage(),e);
		}
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
		
		if (report!=null &&report.countainsErrorCode(DispositionReport.E_INVALID_KEY_PASSED)) {
			log.info("entityKey " + entityKey + " was not found in the registry");
		} else {
			if (report == null) {
				log.error("Missing DispositionReport");
			} else {
				for (Result result : report.getResult()) {
					log.error(result.getErrInfo().getErrCode() + " " + result.getErrInfo().getValue());
				}
			}
		}
	}
	
	private String getAuthToken(String endpointURL) throws TransportException, DispositionReportFaultMessage, RemoteException {
		if (authToken==null) {
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
			log.info("Sending Clerk " + senderClerk.getName() + " info to jUDDI " + getUDDINode().getName());
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

	

}
