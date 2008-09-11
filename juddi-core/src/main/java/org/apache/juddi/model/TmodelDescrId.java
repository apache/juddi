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
import javax.persistence.Embeddable;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Embeddable
public class TmodelDescrId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String tmodelKey;
	private int tmodelDescrId;

	public TmodelDescrId() {
	}

	public TmodelDescrId(String tmodelKey, int tmodelDescrId) {
		this.tmodelKey = tmodelKey;
		this.tmodelDescrId = tmodelDescrId;
	}

	@Column(name = "tmodel_key", nullable = false, length = 41)
	public String getTmodelKey() {
		return this.tmodelKey;
	}

	public void setTmodelKey(String tmodelKey) {
		this.tmodelKey = tmodelKey;
	}

	@Column(name = "tmodel_descr_id", nullable = false)

	public int getTmodelDescrId() {
		return this.tmodelDescrId;
	}

	public void setTmodelDescrId(int tmodelDescrId) {
		this.tmodelDescrId = tmodelDescrId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TmodelDescrId))
			return false;
		TmodelDescrId castOther = (TmodelDescrId) other;

		return ((this.getTmodelKey() == castOther.getTmodelKey()) || (this
				.getTmodelKey() != null
				&& castOther.getTmodelKey() != null && this.getTmodelKey()
				.equals(castOther.getTmodelKey())))
				&& (this.getTmodelDescrId() == castOther.getTmodelDescrId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getTmodelKey() == null ? 0 : this.getTmodelKey().hashCode());
		result = 37 * result + this.getTmodelDescrId();
		return result;
	}

}
