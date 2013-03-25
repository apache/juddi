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

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "j3_auth_token")
public class AuthToken implements java.io.Serializable {

	private static final long serialVersionUID = 1147567747533293480L;
	private String authToken;
	private String authorizedName;
	private Date created;
	private Date lastUsed;
	private int numberOfUses;
	private int tokenState;
	

	public AuthToken() {
	}

	public AuthToken(String authToken, String authorizedName,
			String publisherName, Date created, Date lastUsed,
			int numberOfUses, int tokenState) {
		this.authToken = authToken;
		this.authorizedName = authorizedName;
		this.created = created;
		this.lastUsed = lastUsed;
		this.numberOfUses = numberOfUses;
		this.tokenState = tokenState;
	}

	@Id
	@Column(name = "auth_token", nullable = false, length = 51)
	public String getAuthToken() {
		return this.authToken;
	}
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	@Column(name = "authorized_name", nullable = false, length = 255)
	public String getAuthorizedName() {
		return this.authorizedName;
	}
	public void setAuthorizedName(String authorizedName) {
		this.authorizedName = authorizedName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created", nullable = false, length = 29)
	public Date getCreated() {
		return this.created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_used", nullable = false, length = 29)
	public Date getLastUsed() {
		return this.lastUsed;
	}
	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

	@Column(name = "number_of_uses", nullable = false)
	public int getNumberOfUses() {
		return this.numberOfUses;
	}
	public void setNumberOfUses(int numberOfUses) {
		this.numberOfUses = numberOfUses;
	}

	@Column(name = "token_state", nullable = false)
	public int getTokenState() {
		return this.tokenState;
	}
	public void setTokenState(int tokenState) {
		this.tokenState = tokenState;
	}

}
