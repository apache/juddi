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
package org.apache.juddi.datatype.request;

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.subscription.SubscriptionKey;

/**
 * Used to register or update complete information about a publisher.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class GetSubscriptions implements RegistryObject,Subscribe
{
  String generic;
  AuthInfo authInfo;
  Vector subscriptionKeyVector;

  /**
   *
   */
  public GetSubscriptions()
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
   * @return String request's generic value.
   */
  public String getGeneric()
  {
    return this.generic;
  }

  /**
   *
   */
  public void setAuthInfo(AuthInfo authInfo)
  {
    this.authInfo = authInfo;
  }

  /**
   *
   */
  public AuthInfo getAuthInfo()
  {
    return this.authInfo;
  }

  /**
   *
   */
  public Vector getSubscriptionKeyVector()
  {
    return this.subscriptionKeyVector;
  }

  /**
   * Add a SubscriptionKey to the collection of SubscriptionKeys
   *
   * @param key The new SubscriptionKey to add
   */
  public void addSubscriptionKey(SubscriptionKey key)
  {
    if ((key != null) && (key.getValue() != null))
      addSubscriptionKey(key.getValue());
  }

  /**
   * Add a SubscriptionKey to the collection of SubscriptionKeys
   *
   * @param key The new SubscriptionKey to add to the collection of SubscriptionKeys
   */
  public void addSubscriptionKey(String key)
  {
    if (subscriptionKeyVector == null)
      subscriptionKeyVector = new Vector();
    subscriptionKeyVector.add(key);
  }

  /**
   * Sets the SubscriptionKey Vector
   *
   * @param keys The new collection of SubscriptionKeys
   */
  public void setSubscriptionKeyVector(Vector keys)
  {
    this.subscriptionKeyVector = keys;
  }
}