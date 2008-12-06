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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "service_category")
public class ServiceCategory implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private ServiceCategoryId id;
	private BusinessService businessService;
	private String tmodelKeyRef;
	private String keyName;
	private String keyValue;

	public ServiceCategory() {
	}

	public ServiceCategory(ServiceCategoryId id,
			BusinessService businessService, String keyValue) {
		this.id = id;
		this.businessService = businessService;
		this.keyValue = keyValue;
	}
	public ServiceCategory(ServiceCategoryId id,
			BusinessService businessService, String tmodelKeyRef,
			String keyName, String keyValue) {
		this.id = id;
		this.businessService = businessService;
		this.tmodelKeyRef = tmodelKeyRef;
		this.keyName = keyName;
		this.keyValue = keyValue;
	}

	@EmbeddedId
	public ServiceCategoryId getId() {
		return this.id;
	}
	public void setId(ServiceCategoryId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entity_key", nullable = false, insertable = false, updatable = false)
	public BusinessService getBusinessService() {
		return this.businessService;
	}
	public void setBusinessService(BusinessService businessService) {
		this.businessService = businessService;
	}

	@Column(name = "tmodel_key_ref", length = 255)
	public String getTmodelKeyRef() {
		return this.tmodelKeyRef;
	}
	public void setTmodelKeyRef(String tmodelKeyRef) {
		this.tmodelKeyRef = tmodelKeyRef;
	}

	@Column(name = "key_name")
	public String getKeyName() {
		return this.keyName;
	}
	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	@Column(name = "key_value", nullable = false)
	public String getKeyValue() {
		return this.keyValue;
	}
	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

}
