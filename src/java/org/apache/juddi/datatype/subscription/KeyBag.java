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

import java.util.Vector;

import org.apache.juddi.datatype.BindingKey;
import org.apache.juddi.datatype.BusinessKey;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.ServiceKey;
import org.apache.juddi.datatype.TModelKey;

/**
 * Example:
 * 
 *    <keyBag>
 *      <deleted>false</deleted>
 *      <serviceKey>uddi:BeerSupplies.com:maltSelectionService</serviceKey>
 *      <serviceKey>uddi:Containers.com:kegs:orderingService</serviceKey>
 *    </keyBag>
 * 
 * @author Steve Viens (sviens@apache.org)
 */
public class KeyBag implements RegistryObject
{
	boolean deleted;
	Vector tModelKeyVector;
	Vector businessKeyVector;
	Vector serviceKeyVector;
	Vector bindingKeyVector;
	
  /**
   * default constructor
   */
  public KeyBag()
  {
  }
  
	/**
	 * @return Returns the deleted.
	 */
	public boolean isDeleted() 
	{
		return deleted;
	}
	
	/**
	 * @param deleted The deleted to set.
	 */
	public void setDeleted(boolean deleted) 
	{
		this.deleted = deleted;
	}

	/**
	 * @return Returns the bindingKeyVector.
	 */
	public Vector getBindingKeyVector() 
	{
		return bindingKeyVector;
	}
	
	/**
	 * @param bindingKeyVector The bindingKeyVector to set.
	 */
	public void setBindingKeyVector(Vector bindingKeyVector) 
	{
		this.bindingKeyVector = bindingKeyVector;
	}
	
  /**
   *
   */
  public void addBindingKey(BindingKey bindingKey)
  {
    if ((bindingKey != null) && (bindingKey.getValue() != null))
      this.addBindingKey(bindingKey.getValue());
  }

  /**
   *
   */
  public void addBindingKey(String bindingKey)
  {
    if (this.bindingKeyVector == null)
      this.bindingKeyVector = new Vector();
    this.bindingKeyVector.add(bindingKey);
  }

  /**
	 * @return Returns the businessKeyVector.
	 */
	public Vector getBusinessKeyVector() 
	{
		return businessKeyVector;
	}
	
	/**
	 * @param businessKeyVector The businessKeyVector to set.
	 */
	public void setBusinessKeyVector(Vector businessKeyVector) 
	{
		this.businessKeyVector = businessKeyVector;
	}
	
  /**
   *
   */
  public void addBusinessKey(BusinessKey businessKey)
  {
    if ((businessKey != null) && (businessKey.getValue() != null))
      this.addBusinessKey(businessKey.getValue());
  }

  /**
   *
   */
  public void addBusinessKey(String businessKey)
  {
    if (this.businessKeyVector == null)
      this.businessKeyVector = new Vector();
    this.businessKeyVector.add(businessKey);
  }

  /**
	 * @return Returns the serviceKeyVector.
	 */
	public Vector getServiceKeyVector() 
	{
		return serviceKeyVector;
	}
	
	/**
	 * @param serviceKeyVector The serviceKeyVector to set.
	 */
	public void setServiceKeyVector(Vector serviceKeyVector) 
	{
		this.serviceKeyVector = serviceKeyVector;
	}
	
  /**
   *
   */
  public void addServiceKey(ServiceKey serviceKey)
  {
    if ((serviceKey != null) && (serviceKey.getValue() != null))
      addServiceKey(serviceKey.getValue());
  }

  /**
   *
   */
  public void addServiceKey(String serviceKey)
  {
    if (this.serviceKeyVector == null)
      this.serviceKeyVector = new Vector();
    this.serviceKeyVector.add(serviceKey);
  }

  /**
	 * @return Returns the tModelKeyVector.
	 */
	public Vector getTModelKeyVector() 
	{
		return tModelKeyVector;
	}
	
	/**
	 * @param modelKeyVector The tModelKeyVector to set.
	 */
	public void setTModelKeyVector(Vector modelKeyVector) 
	{
		tModelKeyVector = modelKeyVector;
	}

  /**
   *
   */
  public void addTModelKey(TModelKey tModelKey)
  {
    if ((tModelKey != null) && (tModelKey.getValue() != null))
      addTModelKey(tModelKey.getValue());
  }

  /**
   *
   */
  public void addTModelKey(String tModelKey)
  {
    if (this.tModelKeyVector == null)
      this.tModelKeyVector = new Vector();
    this.tModelKeyVector.add(tModelKey);
  }
}