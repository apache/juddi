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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author <a href="mailto:kstam@apache.org">Kurt Stam</a>
 */
@Entity
@Table(name = "j3_temp_key")
public class TempKey implements java.io.Serializable {

	private static final long serialVersionUID = -2763025628473227781L;
	
	private TempKeyPK pk;
	
	public TempKey(){
	}
	
	@Id
	public TempKeyPK getPk() {
		return pk;
	}
	
	public void setPk(TempKeyPK pk) {
		this.pk = pk;
	}

	public void setPk(String txId, String entityKey) {
		TempKeyPK pk = new TempKeyPK();
		pk.setEntityKey(entityKey);
		pk.setTxId(txId);
		this.pk = pk;
	}
	
}
