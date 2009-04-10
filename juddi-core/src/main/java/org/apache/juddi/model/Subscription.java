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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author <a href="mailto:kurt@apache.org">Kurt T Stam</a>
 * @author <a href="mailto:jfaath@apache.org">Jeff Faath</a>
 */
@Entity
@Table(name = "juddiv3_subscription")
public class Subscription implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String subscriptionKey;
	private String subscriptionFilter;
	private String bindingKey;
	private String notificationInterval;
	private Integer maxEntities;
	private Date expiresAfter;
	private Boolean brief;
	private List<SubscriptionMatch> subscriptionMatches = new ArrayList<SubscriptionMatch>(0);

	public Subscription() {
	}

	public Subscription(String subscriptionKey, String bindingKey,
			String notificationInterval) {
		this.subscriptionKey = subscriptionKey;
		this.bindingKey = bindingKey;
		this.notificationInterval = notificationInterval;
	}

	@Id
	@Column(name = "subscription_key", nullable = false, length = 255)
	public String getSubscriptionKey() {
		return this.subscriptionKey;
	}
	public void setSubscriptionKey(String subscriptionKey) {
		this.subscriptionKey = subscriptionKey;
	}

	@Lob
	@Column(name = "subscription_filter", nullable = false, length = 2048)
	public String getSubscriptionFilter() {
		return subscriptionFilter;
	}
	public void setSubscriptionFilter(String subscriptionFilter) {
		this.subscriptionFilter = subscriptionFilter;
	}
	
	@Column(name = "binding_key", length = 255)
	public String getBindingKey() {
		return this.bindingKey;
	}
	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}

	@Column(name = "notification_interval", nullable = false)
	public String getNotificationInterval() {
		return this.notificationInterval;
	}
	public void setNotificationInterval(String notificationInterval) {
		this.notificationInterval = notificationInterval;
	}

	@Column(name = "max_entities")
	public Integer getMaxEntities() {
		return maxEntities;
	}
	public void setMaxEntities(Integer maxEntities) {
		this.maxEntities = maxEntities;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expires_after", length = 29)
	public Date getExpiresAfter() {
		return expiresAfter;
	}
	public void setExpiresAfter(Date expiresAfter) {
		this.expiresAfter = expiresAfter;
	}

	@Column(name = "brief")
	public Boolean isBrief() {
		return brief;
	}
	public void setBrief(Boolean brief) {
		this.brief = brief;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "subscription")
	public List<SubscriptionMatch> getSubscriptionMatches() {
		return subscriptionMatches;
	}
	public void setSubscriptionMatches(List<SubscriptionMatch> subscriptionMatches) {
		this.subscriptionMatches = subscriptionMatches;
	}
}
