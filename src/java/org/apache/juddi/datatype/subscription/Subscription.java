/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
package org.apache.juddi.datatype.subscription;

import java.util.Date;

import org.apache.juddi.datatype.BindingKey;
import org.apache.juddi.datatype.RegistryObject;

/**
 * Example:
 * 
 *    <subscription brief="true">
 *      <subscriptionFilter>
 *        <find_service xmlns="urn:uddi-org:api_v3" >
 *          <categoryBag>
 *            <keyedReference 
 *              tModeKey="uddi:ubr.uddi.org: taxonomy:unspsc"
 *              keyName="Beer"
 *              keyValue="50.20.20.02.00"/>
 *          </categoryBag>
 *        </find_service>
 *      </subscriptionFilter>
 *      <bindingKey> 
 *        BindingKey of the subscribers NotifySubscriptionListener service
 *      </bindingKey>
 *      <notificationInterval>P1D</notificationInterval>
 *      <maxEntities>1000</maxEntities>
 *      <expiresAfter>20030101T00:00:00</expiresAfter>
 *    </subscription>
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class Subscription implements RegistryObject
{
	String subscriptionKey;
	SubscriptionFilter subscriptionFilter;
	String notificationInterval;
	int maxEntities;
	Date expiresAfter;
	BindingKey bindingKey;
	boolean brief;
	
  /**
   * default constructor
   */
  public Subscription()
  {
  }
  
	/**
	 * @return Returns the subscriptionKey.
	 */
	public String getSubscriptionKey()
  {
		return subscriptionKey;
	}
	
	/**
	 * @param subscriptionKey The subscriptionKey to set.
	 */
	public void setSubscriptionKey(String subscriptionKey) 
	{
		this.subscriptionKey = subscriptionKey;
	}

	/**
	 * @return Returns the bindingKey.
	 */
	public BindingKey getBindingKey() 
	{
		return bindingKey;
	}
	
	/**
	 * @param bindingKey The bindingKey to set.
	 */
	public void setBindingKey(BindingKey bindingKey) 
	{
		this.bindingKey = bindingKey;
	}
	
	/**
	 * @return Returns the brief.
	 */
	public boolean isBrief() 
	{
		return brief;
	}
	
	/**
	 * @param brief The brief to set.
	 */
	public void setBrief(boolean brief) 
	{
		this.brief = brief;
	}
	
	/**
	 * @return Returns the expiresAfter.
	 */
	public Date getExpiresAfter() 
	{
		return expiresAfter;
	}
	
	/**
	 * @param expiresAfter The expiresAfter to set.
	 */
	public void setExpiresAfter(Date expiresAfter) 
	{
		this.expiresAfter = expiresAfter;
	}
	
	/**
	 * @return Returns the maxEntities.
	 */
	public int getMaxEntities() 
	{
		return maxEntities;
	}
	
	/**
	 * @param maxEntities The maxEntities to set.
	 */
	public void setMaxEntities(int maxEntities) 
	{
		this.maxEntities = maxEntities;
	}
	
	/**
	 * @return Returns the notificationInterval.
	 */
	public String getNotificationInterval() 
	{
		return notificationInterval;
	}
	
	/**
	 * @param notificationInterval The notificationInterval to set.
	 */
	public void setNotificationInterval(String notificationInterval) 
	{
		this.notificationInterval = notificationInterval;
	}
	
	/**
	 * @return Returns the subscriptionFilter.
	 */
	public SubscriptionFilter getSubscriptionFilter() 
	{
		return subscriptionFilter;
	}
	
	/**
	 * @param subscriptionFilter The subscriptionFilter to set.
	 */
	public void setSubscriptionFilter(SubscriptionFilter subscriptionFilter) 
	{
		this.subscriptionFilter = subscriptionFilter;
	}
}