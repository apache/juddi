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
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Embeddable
public class TmodelDescrId extends Id implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int tmodelDescrId;

	public TmodelDescrId() {
	}

	public TmodelDescrId(String entityKey, int tmodelDescrId) {
		this.entityKey = entityKey;
		this.tmodelDescrId = tmodelDescrId;
	}

	@Column(name = "descr_id", nullable = false)
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

		return ((this.getEntityKey() == castOther.getEntityKey()) || (this
				.getEntityKey() != null
				&& castOther.getEntityKey() != null && this.getEntityKey()
				.equals(castOther.getEntityKey())))
				&& (this.getTmodelDescrId() == castOther.getTmodelDescrId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getEntityKey() == null ? 0 : this.getEntityKey().hashCode());
		result = 37 * result + this.getTmodelDescrId();
		return result;
	}

}
