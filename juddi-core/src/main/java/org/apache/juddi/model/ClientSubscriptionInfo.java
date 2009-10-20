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
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 */
@Entity
@Table(name = "j3_client_subscriptioninfo")
public class ClientSubscriptionInfo implements java.io.Serializable {

	@Transient
	private static final long serialVersionUID = -1739525637445572028L;
	
	@Id()
	@Column(name="subscription_key", nullable = false, length=255)
	private String subscriptionKey;
	@ManyToOne(fetch = FetchType.EAGER)
	private Clerk fromClerk;
	@ManyToOne(fetch = FetchType.EAGER)
	private Clerk toClerk;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_notified", length = 29)
	private Date lastNotified;
	
	public ClientSubscriptionInfo(){}
	
	public ClientSubscriptionInfo(Clerk fromClerk, Clerk toClerk, Date lastNotified,
			String subscriptionKey) {
		super();
		this.fromClerk = fromClerk;
		this.toClerk = toClerk;
		this.lastNotified = lastNotified;
		this.subscriptionKey = subscriptionKey;
	}
	public String getSubscriptionKey() {
		return subscriptionKey;
	}
	public void setSubscriptionKey(String subscriptionKey) {
		this.subscriptionKey = subscriptionKey;
	}
	public Clerk getFromClerk() {
		return fromClerk;
	}
	public void setFromClerk(Clerk fromClerk) {
		this.fromClerk = fromClerk;
	}
	public Clerk getToClerk() {
		return toClerk;
	}
	public void setToClerk(Clerk toClerk) {
		this.toClerk = toClerk;
	}
	public Date getLastNotified() {
		return lastNotified;
	}
	public void setLastNotified(Date lastNotified) {
		this.lastNotified = lastNotified;
	}
	
	

}
