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

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Embeddable
public class TmodelIdentifierId implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String tmodelKey;
	private int identifierId;

	public TmodelIdentifierId() {
	}

	public TmodelIdentifierId(String tmodelKey, int identifierId) {
		this.tmodelKey = tmodelKey;
		this.identifierId = identifierId;
	}

	@Column(name = "tmodel_key", nullable = false, length = 41)
	public String getTmodelKey() {
		return this.tmodelKey;
	}

	public void setTmodelKey(String tmodelKey) {
		this.tmodelKey = tmodelKey;
	}

	@Column(name = "identifier_id", nullable = false)

	public int getIdentifierId() {
		return this.identifierId;
	}

	public void setIdentifierId(int identifierId) {
		this.identifierId = identifierId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TmodelIdentifierId))
			return false;
		TmodelIdentifierId castOther = (TmodelIdentifierId) other;

		return ((this.getTmodelKey() == castOther.getTmodelKey()) || (this
				.getTmodelKey() != null
				&& castOther.getTmodelKey() != null && this.getTmodelKey()
				.equals(castOther.getTmodelKey())))
				&& (this.getIdentifierId() == castOther.getIdentifierId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getTmodelKey() == null ? 0 : this.getTmodelKey().hashCode());
		result = 37 * result + this.getIdentifierId();
		return result;
	}

}
