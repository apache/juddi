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
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "business_service")
public class BusinessService extends UddiEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String serviceKey;
	private BusinessEntity businessEntity;
	private Set<ServiceName> serviceNames = new HashSet<ServiceName>(0);
	private Set<ServiceDescr> serviceDescrs = new HashSet<ServiceDescr>(0);
	private Set<BindingTemplate> bindingTemplates = new HashSet<BindingTemplate>(0);
	private Set<ServiceCategory> serviceCategories = new HashSet<ServiceCategory>(0);

	public BusinessService() {
	}

	public BusinessService(String serviceKey, BusinessEntity businessEntity, Date lastUpdate) {
		this.serviceKey = serviceKey;
		this.businessEntity = businessEntity;
		this.lastUpdate = lastUpdate;
	}
	public BusinessService(String serviceKey, BusinessEntity businessEntity, Date lastUpdate,
			Set<ServiceName> serviceNames, Set<ServiceDescr> serviceDescrs,
			Set<BindingTemplate> bindingTemplates,
			Set<ServiceCategory> serviceCategories) {
		this.serviceKey = serviceKey;
		this.businessEntity = businessEntity;
		this.lastUpdate = lastUpdate;
		this.serviceNames = serviceNames;
		this.serviceDescrs = serviceDescrs;
		this.bindingTemplates = bindingTemplates;
		this.serviceCategories = serviceCategories;
	}

	@Id
	@Column(name = "service_key", nullable = false, length = 255)
	public String getServiceKey() {
		return this.serviceKey;
	}
	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "business_key", nullable = false)
	public BusinessEntity getBusinessEntity() {
		return this.businessEntity;
	}
	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessService")
	public Set<ServiceName> getServiceNames() {
		return this.serviceNames;
	}
	public void setServiceNames(Set<ServiceName> serviceNames) {
		this.serviceNames = serviceNames;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessService")
	public Set<ServiceDescr> getServiceDescrs() {
		return this.serviceDescrs;
	}
	public void setServiceDescrs(Set<ServiceDescr> serviceDescrs) {
		this.serviceDescrs = serviceDescrs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessService")
	public Set<BindingTemplate> getBindingTemplates() {
		return this.bindingTemplates;
	}
	public void setBindingTemplates(Set<BindingTemplate> bindingTemplates) {
		this.bindingTemplates = bindingTemplates;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessService")
	public Set<ServiceCategory> getServiceCategories() {
		return this.serviceCategories;
	}
	public void setServiceCategories(Set<ServiceCategory> serviceCategories) {
		this.serviceCategories = serviceCategories;
	}

	public String retrievePublisherId() {
		return this.getBusinessEntity().getPublisher().getPublisherId();
	}
	public void assignPublisherId(String id) {
		// Do nothing, the publisher id is set in the parent.
	}

}
