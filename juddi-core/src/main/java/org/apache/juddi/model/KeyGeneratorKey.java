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
 */
@Entity
@Table(name = "uddi_publisher_keygenerators")
public class KeyGeneratorKey implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private UddiEntityPublisher publisher;
	private String keygenTModelKey;

	public KeyGeneratorKey() {
	}

	public KeyGeneratorKey(UddiEntityPublisher publisher, String keygenTModelKey) {
		this.publisher = publisher;
		this.keygenTModelKey = keygenTModelKey;
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
	@JoinColumn(name = "authorized_name", nullable = false)
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
