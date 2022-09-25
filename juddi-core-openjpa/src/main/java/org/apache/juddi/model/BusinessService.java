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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "j3_business_service")
public class BusinessService extends UddiEntity implements java.io.Serializable {

	private static final long serialVersionUID = 7793243417208829793L;
	private BusinessEntity businessEntity;
	private List<ServiceName> serviceNames = new ArrayList<ServiceName>(0);
	private List<ServiceDescr> serviceDescrs = new ArrayList<ServiceDescr>(0);
	private List<BindingTemplate> bindingTemplates = new ArrayList<BindingTemplate>(0);
	private ServiceCategoryBag categoryBag;
    private List<ServiceProjection> projectingBusinesses = new ArrayList<ServiceProjection>(0);
    private List<Signature> signatures = new ArrayList<Signature>(0);

	public BusinessService() {
	}

	public BusinessService(String entityKey, BusinessEntity businessEntity, Date modified) {
		this.entityKey = entityKey;
		this.businessEntity = businessEntity;
		this.modified = modified;
	}
	public BusinessService(String entityKey, BusinessEntity businessEntity, Date modified,
			List<ServiceName> serviceNames, List<ServiceDescr> serviceDescrs,
			List<BindingTemplate> bindingTemplates,
			ServiceCategoryBag categoryBag) {
		this.entityKey = entityKey;
		this.businessEntity = businessEntity;
		this.modified = modified;
		this.serviceNames = serviceNames;
		this.serviceDescrs = serviceDescrs;
		this.bindingTemplates = bindingTemplates;
		this.categoryBag = categoryBag;
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
	@OrderBy
	public List<ServiceName> getServiceNames() {
		return this.serviceNames;
	}
	public void setServiceNames(List<ServiceName> serviceNames) {
		this.serviceNames = serviceNames;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessService")
	@OrderBy
	public List<ServiceDescr> getServiceDescrs() {
		return this.serviceDescrs;
	}
	public void setServiceDescrs(List<ServiceDescr> serviceDescrs) {
		this.serviceDescrs = serviceDescrs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessService")
	@OrderBy
	public List<BindingTemplate> getBindingTemplates() {
		return this.bindingTemplates;
	}
	public void setBindingTemplates(List<BindingTemplate> bindingTemplates) {
		this.bindingTemplates = bindingTemplates;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessService")
	public ServiceCategoryBag getCategoryBag() {
		return this.categoryBag;
	}
	public void setCategoryBag(ServiceCategoryBag categoryBag) {
		this.categoryBag = categoryBag;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessService")
	public List<ServiceProjection> getProjectingBusinesses() {
		return projectingBusinesses;
	}
	public void setProjectingBusinesses(List<ServiceProjection> projectingBusinesses) {
		this.projectingBusinesses = projectingBusinesses;
	}
        
        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessService")
	@OrderBy
        public List<Signature> getSignatures() {
                return signatures;
        }

        public void setSignatures(List<Signature> signatures) {
                this.signatures = signatures;
        }
}
