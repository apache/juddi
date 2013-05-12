package org.apache.juddi.model;
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
 */

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "j3_node")
public class Node implements java.io.Serializable {

	@Transient
	private static final long serialVersionUID = -893203927029468343L;
	@Id
	@Column(name="name", nullable = false)
	private String name;
	@Column(name="client_name", nullable = false, length=255)
	private String clientName;
	@Column(name="proxy_transport", nullable = false, length=255)
	private String proxyTransport;
	@Column(name="security_url", nullable = false, length=255)
	private String securityUrl;
	@Column(name="inquiry_url", nullable = false, length=255)
	private String inquiryUrl;
	@Column(name="publish_url", nullable = false, length=255)
	private String publishUrl;
	@Column(name="custody_transfer_url", nullable = false, length=255)
	private String custodyTransferUrl;
	@Column(name="subscription_url", nullable = false, length=255)
	private String subscriptionUrl;
	@Column(name="juddi_api_url", length=255)
	private String juddiApiUrl;
	@Column(name="factory_initial", length=255)
	private String factoryInitial;
	@Column(name="factory_url_pkgs", length=255)
	private String factoryURLPkgs;
	@Column(name="factory_naming_provider", length=255)
	private String factoryNamingProvider;

	public Node() {}

	public Node(String custodyTransferUrl, String inquiryUrl,
			String juddiApiUrl, String name, String proxyTransport,
			String publishUrl, String securityUrl, String subscriptionUrl) {
		super();
		this.custodyTransferUrl = custodyTransferUrl;
		this.inquiryUrl = inquiryUrl;
		this.juddiApiUrl = juddiApiUrl;
		this.name = name;
		this.proxyTransport = proxyTransport;
		this.publishUrl = publishUrl;
		this.securityUrl = securityUrl;
		this.subscriptionUrl = subscriptionUrl;
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

	public String getProxyTransport() {
		return proxyTransport;
	}

	public void setProxyTransport(String proxyTransport) {
		this.proxyTransport = proxyTransport;
	}

	public String getSecurityUrl() {
		return securityUrl;
	}

	public void setSecurityUrl(String securityUrl) {
		this.securityUrl = securityUrl;
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

	public String getCustodyTransferUrl() {
		return custodyTransferUrl;
	}

	public void setCustodyTransferUrl(String custodyTransferUrl) {
		this.custodyTransferUrl = custodyTransferUrl;
	}

	public String getSubscriptionUrl() {
		return subscriptionUrl;
	}

	public void setSubscriptionUrl(String subscriptionUrl) {
		this.subscriptionUrl = subscriptionUrl;
	}

	public String getJuddiApiUrl() {
		return juddiApiUrl;
	}

	public void setJuddiApiUrl(String juddiApiUrl) {
		this.juddiApiUrl = juddiApiUrl;
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
