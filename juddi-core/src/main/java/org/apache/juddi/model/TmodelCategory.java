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
@Table(name = "tmodel_category")
public class TmodelCategory implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private TmodelCategoryId id;
	private Tmodel tmodel;
	private String tmodelKeyRef;
	private String keyName;
	private String keyValue;

	public TmodelCategory() {
	}

	public TmodelCategory(TmodelCategoryId id, Tmodel tmodel, String keyValue) {
		this.id = id;
		this.tmodel = tmodel;
		this.keyValue = keyValue;
	}
	public TmodelCategory(TmodelCategoryId id, Tmodel tmodel,
			String tmodelKeyRef, String keyName, String keyValue) {
		this.id = id;
		this.tmodel = tmodel;
		this.tmodelKeyRef = tmodelKeyRef;
		this.keyName = keyName;
		this.keyValue = keyValue;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "tmodelKey", column = @Column(name = "tmodel_key", nullable = false, length = 41)),
			@AttributeOverride(name = "categoryId", column = @Column(name = "category_id", nullable = false))})

	public TmodelCategoryId getId() {
		return this.id;
	}

	public void setId(TmodelCategoryId id) {
		this.id = id;
	}
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tmodel_key", nullable = false, insertable = false, updatable = false)

	public Tmodel getTmodel() {
		return this.tmodel;
	}

	public void setTmodel(Tmodel tmodel) {
		this.tmodel = tmodel;
	}

	@Column(name = "tmodel_key_ref")
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
