package org.apache.juddi.model;
/*
 * Copyright 2001-2008 The Apache Software Foundation.
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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "binding_template")
public class BindingTemplate extends UddiEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String bindingKey;
	private BusinessService businessService;
	private String accessPointType;
	private String accessPointUrl;
	private String hostingRedirector;
	private Set<BindingCategory> bindingCategories = new HashSet<BindingCategory>(0);
	private Set<TmodelInstanceInfo> tmodelInstanceInfos = new HashSet<TmodelInstanceInfo>(0);
	private Set<BindingDescr> bindingDescrs = new HashSet<BindingDescr>(0);

	public BindingTemplate() {
	}

	public BindingTemplate(String bindingKey, BusinessService businessService, Date lastUpdate) {
		this.bindingKey = bindingKey;
		this.businessService = businessService;
		this.lastUpdate = lastUpdate;
	}
	public BindingTemplate(String bindingKey, BusinessService businessService, String accessPointType,
			String accessPointUrl, String hostingRedirector, Date lastUpdate,
			Set<BindingCategory> bindingCategories,
			Set<TmodelInstanceInfo> tmodelInstanceInfos,
			Set<BindingDescr> bindingDescrs) {
		this.bindingKey = bindingKey;
		this.businessService = businessService;
		this.accessPointType = accessPointType;
		this.accessPointUrl = accessPointUrl;
		this.hostingRedirector = hostingRedirector;
		this.lastUpdate = lastUpdate;
		this.bindingCategories = bindingCategories;
		this.tmodelInstanceInfos = tmodelInstanceInfos;
		this.bindingDescrs = bindingDescrs;
	}

	@Id
	@Column(name = "binding_key", nullable = false, length = 255)
	public String getBindingKey() {
		return this.bindingKey;
	}
	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_key", nullable = false)
	public BusinessService getBusinessService() {
		return this.businessService;
	}
	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	@Column(name = "access_point_type", length = 20)
	public String getAccessPointType() {
		return this.accessPointType;
	}
	public void setAccessPointType(String accessPointType) {
		this.accessPointType = accessPointType;
	}

	@Column(name = "access_point_url", length = 2000)
	public String getAccessPointUrl() {
		return this.accessPointUrl;
	}
	public void setAccessPointUrl(String accessPointUrl) {
		this.accessPointUrl = accessPointUrl;
	}

	@Column(name = "hosting_redirector")
	public String getHostingRedirector() {
		return this.hostingRedirector;
	}
	public void setHostingRedirector(String hostingRedirector) {
		this.hostingRedirector = hostingRedirector;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bindingTemplate")
	public Set<BindingCategory> getBindingCategories() {
		return this.bindingCategories;
	}
	public void setBindingCategories(Set<BindingCategory> bindingCategories) {
		this.bindingCategories = bindingCategories;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bindingTemplate")
	public Set<TmodelInstanceInfo> getTmodelInstanceInfos() {
		return this.tmodelInstanceInfos;
	}
	public void setTmodelInstanceInfos(
			Set<TmodelInstanceInfo> tmodelInstanceInfos) {
		this.tmodelInstanceInfos = tmodelInstanceInfos;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bindingTemplate")
	public Set<BindingDescr> getBindingDescrs() {
		return this.bindingDescrs;
	}
	public void setBindingDescrs(Set<BindingDescr> bindingDescrs) {
		this.bindingDescrs = bindingDescrs;
	}

}
