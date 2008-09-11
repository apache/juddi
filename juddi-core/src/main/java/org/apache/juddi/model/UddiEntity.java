package org.apache.ws.juddi.model;
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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "uddi_entity")
public class UddiEntity implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String objectKey;
	private int objectId;
	private Set<BindingTemplate> bindingTemplates = new HashSet<BindingTemplate>(0);
	private Set<Tmodel> tmodels = new HashSet<Tmodel>(0);
	private Set<BusinessService> businessServices = new HashSet<BusinessService>(0);
	private Set<BusinessEntity> businessEntities = new HashSet<BusinessEntity>(0);

	public UddiEntity() {
	}

	public UddiEntity(String objectKey, int objectId) {
		this.objectKey = objectKey;
		this.objectId = objectId;
	}
	public UddiEntity(String objectKey, int objectId,
			Set<BindingTemplate> bindingTemplates, Set<Tmodel> tmodels,
			Set<BusinessService> businessServices,
			Set<BusinessEntity> businessEntities) {
		this.objectKey = objectKey;
		this.objectId = objectId;
		this.bindingTemplates = bindingTemplates;
		this.tmodels = tmodels;
		this.businessServices = businessServices;
		this.businessEntities = businessEntities;
	}

	@Id
	@Column(name = "object_key", unique = true, nullable = false, length = 41)
	public String getObjectKey() {
		return this.objectKey;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	@Column(name = "object_id", nullable = false)

	public int getObjectId() {
		return this.objectId;
	}

	public void setObjectId(int objectId) {
		this.objectId = objectId;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "uddiEntity")
	public Set<BindingTemplate> getBindingTemplates() {
		return this.bindingTemplates;
	}

	public void setBindingTemplates(Set<BindingTemplate> bindingTemplates) {
		this.bindingTemplates = bindingTemplates;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "uddiEntity")
	public Set<Tmodel> getTmodels() {
		return this.tmodels;
	}

	public void setTmodels(Set<Tmodel> tmodels) {
		this.tmodels = tmodels;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "businessEntity")
	public Set<BusinessService> getBusinessServices() {
		return this.businessServices;
	}

	public void setBusinessServices(Set<BusinessService> businessServices) {
		this.businessServices = businessServices;
	}
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "uddiEntity")
	public Set<BusinessEntity> getBusinessEntities() {
		return this.businessEntities;
	}

	public void setBusinessEntities(Set<BusinessEntity> businessEntities) {
		this.businessEntities = businessEntities;
	}

}
