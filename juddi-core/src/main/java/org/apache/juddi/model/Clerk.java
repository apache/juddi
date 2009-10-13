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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "j3_clerk")
public class Clerk implements java.io.Serializable {

	@Transient
	private static final long serialVersionUID = -4175742578534548023L;
	@Id()
	@Column(name="clerk_name", nullable = false, length=255)
	private String clerkName;
	@Column(name="publisher_id", nullable = false, length=255)
	private String publisherId;
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	private Node node;
	@Column(name="cred", length=255)
	private String cred;
	
	public Clerk() {}

	public Clerk(String clerkName, String cred, Node node, String publisherId) {
		super();
		this.clerkName = clerkName;
		this.cred = cred;
		this.node = node;
		this.publisherId = publisherId;
	}

	public String getClerkName() {
		return clerkName;
	}

	public void setClerkName(String clerkName) {
		this.clerkName = clerkName;
	}

	public String getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(String publisherId) {
		this.publisherId = publisherId;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public String getCred() {
		return cred;
	}

	public void setCred(String cred) {
		this.cred = cred;
	}

}
