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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "j3_business_identifier")
public class BusinessIdentifier implements java.io.Serializable {

	private static final long serialVersionUID = -4262205627713188502L;
	private Long id;
	private BusinessEntity businessEntity;
	private String tmodelKeyRef;
	private String keyName;
	private String keyValue;

	public BusinessIdentifier() {
	}

	public BusinessIdentifier(BusinessEntity businessEntity, String keyValue) {
		this.businessEntity = businessEntity;
		this.keyValue = keyValue;
	}
	public BusinessIdentifier(BusinessEntity businessEntity, String tmodelKeyRef, String keyName,
			String keyValue) {
		this.businessEntity = businessEntity;
		this.tmodelKeyRef = tmodelKeyRef;
		this.keyName = keyName;
		this.keyValue = keyValue;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return this.id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entity_key", nullable = false)
	public BusinessEntity getBusinessEntity() {
		return this.businessEntity;
	}
	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
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
