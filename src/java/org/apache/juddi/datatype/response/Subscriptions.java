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

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.subscription.Subscription;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class Subscriptions implements RegistryObject
{
  String generic;
  String operator;
  Vector subscriptionVector;

  /**
   * default constructor
   */
  public Subscriptions()
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
   *
   */
  public void addSubscription(Subscription subscription)
  {
    if (this.subscriptionVector == null)
      this.subscriptionVector = new Vector();
    this.subscriptionVector.add(subscription);
  }

  /**
   *
   */
  public void setSubscriptionVector(Vector subscriptions)
  {
    this.subscriptionVector = subscriptions;
  } 

  /**
   *
   */
  public Vector getSubscriptionVector()
  {
    return subscriptionVector;
  }
}