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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "transfer_token_keys")
public class TransferTokenKey implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private TransferTokenKeyId id;
	private TransferToken transferToken;
	private String entityKey;

	public TransferTokenKey() {
	}

	public TransferTokenKey(TransferTokenKeyId id, TransferToken transferToken, String entityKey) {
		this.id = id;
		this.transferToken = transferToken;
		this.entityKey = entityKey;
	}

	@EmbeddedId
	public TransferTokenKeyId getId() {
		return this.id;
	}
	public void setId(TransferTokenKeyId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transfer_token", nullable = false, insertable = false, updatable = false)
	public TransferToken getTransferToken() {
		return this.transferToken;
	}
	public void setTransferToken(TransferToken transferToken) {
		this.transferToken = transferToken;
	}

	@Column(name = "entity_key", length = 255)
	public String getEntityKey() {
		return this.entityKey;
	}
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}


}
