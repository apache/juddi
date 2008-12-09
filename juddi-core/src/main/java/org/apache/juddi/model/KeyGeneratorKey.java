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
@Table(name = "uddi_publisher_keygenerators")
public class KeyGeneratorKey implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private KeyGeneratorKeyId id;
	private UddiEntityPublisher publisher;
	private String keygenTModelKey;

	public KeyGeneratorKey() {
	}

	public KeyGeneratorKey(KeyGeneratorKeyId id, UddiEntityPublisher publisher, String keygenTModelKey) {
		this.id = id;
		this.publisher = publisher;
		this.keygenTModelKey = keygenTModelKey;
	}

	@EmbeddedId
	public KeyGeneratorKeyId getId() {
		return this.id;
	}
	public void setId(KeyGeneratorKeyId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "authorized_name", nullable = false, insertable = false, updatable = false)
	public UddiEntityPublisher getPublisher() {
		return this.publisher;
	}
	public void setPublisher(UddiEntityPublisher publisher) {
		this.publisher = publisher;
	}

	@Column(name = "keygen_tmodel_key", length = 255)
	public String getKeygenTModelKey() {
		return this.keygenTModelKey;
	}
	public void setKeygenTModelKey(String keygenTModelKey) {
		this.keygenTModelKey = keygenTModelKey;
	}


}
