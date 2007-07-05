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
package org.apache.juddi.datatype.publisher;

import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class Publisher implements RegistryObject
{
	private String publisherID;
	private String nameValue;
	private String emailAddress;
	private boolean admin;
	private boolean enabled;  
	private int maxBusinesses;
	private int maxServicesPerBusiness;
	private int maxBindingsPerService;
	private int maxTModels;

	/**
	 *
	 */
	public Publisher()
	{
	}

	/**
	 *
	 */
	public Publisher(String pubID,String name)
	{
	    this.publisherID = pubID;
	    this.nameValue = name;
	}

	/**
	 *
	 */
	public Publisher(String pubID,String name,boolean adminValue)
	{
		this(pubID,name);
		this.admin = adminValue;
	}

	/**
	 *
	 */
	public void setPublisherID(String pubID)
	{
		this.publisherID = pubID;
	}

	/**
	 *
	 */
	public String getPublisherID()
	{
		return this.publisherID;
	}

	/**
	 * Sets the name of this Publisher to the given name.
	 *
	 * @param name The new name of this Publisher.
	 */
	public void setName(String name)
	{
		this.nameValue = name;
	}
	
	/**
	 * Returns the name of this Publisher as a String.
	 *
	 * @return The name of this Publisher.
	 */
	public String getName()
	{
	    return this.nameValue;
	}

	/**
	 * Sets the name of this Publisher to the given name.
	 *
	 * @param name The new name of this Publisher.
	 */
	public void setName(Name name)
	{
	    if (name != null)
	    	this.nameValue = name.getValue();
	    else
	    	this.nameValue = null;
	}

	/**
	 *
	 */
	public void setEmailAddress(String email)
	{
		this.emailAddress = email;
	}

    /**
     *
     */
    public String getEmailAddress()
  	{
    	return this.emailAddress;
  	}

    /**
     *
     */
    public void setAdmin(boolean adminValue)
    {
    	this.admin = adminValue;
    }

    /**
     *
     */
  	public void setAdminValue(String adminValue)
  	{
	    if (adminValue == null)
	      this.admin = false;
	    else
	      this.admin = (adminValue.equalsIgnoreCase("true"));
  	}

  	/**
	 *
	 */
	public boolean isAdmin()
	{
		return this.admin;
	}

	/**
	 *
	 */
	public void setEnabled(boolean enabledValue)
	{
		this.enabled = enabledValue;
	}

	/**
	 *
	 */
	public void setEnabledValue(String enabledValue)
	{
		if (enabledValue == null)
			this.enabled = false;
		else
			this.enabled = (enabledValue.equalsIgnoreCase("true"));
	}

	/**
	 *
	 */
	public boolean isEnabled()
	{
		return this.enabled;
	}

	/**
	 *
	 */
	public void setMaxBusinesses(int max)
	{
		this.maxBusinesses = max;
	}
	
	/**
	 *
	 */
	public int getMaxBusinesses()
	{
		return this.maxBusinesses;
	}
	
	/**
	 *
	 */
	public void setMaxServicesPerBusiness(int max)
	{
		this.maxServicesPerBusiness = max;
	}
	
	/**
	 *
	 */
	public int getMaxServicesPerBusiness()
	{
		return this.maxServicesPerBusiness;
	}
	
	/**
	 *
	 */
	public void setMaxBindingsPerService(int max)
	{
		this.maxBindingsPerService = max;
	}
	
	/**
	 *
	 */
	public int getMaxBindingsPerService()
	{
		return this.maxBindingsPerService;
	}
	
	/**
	 *
	 */
	public void setMaxTModels(int max)
	{
		this.maxTModels = max;
	}
	
	/**
	 *
	 */
	public int getMaxTModels()
	{
		return this.maxTModels;
	}
}