/*
 * Copyright 2001-2009 The Apache Software Foundation.
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
/**
 * <p>Java class for Node type.  Specific to juddi.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a> 
 * 
 */
package org.apache.juddi.api_v3;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "node", propOrder = {
	"name",
	"clientName",
    "description",
    "custodyTransferUrl",
    "inquiryUrl",
    "publishUrl",
    "securityUrl",
	"subscriptionUrl",
	"subscriptionListenerUrl",
	"juddiApiUrl",
	"proxyTransport",
	"factoryInitial",
	"factoryURLPkgs",
	"factoryNamingProvider"
})
public class Node implements Serializable{
	
	@XmlTransient
	private static final long serialVersionUID = -4601378453000384721L;
	private String name;
	private String clientName;
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

	
	
}
