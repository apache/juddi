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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:tcunning@apache.org">Tom Cunningham</a>
 */
@Entity
@Table(name = "j3_overview_doc")
public class OverviewDoc implements java.io.Serializable {

	private static final long serialVersionUID = 4560091663915489899L;
	private Long id;
	private TmodelInstanceInfo tmodelInstanceInfo;
	private Tmodel tmodel;
	private String overviewUrl;
	private String overviewUrlUseType;
	private List<OverviewDocDescr> overviewDocDescrs = new ArrayList<OverviewDocDescr>(0);

	public OverviewDoc() {
	}

	public OverviewDoc(TmodelInstanceInfo tmodelInstanceInfo) {
		this.tmodelInstanceInfo = tmodelInstanceInfo;
	}
	
	public OverviewDoc(Tmodel tmodel) {
		this.tmodel = tmodel;
	}
	
	public OverviewDoc(TmodelInstanceInfo tmodelInstanceInfo, Tmodel tmodel,
			String overviewUrl, String overviewUrlUseType, List<OverviewDocDescr> overviewDocDescrs) {
		this.tmodelInstanceInfo = tmodelInstanceInfo;
		this.tmodel = tmodel;
		this.overviewUrl = overviewUrl;
		this.overviewUrlUseType = overviewUrlUseType;
		this.overviewDocDescrs = overviewDocDescrs;
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
	@JoinColumn(name = "tomodel_instance_info_id", nullable = true)
	public TmodelInstanceInfo getTmodelInstanceInfo() {
		return this.tmodelInstanceInfo;
	}

	public void setTmodelInstanceInfo(TmodelInstanceInfo tmodelInstanceInfo) {
		this.tmodelInstanceInfo = tmodelInstanceInfo;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entity_key", nullable = true)
	public Tmodel getTmodel() {
		return this.tmodel;
	}

	public void setTmodel(Tmodel tmodel) {
		this.tmodel = tmodel;
	}

	@Column(name = "overview_url", nullable = true)
	public String getOverviewUrl() {
		return this.overviewUrl;
	}

	public void setOverviewUrl(String overviewUrl) {
		this.overviewUrl = overviewUrl;
	}
	
	@Column(name = "overview_url_use_type", nullable = true)
	public String getOverviewUrlUseType() {
		return this.overviewUrlUseType;
	}

	public void setOverviewUrlUseType(String overviewUrlUseType) {
		this.overviewUrlUseType = overviewUrlUseType;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "overviewDoc")
	@OrderBy
	public List<OverviewDocDescr> getOverviewDocDescrs() {
		return this.overviewDocDescrs;
	}
	
	public void setOverviewDocDescrs(List<OverviewDocDescr> overviewDocDescrs) {
		this.overviewDocDescrs = overviewDocDescrs;
	}	
}
