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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.auth.Authenticator;
import org.apache.juddi.auth.AuthenticatorFactory;
import org.apache.juddi.datastore.DataStore;
import org.apache.juddi.datastore.DataStoreFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.GetAuthToken;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnknownUserException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class GetAuthTokenFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(GetAuthTokenFunction.class);

  /**
   *
   */
  public GetAuthTokenFunction()
  {
    super();
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    GetAuthToken request = (GetAuthToken)regObject;
    String generic = request.getGeneric();
    String userID = request.getUserID();
    String cred = request.getCredential();

    // aquire a jUDDI datastore instance
    DataStoreFactory dataFactory = DataStoreFactory.getFactory();
    DataStore dataStore = dataFactory.acquireDataStore();

    // aquire a jUDDI Authenticator instance
    Authenticator authenticator = AuthenticatorFactory.getAuthenticator();

    try
    {
      // begin this transaction
      dataStore.beginTrans();

      // authenticate the requestor's credentials
      String publisherID = authenticator.authenticate(userID,cred);
      if (publisherID == null)
        throw new UnknownUserException("user ID: "+userID);

      // ensure the user has the authority to publish
      Publisher publisher = dataStore.getPublisher(publisherID);
      if (publisher == null)
        throw new UnknownUserException("user ID: "+userID);

      // generate a new token (optionally using publisher info)
      String token = dataStore.generateToken(publisher);

      // save auth token value to persistent storage
      dataStore.storeAuthToken(token,publisher);

      // commit this transaction
      dataStore.commit();

      // create, populate and return an AuthToken object
      AuthToken authToken = new AuthToken();
      authToken.setGeneric(generic);
      authToken.setOperator(Config.getOperator());
      authToken.setAuthInfo(new AuthInfo(token));
      return authToken;
    }
    catch(Exception ex)
    {
      // we must rollback for *any* exception
      try { dataStore.rollback(); }
      catch(Exception e) { }

      // write to the log
      log.error(ex);

      // prep RegistryFault to throw
      if (ex instanceof RegistryException)
        throw (RegistryException)ex;
      else
        throw new RegistryException(ex);
    }
    finally {
      dataFactory.releaseDataStore(dataStore);
    }
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    // initialize the registry
    RegistryEngine reg = RegistryEngine.getInstance();
    reg.init();

    try
    {
      // valid request
      GetAuthToken request = new GetAuthToken("sviens","password");
      AuthToken response = (AuthToken)(new GetAuthTokenFunction().execute(request));
      System.out.println("Function: getAuthToken(sviens/password)");
      System.out.println(" AuthInfo: "+response.getAuthInfo());

      // invalid (unknown user) request
      request = new GetAuthToken("jdoe","password");
      System.out.println("Function: getAuthToken(jdoe/password)");
      response = (AuthToken)(new GetAuthTokenFunction().execute(request));
      System.out.println(" AuthInfo: "+response.getAuthInfo());

      // invalid (invalid credential) request
      request = new GetAuthToken("guest","password");
      System.out.println("Function: getAuthToken(guest/password)");
      response = (AuthToken)(new GetAuthTokenFunction().execute(request));
      System.out.println(" AuthInfo: "+response.getAuthInfo());
    }
    catch (Exception ex)
    {
      // write execption to the console
      ex.printStackTrace();
    }
    finally
    {
      // destroy the registry
      reg.dispose();
    }
  }
}