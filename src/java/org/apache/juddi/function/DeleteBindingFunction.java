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

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datastore.DataStore;
import org.apache.juddi.datastore.DataStoreFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.DeleteBinding;
import org.apache.juddi.datatype.request.GetAuthToken;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UserMismatchException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class DeleteBindingFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(DeleteBindingFunction.class);

  /**
   *
   */
  public DeleteBindingFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    // extract individual parameters
    DeleteBinding request = (DeleteBinding)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();
    Vector bindingKeyVector = request.getBindingKeyVector();

    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);
      String publisherID = publisher.getPublisherID();

      // validate request parameters
      for (int i=0; i<bindingKeyVector.size(); i++)
      {
        // grab the next key from the vector
        String bindingKey = (String)bindingKeyVector.elementAt(i);

        // check that this binding template really exists.
        // If not then throw an InvalidKeyPassedException.
        if ((bindingKey == null) || (bindingKey.length() == 0) ||
            (!dataStore.isValidBindingKey(bindingKey)))
          throw new InvalidKeyPassedException(bindingKey);

        // check to make sure that 'authorizedName' controls the
        // business entity that this binding belongs to. If not
        // then throw a UserMismatchException.
        if (!dataStore.isBindingPublisher(bindingKey,publisherID))
          throw new UserMismatchException(bindingKey);
      }

      // delete the BindingTemplates
      for (int i=0; i<bindingKeyVector.size(); i++)
      {
        String bindingKey = (String)bindingKeyVector.elementAt(i);
        dataStore.deleteBinding(bindingKey);

        log.info("Publisher '"+publisherID+"' deleted BindingTemplate with key: "+bindingKey);
      }

      dataStore.commit();
    }
    catch(InvalidKeyPassedException keyex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(keyex.getMessage());
      throw (RegistryException)keyex;
    }
    catch(UserMismatchException umex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(umex.getMessage());
      throw (RegistryException)umex;
    }
    catch(RegistryException regex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.error(regex);
      throw (RegistryException)regex;
    }
    catch(Exception ex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.error(ex);
      throw new RegistryException(ex);
    }
    finally
    {
      if (dataStore != null)
        dataStore.release();
    }

    // We didn't encounter any problems so let's create an
    // E_SUCCESS Result, embed it in a DispositionReport
    // and return it.
    Result result = new Result(Result.E_SUCCESS);
    result.setErrCode(Result.lookupErrCode(Result.E_SUCCESS));    
    DispositionReport dispRpt = new DispositionReport();
    dispRpt.setGeneric(generic);
    dispRpt.setOperator(Config.getOperator());
    dispRpt.addResult(result);
    
    return dispRpt;
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    // initialize the registry
    RegistryEngine reg = new RegistryEngine();
    reg.init();

    try
    {
      // create & execute the GetAuthToken request
      GetAuthToken authTokenRequest = new GetAuthToken("sviens","password");
      AuthToken authToken = (AuthToken)reg.execute(authTokenRequest);
      AuthInfo authInfo = authToken.getAuthInfo();

      // create & execute the DeleteBinding request
      DeleteBinding request = new DeleteBinding();
      request.setAuthInfo(authInfo);
      DispositionReport response = (DispositionReport)reg.execute(request);
      System.out.println("errno: "+response.toString());
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