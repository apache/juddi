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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "tmodel_instance_info")
public class TmodelInstanceInfo implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private TmodelInstanceInfoId id;
	private BindingTemplate bindingTemplate;
	private String tmodelKey;
	private String instanceParms;
	private Set<InstanceDetailsDocDescr> instanceDetailsDocDescrs = new HashSet<InstanceDetailsDocDescr>(0);
	private Set<InstanceDetailsDescr> instanceDetailsDescrs = new HashSet<InstanceDetailsDescr>(0);
	private Set<TmodelInstanceInfoDescr> tmodelInstanceInfoDescrs = new HashSet<TmodelInstanceInfoDescr>(0);

	public TmodelInstanceInfo() {
	}

	public TmodelInstanceInfo(TmodelInstanceInfoId id,
			BindingTemplate bindingTemplate, String tmodelKey) {
		this.id = id;
		this.bindingTemplate = bindingTemplate;
		this.tmodelKey = tmodelKey;
	}
	public TmodelInstanceInfo(TmodelInstanceInfoId id,
			BindingTemplate bindingTemplate, String tmodelKey,
			String instanceParms,
			Set<InstanceDetailsDocDescr> instanceDetailsDocDescrs,
			Set<InstanceDetailsDescr> instanceDetailsDescrs,
			Set<TmodelInstanceInfoDescr> tmodelInstanceInfoDescrs) {
		this.id = id;
		this.bindingTemplate = bindingTemplate;
		this.tmodelKey = tmodelKey;
		this.instanceParms = instanceParms;
		this.instanceDetailsDocDescrs = instanceDetailsDocDescrs;
		this.instanceDetailsDescrs = instanceDetailsDescrs;
		this.tmodelInstanceInfoDescrs = tmodelInstanceInfoDescrs;
	}

	@EmbeddedId
	public TmodelInstanceInfoId getId() {
		return this.id;
	}
	public void setId(TmodelInstanceInfoId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entity_key", nullable = false, insertable = false, updatable = false)
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

	@Column(name = "instance_parms", length = 512)
	public String getInstanceParms() {
		return this.instanceParms;
	}
	public void setInstanceParms(String instanceParms) {
		this.instanceParms = instanceParms;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodelInstanceInfo")
	public Set<InstanceDetailsDocDescr> getInstanceDetailsDocDescrs() {
		return this.instanceDetailsDocDescrs;
	}
	public void setInstanceDetailsDocDescrs(
			Set<InstanceDetailsDocDescr> instanceDetailsDocDescrs) {
		this.instanceDetailsDocDescrs = instanceDetailsDocDescrs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodelInstanceInfo")
	public Set<InstanceDetailsDescr> getInstanceDetailsDescrs() {
		return this.instanceDetailsDescrs;
	}
	public void setInstanceDetailsDescrs(
			Set<InstanceDetailsDescr> instanceDetailsDescrs) {
		this.instanceDetailsDescrs = instanceDetailsDescrs;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "tmodelInstanceInfo")
	public Set<TmodelInstanceInfoDescr> getTmodelInstanceInfoDescrs() {
		return this.tmodelInstanceInfoDescrs;
	}
	public void setTmodelInstanceInfoDescrs(
			Set<TmodelInstanceInfoDescr> tmodelInstanceInfoDescrs) {
		this.tmodelInstanceInfoDescrs = tmodelInstanceInfoDescrs;
	}

}
