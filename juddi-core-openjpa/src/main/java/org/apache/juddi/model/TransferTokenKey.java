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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "j3_transfer_token_keys")
public class TransferTokenKey implements java.io.Serializable {

	private static final long serialVersionUID = 8163650692808546199L;
	private Long id;
	private TransferToken transferToken;
	private String entityKey;

	public TransferTokenKey() {
	}

	public TransferTokenKey(TransferToken transferToken, String entityKey) {
		this.transferToken = transferToken;
		this.entityKey = entityKey;
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
	@JoinColumn(name = "transfer_token", nullable = false)
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
