/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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