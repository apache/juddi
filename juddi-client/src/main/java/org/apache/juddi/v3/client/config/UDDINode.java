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
import java.util.Properties;

import org.apache.juddi.api_v3.Node;
import org.apache.juddi.v3.client.ClassUtil;
import org.apache.juddi.v3.client.transport.Transport;
import org.apache.juddi.v3.client.transport.TransportException;

/**
 * Connection information to a UDDI node.
 * 
 * @author kstam
 *
 */
public class UDDINode implements Serializable {

	private static final long serialVersionUID = 5721040459195558161L;
	private Properties properties=new Properties();
        private transient Transport transport;
	
	private boolean isHomeJUDDI;
	private String name;
	private String clientName;
	private String description;
	private String custodyTransferUrl;
	private String inquiryUrl;
	private String inquiryRESTUrl;
	private String publishUrl;
	private String securityUrl;
        private String replicationUrl;
	private String subscriptionUrl;
	private String subscriptionListenerUrl;
	private String juddiApiUrl;
	private String proxyTransport;
	private String factoryInitial;
	private String factoryURLPkgs;
	private String factoryNamingProvider;
	
	
	public UDDINode() {
		super();
	}
	
	public UDDINode(Node node) {
		super();
		name = node.getName();
		clientName = node.getClientName();
		description = node.getDescription();
		custodyTransferUrl = node.getCustodyTransferUrl();
		inquiryUrl = node.getInquiryUrl();
                //TODO need juddi schema updat inquiryRESTUrl = node.getInquiryRESTUrl()
		publishUrl = node.getPublishUrl();
		securityUrl = node.getSecurityUrl();
		subscriptionUrl = node.getSubscriptionUrl();
		subscriptionListenerUrl = node.getSubscriptionListenerUrl();
                replicationUrl=node.getReplicationUrl();
		juddiApiUrl = node.getJuddiApiUrl();
		proxyTransport = node.getProxyTransport();
		factoryInitial = node.getFactoryInitial();
		factoryURLPkgs = node.getFactoryURLPkgs();
		factoryNamingProvider = node.getFactoryNamingProvider();
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public Node getApiNode() {
		Node apiNode = new Node();
		apiNode.setClientName(clientName);
                apiNode.setCustodyTransferUrl(custodyTransferUrl);
		apiNode.setDescription(description);
		apiNode.setFactoryInitial(factoryInitial);
		apiNode.setFactoryNamingProvider(factoryNamingProvider);
		apiNode.setFactoryURLPkgs(factoryURLPkgs);
		apiNode.setInquiryUrl(inquiryUrl);
                //no mapping for this apiNode.setInquiryRESTUrl(name);
		apiNode.setJuddiApiUrl(juddiApiUrl);
		
		apiNode.setName(name);
		apiNode.setProxyTransport(proxyTransport);
		apiNode.setPublishUrl(publishUrl);
                apiNode.setReplicationUrl(replicationUrl);
		apiNode.setSecurityUrl(securityUrl);
		apiNode.setSubscriptionUrl(subscriptionUrl);
                apiNode.setSubscriptionListenerUrl(subscriptionListenerUrl);
		return apiNode;
	}
	
	
	public Transport getTransport() throws TransportException {
		if (transport==null) {
                        String clazz = getProxyTransport();
			try {
				
				Class<?> transportClass = ClassUtil.forName(clazz,this.getClass());
				transport = (Transport) transportClass.getConstructor(String.class,String.class).newInstance(clientName,name);
			} catch (Exception e) {
				throw new TransportException(e.getMessage() + " node " + name + " transport class: " + clazz,e);
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

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
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
        /**
         * @since 3.2.1
         * @return 
         */
        public String getReplicationUrl() {
		return replicationUrl;
	}
        /**
         * @since 3.2.1
         * @param value 
         */
        public void setReplicationUrl(String value) {
		replicationUrl=value;
	}
        
        /**
         * used ONLY for UDDI's HTTP GET (REST) endpoint
         * @since 3.2
         * @param url 
         */
        public void setInquiryRESTUrl(String url){
                this.inquiryRESTUrl = url;
        }
        /**
         * used ONLY for jUDDI's HTTP GET (REST) endpoint
         * @since 3.2
         * @return the REST url for the Inquiry, not supported by all registries 
         */
        public String getInquiry_REST_Url() {
		return inquiryRESTUrl;
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

	public String getSubscriptionListenerUrl() {
		return subscriptionListenerUrl;
	}

	public void setSubscriptionListenerUrl(String subscriptionListenerUrl) {
		this.subscriptionListenerUrl = subscriptionListenerUrl;
	}

	public String getJuddiApiUrl() {
		return juddiApiUrl;
	}

	public void setJuddiApiUrl(String juddiApiUrl) {
		this.juddiApiUrl = juddiApiUrl;
	}

	public String getProxyTransport() {
		return proxyTransport;
	}

	public void setProxyTransport(String proxyTransport) {
		this.proxyTransport = proxyTransport;
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

	public boolean isHomeJUDDI() {
		return isHomeJUDDI;
	}

	public void setHomeJUDDI(boolean isHomeJUDDI) {
		this.isHomeJUDDI = isHomeJUDDI;
	}
}
