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

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
@Entity
@Table(name = "tmodel_overview")
public class TmodelOverview implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int tmodelOverviewId;
	private Tmodel tmodel;
	private String overviewUrl;
	private Set<TmodelDocDescr> docDescrs = new HashSet<TmodelDocDescr>(0);

	public TmodelOverview() {
	}

	public TmodelOverview(int tmodelOverviewId, Tmodel tmodel) {
		this.tmodelOverviewId = tmodelOverviewId;
		this.tmodel = tmodel;
	}
	
	public TmodelOverview(int tmodelOverviewId, Tmodel tmodel,
			String overviewUrl, Set<TmodelDocDescr> docDescrs) {
		this.tmodelOverviewId = tmodelOverviewId;
		this.tmodel = tmodel;
		this.overviewUrl = overviewUrl;
		this.docDescrs = docDescrs;
	}

	@Id
	@Column(name = "tmodel_overview_id", nullable = false)

	public int getTmodelOverviewId() {
		return this.tmodelOverviewId;
	}

	public void setTmodelOverviewId(int tmodelOverviewId) {
		this.tmodelOverviewId = tmodelOverviewId;
	}

	@Column(name = "tmodel", nullable = false, length = 255)
	public Tmodel getTmodel() {
		return this.tmodel;
	}

	public void setTmodel(Tmodel tmodel) {
		this.tmodel = tmodel;
	}

	@Column(name = "overview_url")
	public String getOverviewUrl() {
		return this.overviewUrl;
	}

	public void setOverviewUrl(String overviewUrl) {
		this.overviewUrl = overviewUrl;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodeloverview")
	@OrderBy
	public Set<TmodelDocDescr> getDocDescriptions() {
		return this.docDescrs;
	}
	
	public void setDocDescriptions(Set<TmodelDocDescr> docDescrs) {
		this.docDescrs = docDescrs;
	}	
}
