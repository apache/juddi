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

import org.apache.juddi.datatype.RegistryObject;

/**
 * Example:
 * 
 *   <subscriptionFilter>
 *     <find_service xmlns="urn:uddi-org:api_v3" >
 *       <categoryBag>
 *         <keyedReference 
 *            tModeKey="uddi:ubr.uddi.org: taxonomy:unspsc"
 *            keyName="Beer"
 *            keyValue="50.20.20.02.00"/>
 *       </categoryBag>
 *     </find_service>
 *   </subscriptionFilter>
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class SubscriptionFilter implements RegistryObject
{
	RegistryObject request = null;
	
  /**
   * default constructor
   */
  public SubscriptionFilter()
  {
  }

  /**
   * default constructor
   */
  public SubscriptionFilter(RegistryObject request)
  {
  	this.request = request;
  }
   
	/**
	 * @return Returns the request.
	 */
	public RegistryObject getRequest() 
	{
		return request;
	}
	
	/**
	 * @param request The request to set.
	 */
	public void setRequest(RegistryObject request) 
	{
		this.request = request;
	}
}