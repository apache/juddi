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
@Table(name = "binding_descr")
public class BindingDescr implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private BindingDescrId id;
	private BindingTemplate bindingTemplate;
	private String langCode;
	private String descr;

	public BindingDescr() {
	}

	public BindingDescr(BindingDescrId id, BindingTemplate bindingTemplate,
			String descr) {
		this.id = id;
		this.bindingTemplate = bindingTemplate;
		this.descr = descr;
	}
	public BindingDescr(BindingDescrId id, BindingTemplate bindingTemplate,
			String langCode, String descr) {
		this.id = id;
		this.bindingTemplate = bindingTemplate;
		this.langCode = langCode;
		this.descr = descr;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bindingKey", column = @Column(name = "binding_key", nullable = false, length = 255)),
			@AttributeOverride(name = "bindingDescrId", column = @Column(name = "binding_descr_id", nullable = false))})

	public BindingDescrId getId() {
		return this.id;
	}

	public void setId(BindingDescrId id) {
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

	@Column(name = "lang_code", length = 5)
	public String getLangCode() {
		return this.langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	@Column(name = "descr", nullable = false)

	public String getDescr() {
		return this.descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

}
