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
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "j3_tmodel_instance_info")
public class TmodelInstanceInfo implements java.io.Serializable {

	private static final long serialVersionUID = -6375499794977257231L;
	private Long id;
	private BindingTemplate bindingTemplate;
	private String tmodelKey;
	private List<TmodelInstanceInfoDescr> tmodelInstanceInfoDescrs = new ArrayList<TmodelInstanceInfoDescr>(0);

	private String instanceParms;
	private List<OverviewDoc> overviewDocs = new ArrayList<OverviewDoc>(0);
	private List<InstanceDetailsDescr> instanceDetailsDescrs = new ArrayList<InstanceDetailsDescr>(0);
	
	public TmodelInstanceInfo() {
	}

	public TmodelInstanceInfo(BindingTemplate bindingTemplate, String tmodelKey) {
		this.bindingTemplate = bindingTemplate;
		this.tmodelKey = tmodelKey;
	}
	public TmodelInstanceInfo(BindingTemplate bindingTemplate, String tmodelKey,
			String instanceParms,
			List<OverviewDoc> overviewDocs,
			List<InstanceDetailsDescr> instanceDetailsDescrs,
			List<TmodelInstanceInfoDescr> tmodelInstanceInfoDescrs) {
		this.bindingTemplate = bindingTemplate;
		this.tmodelKey = tmodelKey;
		this.instanceParms = instanceParms;
		this.overviewDocs = overviewDocs;
		this.instanceDetailsDescrs = instanceDetailsDescrs;
		this.tmodelInstanceInfoDescrs = tmodelInstanceInfoDescrs;
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
	public BindingTemplate getBindingTemplate() {
		return this.bindingTemplate;
	}
	public void setBindingTemplate(BindingTemplate bindingTemplate) {
		this.bindingTemplate = bindingTemplate;
	}

	@Column(name = "tmodel_key", nullable = false, length = 255)
	public String getTmodelKey() {
		return this.tmodelKey;
	}
	public void setTmodelKey(String tmodelKey) {
		this.tmodelKey = tmodelKey;
	}

	@Column(name = "instance_parms", length = 8192)
	public String getInstanceParms() {
		return this.instanceParms;
	}
	public void setInstanceParms(String instanceParms) {
		this.instanceParms = instanceParms;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodelInstanceInfo")
	@OrderBy
	public List<OverviewDoc> getOverviewDocs() {
		return this.overviewDocs;
	}
	public void setOverviewDocs(
			List<OverviewDoc> overviewDocs) {
		this.overviewDocs = overviewDocs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodelInstanceInfo")
	@OrderBy
	public List<InstanceDetailsDescr> getInstanceDetailsDescrs() {
		return this.instanceDetailsDescrs;
	}
	public void setInstanceDetailsDescrs(
			List<InstanceDetailsDescr> instanceDetailsDescrs) {
		this.instanceDetailsDescrs = instanceDetailsDescrs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodelInstanceInfo")
	@OrderBy
	public List<TmodelInstanceInfoDescr> getTmodelInstanceInfoDescrs() {
		return this.tmodelInstanceInfoDescrs;
	}
	public void setTmodelInstanceInfoDescrs(
			List<TmodelInstanceInfoDescr> tmodelInstanceInfoDescrs) {
		this.tmodelInstanceInfoDescrs = tmodelInstanceInfoDescrs;
	}

}
