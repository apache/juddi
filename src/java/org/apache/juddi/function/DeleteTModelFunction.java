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
import org.apache.juddi.datatype.request.DeleteTModel;
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
public class DeleteTModelFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(DeleteTModelFunction.class);

  /**
   *
   */
  public DeleteTModelFunction(RegistryEngine registry)
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
    DeleteTModel request = (DeleteTModel)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();
    Vector tModelKeyVector = request.getTModelKeyVector();

    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);
      String publisherID = publisher.getPublisherID();

      // validate request parameters
      for (int i=0; i<tModelKeyVector.size(); i++)
      {
        // grab the next key from the vector
        String tModelKey = (String)tModelKeyVector.elementAt(i);

        // check that this TModel really exists. If not
        // then throw an InvalidKeyPassedException.
        if ((tModelKey == null) || (tModelKey.length() == 0) ||
            (!dataStore.isValidTModelKey(tModelKey)))
          throw new InvalidKeyPassedException(tModelKey);

        // check to make sure that 'authorizedName' controls this
        // TModel. If not then throw a UserMismatchException.
        if (!dataStore.isTModelPublisher(tModelKey,publisherID))
          throw new UserMismatchException("publisher="+publisherID+" tModelKey="+tModelKey);

        // TModel exists and we control it so let's marke it as deleted.
        dataStore.markTModelAsDeleted(tModelKey);
      }

      // delete the TModels
      for (int i=0; i<tModelKeyVector.size(); i++)
      {
        String tModelKey = (String)tModelKeyVector.elementAt(i);
        dataStore.deleteTModel(tModelKey);

        log.info("Publisher '"+publisherID+"' deleted TModel with key: "+tModelKey);
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
      DeleteTModel request = new DeleteTModel();

      // invoke the server
      DispositionReport response = (DispositionReport)(new DeleteTModelFunction(reg).execute(request));
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