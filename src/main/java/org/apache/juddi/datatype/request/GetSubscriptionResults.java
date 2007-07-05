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

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.subscription.CoveragePeriod;

/**
 * Used to register or update complete information about a publisher.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class GetSubscriptionResults implements RegistryObject,Subscribe
{
  String generic;
  AuthInfo authInfo;
  String subscriptionKey;
  CoveragePeriod coveragePeriod;
  String chunkToken;

  /**
   *
   */
  public GetSubscriptionResults()
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
   * @return Returns the chunkToken.
   */
  public String getChunkToken()
  {
    return chunkToken;
  }

  /**
   * @param chunkToken The chunkToken to set.
   */
  public void setChunkToken(String chunkToken)
  {
    this.chunkToken = chunkToken;
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
}