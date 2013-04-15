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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
@Entity
@Table(name = "j3_tmodel")
public class Tmodel extends UddiEntity implements java.io.Serializable {

	private static final long serialVersionUID = -4577524699559324289L;
	private String name;
	private String langCode;
	private boolean deleted;
	private List<OverviewDoc> overviewDocs = new ArrayList<OverviewDoc>(0);
	private List<TmodelDescr> tmodelDescrs = new ArrayList<TmodelDescr>(0);
	private List<TmodelIdentifier> tmodelIdentifiers = new ArrayList<TmodelIdentifier>(0);
	private TmodelCategoryBag categoryBag;
    private List<Signature> signatures = new ArrayList<Signature>(0);

	public Tmodel() {
	}

	public Tmodel(String entityKey, String name, Date modified) {
		this.entityKey = entityKey;
		this.name = name;
		this.modified = modified;
	}
	public Tmodel(String entityKey, String authorizedName, String operator,
			String name, String langCode, boolean deleted, Date modified,
			List<OverviewDoc> overviewDocs,
			List<TmodelDescr> tmodelDescrs,
			List<TmodelIdentifier> tmodelIdentifiers,
			TmodelCategoryBag categoryBag) {
		this.entityKey = entityKey;
		this.authorizedName = authorizedName;
		this.name = name;
		this.langCode = langCode;
		this.deleted = deleted;
		this.modified = modified;
		this.overviewDocs = overviewDocs;
		this.tmodelDescrs = tmodelDescrs;
		this.tmodelIdentifiers = tmodelIdentifiers;
		this.categoryBag = categoryBag;
	}

	@Column(name = "name", nullable = false, length = 255)
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "lang_code", length = 26)
	public String getLangCode() {
		return this.langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	@Column(name = "deleted")
	public boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodel")
	@OrderBy
	public List<OverviewDoc> getOverviewDocs() {
		return this.overviewDocs;
	}
	
	public void setOverviewDocs(List<OverviewDoc> overviewDocs) {
		this.overviewDocs = overviewDocs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodel")
	@OrderBy
	public List<TmodelDescr> getTmodelDescrs() {
		return this.tmodelDescrs;
	}
	public void setTmodelDescrs(List<TmodelDescr> tmodelDescrs) {
		this.tmodelDescrs = tmodelDescrs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodel")
	@OrderBy
	public List<TmodelIdentifier> getTmodelIdentifiers() {
		return this.tmodelIdentifiers;
	}
	public void setTmodelIdentifiers(List<TmodelIdentifier> tmodelIdentifiers) {
		this.tmodelIdentifiers = tmodelIdentifiers;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodel")
	public TmodelCategoryBag getCategoryBag() {
		return this.categoryBag;
	}
	public void setCategoryBag(TmodelCategoryBag categoryBag) {
		this.categoryBag = categoryBag;
	}

        @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodel")
	@OrderBy
        public List<Signature> getSignatures() {
                return signatures;
        }

        public void setSignatures(List<Signature> signatures) {
                this.signatures = signatures;
        }
}
