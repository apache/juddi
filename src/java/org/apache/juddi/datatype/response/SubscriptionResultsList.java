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
package org.apache.juddi.datatype.response;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.subscription.CoveragePeriod;
import org.apache.juddi.datatype.subscription.KeyBag;
import org.apache.juddi.datatype.subscription.Subscription;

/**
 * Example:
 *
 *  <subscriptionResultsList>
 *    <coveragePeriod>
 *      <startPoint>20020727T00:00:00</startPoint>
 *      <endPoint>20020728T00:00:00</endPoint>
 *    </coveragePeriod>
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
 *    <keyBag>
 *      <deleted>false</deleted>
 *      <serviceKey>uddi:BeerSupplies.com:maltSelectionService</serviceKey>
 *      <serviceKey>uddi:Containers.com:kegs:orderingService</serviceKey>
 *    </keyBag>
 *  </subscriptionResultsList>
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class SubscriptionResultsList implements RegistryObject
{
  String generic;
  String operator;
  
  CoveragePeriod coveragePeriod;
  Subscription subscription;
  KeyBag keyBag;
  
  /**
   * default constructor
   */
  public SubscriptionResultsList()
  {
  }
  
  /**
   *
   * @param genericValue
   */
  public void setGeneric(String genericValue)
  {
    this.generic = genericValue;
  }

  /**
   *
   * @return String UDDI generic value.
   */
  public String getGeneric()
  {
    return this.generic;
  }

  /**
   *
   */
  public void setOperator(String operator)
  {
    this.operator = operator;
  }

  /**
   *
   */
  public String getOperator()
 	{
    return this.operator;
 	}
  
	/**
	 * @return Returns the coveragePeriod.
	 */
	public CoveragePeriod getCoveragePeriod() 
	{
		return coveragePeriod;
	}
	
	/**
	 * @param coveragePeriod The coveragePeriod to set.
	 */
	public void setCoveragePeriod(CoveragePeriod coveragePeriod) 
	{
		this.coveragePeriod = coveragePeriod;
	}
	
	/**
	 * @return Returns the keyBag.
	 */
	public KeyBag getKeyBag() 
	{
		return keyBag;
	}
	
	/**
	 * @param keyBag The keyBag to set.
	 */
	public void setKeyBag(KeyBag keyBag) 
	{
		this.keyBag = keyBag;
	}
	
	/**
	 * @return Returns the subscription.
	 */
	public Subscription getSubscription() 
	{
		return subscription;
	}
	
	/**
	 * @param subscription The subscription to set.
	 */
	public void setSubscription(Subscription subscription) 
	{
		this.subscription = subscription;
	}
}