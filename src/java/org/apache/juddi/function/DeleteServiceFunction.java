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
import org.apache.juddi.datatype.request.DeleteService;
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
public class DeleteServiceFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(DeleteServiceFunction.class);

  /**
   *
   */
  public DeleteServiceFunction(RegistryEngine registry)
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
    DeleteService request = (DeleteService)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();
    Vector serviceKeyVector = request.getServiceKeyVector();

    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);
      String publisherID = publisher.getPublisherID();

      // validate request parameters
      for (int i=0; i<serviceKeyVector.size(); i++)
      {
        // grab the next key from the vector
        String serviceKey = (String)serviceKeyVector.elementAt(i);

        // check that this business server really exists.
        // If not then throw an InvalidKeyPassedException.
        if ((serviceKey == null) || (serviceKey.length() == 0) ||
            (!dataStore.isValidServiceKey(serviceKey)))
          throw new InvalidKeyPassedException(serviceKey);

        // check to make sure that 'authorizedName' controls the
        // business entity that this server belongs to. If not
        // then throw a UserMismatchException.
        if (!dataStore.isServicePublisher(serviceKey,publisherID))
          throw new UserMismatchException("serviceKey="+serviceKey);
      }

      // delete the BusinessServices
      for (int i=0; i<serviceKeyVector.size(); i++)
      {
        String serviceKey = (String)serviceKeyVector.elementAt(i);
        dataStore.deleteService(serviceKey);

        log.info("Publisher '"+publisherID+"' deleted BusinessService with key: "+serviceKey);
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
      // create a request
      DeleteService request = new DeleteService();

      // invoke the server
      DispositionReport response = (DispositionReport)(new DeleteServiceFunction(reg).execute(request));
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