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
package org.apache.juddi.function;

import org.apache.juddi.datastore.DataStore;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.error.AuthTokenExpiredException;
import org.apache.juddi.error.AuthTokenRequiredException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.registry.RegistryEngine;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public abstract class AbstractFunction implements IFunction
{
  // protected reference to parent Registry instance
  protected RegistryEngine registry = null;

  /**
   * @param registry
   */
  protected AbstractFunction(RegistryEngine registry)
  {
  	this.registry = registry;
  }
  
  /**
   * @return RegistryEngine
   */
  public RegistryEngine getRegistry()
  {
  	return this.registry;
  }
  
  /**
   * @param registry
   */
  public void setRegistry(RegistryEngine registry)
  {
    this.registry = registry;
  }

  /**
   * Verify the authentication parameters.
   */
  protected Publisher getPublisher(AuthInfo authInfo,DataStore dataStore)
    throws RegistryException
  {
    Publisher publisher = null;

    if ((authInfo == null) || (authInfo.getValue() == null))
      throw new AuthTokenRequiredException("authToken: null");

    String authToken = authInfo.getValue();
    if (authToken.trim().length() == 0)
      throw new AuthTokenRequiredException("authToken: "+authToken);

    publisher = dataStore.getAuthTokenPublisher(authToken);
    if (publisher == null)
      throw new AuthTokenRequiredException("authToken: "+authToken);

    if (dataStore.isAuthTokenExpired(authToken))
      throw new AuthTokenExpiredException("authToken: "+authToken);

    // Token is valid so 'touch' so that it's
    // internal 'expiration clock' is reset.
    dataStore.touchAuthToken(authToken);

    return publisher;
  }
}