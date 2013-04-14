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
import javax.persistence.Column;
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
@Table(name = "j3_binding_template")
public class BindingTemplate extends UddiEntity implements java.io.Serializable {

	private static final long serialVersionUID = -813683306021520411L;
	private BusinessService businessService;
	private List<BindingDescr> bindingDescrs = new ArrayList<BindingDescr>(0);

	private String accessPointType;
	private String accessPointUrl;
	private String hostingRedirector;
	private BindingCategoryBag categoryBag;
	private List<TmodelInstanceInfo> tmodelInstanceInfos = new ArrayList<TmodelInstanceInfo>(0);
    private List<Signature> signatures = new ArrayList<Signature>(0);
	
	public BindingTemplate() {
	}

	public BindingTemplate(String entityKey, BusinessService businessService, Date modified) {
		this.entityKey = entityKey;
		this.businessService = businessService;
		this.modified = modified;
	}
	public BindingTemplate(String entityKey, BusinessService businessService, String accessPointType,
			String accessPointUrl, String hostingRedirector, Date modified,
			BindingCategoryBag categoryBag,
			List<TmodelInstanceInfo> tmodelInstanceInfos,
			List<BindingDescr> bindingDescrs) {
		this.entityKey = entityKey;
		this.businessService = businessService;
		this.accessPointType = accessPointType;
		this.accessPointUrl = accessPointUrl;
		this.hostingRedirector = hostingRedirector;
		this.modified = modified;
		this.categoryBag = categoryBag;
		this.tmodelInstanceInfos = tmodelInstanceInfos;
		this.bindingDescrs = bindingDescrs;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_key", nullable = false)
	public BusinessService getBusinessService() {
		return this.businessService;
	}
	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	@Column(name = "access_point_type", length = 255)
	public String getAccessPointType() {
		return this.accessPointType;
	}
	public void setAccessPointType(String accessPointType) {
		this.accessPointType = accessPointType;
	}

	@Column(name = "access_point_url", length = 4096)
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
	
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bindingTemplate")
	public BindingCategoryBag getCategoryBag() {
		return this.categoryBag;
	}
	public void setCategoryBag(BindingCategoryBag categoryBag) {
		this.categoryBag = categoryBag;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bindingTemplate")
	@OrderBy
	public List<TmodelInstanceInfo> getTmodelInstanceInfos() {
		return this.tmodelInstanceInfos;
	}
	public void setTmodelInstanceInfos(
			List<TmodelInstanceInfo> tmodelInstanceInfos) {
		this.tmodelInstanceInfos = tmodelInstanceInfos;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bindingTemplate")
	@OrderBy
	public List<BindingDescr> getBindingDescrs() {
		return this.bindingDescrs;
	}
	public void setBindingDescrs(List<BindingDescr> bindingDescrs) {
		this.bindingDescrs = bindingDescrs;
	}
        
        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bindingTemplate")
	@OrderBy
        public List<Signature> getSignatures() {
                return signatures;
        }

        public void setSignatures(List<Signature> signatures) {
                this.signatures = signatures;
        }
}
