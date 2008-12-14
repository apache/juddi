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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "tmodel_overview")
public class TmodelOverview implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int tmodelOverviewId;
	private String tmodelKey;
	private Tmodel tmodel;
	private String overviewUrl;

	public TmodelOverview() {
	}

	public TmodelOverview(int tmodelOverviewId, String tmodelKey, Tmodel tmodel) {
		this.tmodelOverviewId = tmodelOverviewId;
		this.tmodelKey = tmodelKey;
		this.tmodel = tmodel;
	}
	public TmodelOverview(int tmodelOverviewId, String tmodelKey, Tmodel tmodel,
			String overviewUrl) {
		this.tmodelOverviewId = tmodelOverviewId;
		this.tmodelKey = tmodelKey;
		this.overviewUrl = overviewUrl;
		this.tmodel = tmodel;
	}

	@Id
	@Column(name = "tmodel_overview_id", nullable = false)

	public int getTmodelOverviewId() {
		return this.tmodelOverviewId;
	}

	public void setTmodelOverviewId(int tmodelOverviewId) {
		this.tmodelOverviewId = tmodelOverviewId;
	}

	@Column(name = "tmodel_key", nullable = false, length = 255)
	public String getTmodelKey() {
		return this.tmodelKey;
	}

	public void setTmodelKey(String tmodelKey) {
		this.tmodelKey = tmodelKey;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tmodel_key", nullable = false, insertable = false, updatable = false)
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

}
