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
 *
 */


package org.apache.juddi.portlets.client.model;

import java.io.Serializable;


/**
 * <p>Java class for Subscription type.
 * 
 * @author <a href="mailto:kstam@apache.org">Kurt T Stam</a> 
 * 
 */
public class Subscription implements Serializable{
	private static final long serialVersionUID = 4857108890203941634L;
	private String bindingKey;
	private Boolean brief;
	private String expiresAfter;
	private Integer maxEntities;
	private String notificationInterval;
	private String subscriptionFilter;
	private String subscriptionKey;
    
    //for sync requests.
	private String coverageStart;
	private String coverageEnd;
    
    private Node node;
    private String fromClerkName;
    private String toClerkName;
    
	public Subscription() {
		super();
    }
    public Subscription(String bindingKey, Boolean brief, String expiresAfter,
			Integer maxEntities, String notificationInterval, String subscriptionFilter, String subscriptionKey) {
		super();
		this.bindingKey = bindingKey;
		this.brief = brief;
		this.expiresAfter = expiresAfter;
		this.maxEntities = maxEntities;
		this.notificationInterval = notificationInterval;
		this.subscriptionFilter = subscriptionFilter;
		this.subscriptionKey = subscriptionKey;
	}
    
	public String getBindingKey() {
		return bindingKey;
	}

	public void setBindingKey(String bindingKey) {
		this.bindingKey = bindingKey;
	}

	public Boolean getBrief() {
		return brief;
	}

	public void setBrief(Boolean brief) {
		this.brief = brief;
	}

	public Integer getMaxEntities() {
		return maxEntities;
	}

	public void setMaxEntities(Integer maxEntities) {
		this.maxEntities = maxEntities;
	}

	public String getSubscriptionKey() {
		return subscriptionKey;
	}

	public void setSubscriptionKey(String subscriptionKey) {
		this.subscriptionKey = subscriptionKey;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getExpiresAfter() {
		return expiresAfter;
	}

	public void setExpiresAfter(String expiresAfter) {
		this.expiresAfter = expiresAfter;
	}

	public String getNotificationInterval() {
		return notificationInterval;
	}

	public void setNotificationInterval(String notificationInterval) {
		this.notificationInterval = notificationInterval;
	}

	public String getSubscriptionFilter() {
		return subscriptionFilter;
	}
	
	public void setSubscriptionFilter(String subscriptionFilter) {
		this.subscriptionFilter = subscriptionFilter;
	}
	
	public Node getNode() {
			return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}
	public String getFromClerkName() {
		return fromClerkName;
	}
	public void setFromClerkName(String fromClerkName) {
		this.fromClerkName = fromClerkName;
	}
	public String getToClerkName() {
		return toClerkName;
	}
	public void setToClerkName(String toClerkName) {
		this.toClerkName = toClerkName;
	}
	public String getCoverageStart() {
		return coverageStart;
	}
	public void setCoverageStart(String coverageStart) {
		this.coverageStart = coverageStart;
	}
	public String getCoverageEnd() {
		return coverageEnd;
	}
	public void setCoverageEnd(String coverageEnd) {
		this.coverageEnd = coverageEnd;
	}

	
}
