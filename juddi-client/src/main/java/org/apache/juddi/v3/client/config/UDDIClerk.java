package org.apache.juddi.v3.client.config;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.log4j.Logger;
import org.uddi.api_v3.BindingDetail;
import org.uddi.api_v3.BindingTemplate;
import org.uddi.api_v3.BusinessService;
import org.uddi.api_v3.DeleteBinding;
import org.uddi.api_v3.GetAuthToken;
import org.uddi.api_v3.GetBindingDetail;
import org.uddi.api_v3.GetServiceDetail;
import org.uddi.api_v3.SaveService;
import org.uddi.api_v3.ServiceDetail;
import org.uddi.v3_service.DispositionReportFaultMessage;

public class UDDIClerk {

	private Logger log = Logger.getLogger(this.getClass());
	private String name;
	private UDDINode node;
	private String publisher;
	private String password;
	private String[] classWithAnnotations;
	private String authToken;
	private String managerName;
	private Map<String,Properties> services = new HashMap<String,Properties>(); 

	public UDDIClerk() {
		super();
	}

	/**
	 * Register a service.
	 * 
	 */
	public BusinessService register(BusinessService service) {
		
		BusinessService businessService=null;
		log.info("Registering service " + service);
		try {
			String authToken = getAuthToken();
			SaveService saveService = new SaveService();
			saveService.setAuthInfo(authToken);
			saveService.getBusinessService().add(service);
			ServiceDetail serviceDetail = node.getTransport().getUDDIPublishService().saveService(saveService);
			businessService = serviceDetail.getBusinessService().get(0);
		} catch (Exception e) {
			log.error("Unable to register service " + service.getName().get(0).getValue()
					+ " ." + e.getMessage(),e);
		} catch (Throwable t) {
			log.error("Unable to register service " + service.getName().get(0).getValue()
					+ " ." + t.getMessage(),t);
		}
		log.info("Registering service " + service + " completed.");
		return businessService;
	}
	/**
	 * Unregisters the BindingTemplates for this service.
	 * @param service
	 */
	public void unRegister(BusinessService service) {
		log.info("UnRegistering binding for service " + service.getName().get(0).getValue());
		try {
			String authToken = getAuthToken();
			DeleteBinding deleteBinding = new DeleteBinding();
			deleteBinding.setAuthInfo(authToken);
			for (BindingTemplate binding : service.getBindingTemplates().getBindingTemplate()) {
				deleteBinding.getBindingKey().add(binding.getBindingKey());
			}
			node.getTransport().getUDDIPublishService().deleteBinding(deleteBinding);
		} catch (Exception e) {
			log.error("Unable to register service " + service.getName().get(0).getValue()
					+ " ." + e.getMessage(),e);
		}
	}
	
	public BusinessService findService(String serviceKey) throws DispositionReportFaultMessage, RemoteException, 
	TransportException, ConfigurationException  {
		GetServiceDetail getServiceDetail = new GetServiceDetail();
		getServiceDetail.getServiceKey().add(serviceKey);
		getServiceDetail.setAuthInfo(getAuthToken());
		ServiceDetail sd = node.getTransport().getUDDIInquiryService().getServiceDetail(getServiceDetail);
		List<BusinessService> businessServiceList = sd.getBusinessService();
		if (businessServiceList.size() == 0) throw new ConfigurationException("Could not find Service with key=" + serviceKey);
		return businessServiceList.get(0);
	}
	
	public BindingTemplate findServiceBinding(String bindingKey) throws DispositionReportFaultMessage, RemoteException, 
			TransportException, ConfigurationException  {
		GetBindingDetail getBindingDetail = new GetBindingDetail();
		getBindingDetail.getBindingKey().add(bindingKey);
		getBindingDetail.setAuthInfo(getAuthToken());
		BindingDetail bd = node.getTransport().getUDDIInquiryService().getBindingDetail(getBindingDetail);
		List<BindingTemplate> bindingTemplateList = bd.getBindingTemplate();
		if (bindingTemplateList.size() == 0) throw new ConfigurationException("Could not find ServiceBbinding with key=" + bindingKey);
		return bindingTemplateList.get(0);
	}
	
	private String getAuthToken() throws TransportException, DispositionReportFaultMessage, RemoteException {
		if (authToken==null) {
			GetAuthToken getAuthToken = new GetAuthToken();
			getAuthToken.setUserID(getPublisher());
			getAuthToken.setCred(getPassword());
			authToken = node.getTransport().getUDDISecurityService().getAuthToken(getAuthToken).getAuthInfo();
		}
		return authToken;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getClassWithAnnotations() {
		return classWithAnnotations;
	}

	public void setClassWithAnnotations(String[] classWithAnnotations) {
		this.classWithAnnotations = classWithAnnotations;
	}

	public UDDINode getNode() {
		return node;
	}

	public void setNode(UDDINode node) {
		this.node = node;
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
	
}
