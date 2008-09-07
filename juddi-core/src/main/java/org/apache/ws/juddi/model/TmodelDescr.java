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
@Table(name = "tmodel_descr")
public class TmodelDescr implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private TmodelDescrId id;
	private Tmodel tmodel;
	private String langCode;
	private String descr;

	public TmodelDescr() {
	}

	public TmodelDescr(TmodelDescrId id, Tmodel tmodel, String descr) {
		this.id = id;
		this.tmodel = tmodel;
		this.descr = descr;
	}
	public TmodelDescr(TmodelDescrId id, Tmodel tmodel, String langCode,
			String descr) {
		this.id = id;
		this.tmodel = tmodel;
		this.langCode = langCode;
		this.descr = descr;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "tmodelKey", column = @Column(name = "tmodel_key", nullable = false, length = 41)),
			@AttributeOverride(name = "tmodelDescrId", column = @Column(name = "tmodel_descr_id", nullable = false))})

	public TmodelDescrId getId() {
		return this.id;
	}

	public void setId(TmodelDescrId id) {
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
