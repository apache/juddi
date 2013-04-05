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

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt Stam</a>
 */

@Embeddable
class TempKeyPK implements java.io.Serializable {
	
	private static final long serialVersionUID = 790951819871694597L;
	private String entityKey;
	private String txId;
	
	public TempKeyPK(){
	}
	
	@Column(name = "entity_key", nullable = false, length = 255)
	public String getEntityKey() {
		return entityKey;
	}
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	@Column(name = "tx_id", nullable = false, length = 255)
	public String getTxId() {
		return txId;
	}
	public void setTxId(String txId) {
		this.txId = txId;
	}
	
	public int compareTo(TempKeyPK o) {
		if (o==null || o.getEntityKey()==null || o.getTxId()==null) return 0;
		if (o.getEntityKey().equals(getEntityKey()) && o.getTxId().equals(getTxId())) return 1;
		else return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj!=null && obj instanceof TempKeyPK) {
			int i = compareTo((TempKeyPK) obj);
			if (i==1) return true;
			else return false;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            append(txId).
            append(entityKey).
            toHashCode();
    }
}


