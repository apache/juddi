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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "binding_category")
public class BindingCategory implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private BindingCategoryId id;
	private BindingTemplate bindingTemplate;
	private String tmodelKeyRef;
	private String keyName;
	private String keyValue;

	public BindingCategory() {
	}

	public BindingCategory(BindingCategoryId id,
			BindingTemplate bindingTemplate, String keyValue) {
		this.id = id;
		this.bindingTemplate = bindingTemplate;
		this.keyValue = keyValue;
	}
	public BindingCategory(BindingCategoryId id,
			BindingTemplate bindingTemplate, String tmodelKeyRef,
			String keyName, String keyValue) {
		this.id = id;
		this.bindingTemplate = bindingTemplate;
		this.tmodelKeyRef = tmodelKeyRef;
		this.keyName = keyName;
		this.keyValue = keyValue;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bindingKey", column = @Column(name = "binding_key", nullable = false, length = 41)),
			@AttributeOverride(name = "categoryId", column = @Column(name = "category_id", nullable = false))})

	public BindingCategoryId getId() {
		return this.id;
	}

	public void setId(BindingCategoryId id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "binding_key", nullable = false, insertable = false, updatable = false)

	public BindingTemplate getBindingTemplate() {
		return this.bindingTemplate;
	}

	public void setBindingTemplate(BindingTemplate bindingTemplate) {
		this.bindingTemplate = bindingTemplate;
	}

	@Column(name = "tmodel_key_ref", length = 41)
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
