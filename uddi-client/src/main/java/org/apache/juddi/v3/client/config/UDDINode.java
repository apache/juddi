package org.apache.juddi.v3.client.config;

import java.util.Properties;

import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;
import org.apache.log4j.helpers.Loader;


public class UDDINode {
	
	private String name;
	private String description;
	private String custodyTransferUrl;
	private String inquiryUrl;
	private String publishUrl;
	private String securityUrl;
	private String subscriptionUrl;
	private String subscriptionListenerUrl;
	private String juddiApiUrl;
	private String proxyTransport;
	private String factoryInitial;
	private String factoryURLPkgs;
	private String factoryNamingProvider;
	
	private Properties properties;
	
	private Transport transport;
	
	public Transport getTransport(String managerName) throws TransportException {
		if (transport==null) {
			try {
				String clazz = UDDIClerkManager.getClientConfig(managerName).getNodes().get(name).getProxyTransport();
				Class<?> transportClass = Loader.loadClass(clazz);
				transport = (Transport) transportClass.getConstructor(String.class,String.class).newInstance(managerName,name);
			} catch (Exception e) {
				throw new TransportException(e.getMessage(),e);
			}
		}
		return transport;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCustodyTransferUrl() {
		return custodyTransferUrl;
	}
	public void setCustodyTransferUrl(String custodyTransferUrl) {
		this.custodyTransferUrl = custodyTransferUrl;
	}
	public String getInquiryUrl() {
		return inquiryUrl;
	}
	public void setInquiryUrl(String inquiryUrl) {
		this.inquiryUrl = inquiryUrl;
	}
	public String getPublishUrl() {
		return publishUrl;
	}
	public void setPublishUrl(String publishUrl) {
		this.publishUrl = publishUrl;
	}
	public String getSecurityUrl() {
		return securityUrl;
	}
	public void setSecurityUrl(String securityUrl) {
		this.securityUrl = securityUrl;
	}
	public String getSubscriptionUrl() {
		return subscriptionUrl;
	}
	public void setSubscriptionUrl(String subscriptionUrl) {
		this.subscriptionUrl = subscriptionUrl;
	}
	public String getProxyTransport() {
		return proxyTransport;
	}
	public void setProxyTransport(String proxyTransport) {
		this.proxyTransport = proxyTransport;
	}
	public String getJuddiApiUrl() {
		return juddiApiUrl;
	}
	public void setJuddiApiUrl(String juddiApiUrl) {
		this.juddiApiUrl = juddiApiUrl;
	}
	public String getSubscriptionListenerUrl() {
		return subscriptionListenerUrl;
	}
	public void setSubscriptionListenerUrl(String subscriptionListenerUrl) {
		this.subscriptionListenerUrl = subscriptionListenerUrl;
	}
	public String getFactoryInitial() {
		return factoryInitial;
	}
	public void setFactoryInitial(String factoryInitial) {
		this.factoryInitial = factoryInitial;
	}
	public String getFactoryURLPkgs() {
		return factoryURLPkgs;
	}
	public void setFactoryURLPkgs(String factoryURLPkgs) {
		this.factoryURLPkgs = factoryURLPkgs;
	}
	public String getFactoryNamingProvider() {
		return factoryNamingProvider;
	}
	public void setFactoryNamingProvider(String factoryNamingProvider) {
		this.factoryNamingProvider = factoryNamingProvider;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	
}
