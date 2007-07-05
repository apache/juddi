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
import org.apache.juddi.datatype.request.DeletePublisher;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class DeletePublisherFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(DeletePublisherFunction.class);

  /**
   *
   */
  public DeletePublisherFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   * Here are the rules:
   *
   * 1. A publisher must have administrative privs to
   *    delete anything.
   * 2. A publisher cannot delete itself.
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    // extract individual parameters
    DeletePublisher request = (DeletePublisher)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();
    Vector publisherIDVector = request.getPublisherIDVector();

    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);
      String publisherID = publisher.getPublisherID();

      // Only a user with administrative access can delete publishers
      if (!dataStore.isAdministrator(publisherID))
        throw new RegistryException("Invalid Operation, You must have " +
          "administrative priveledges to delete a publisher account.");

      // validate request parameters
      for (int i=0; i<publisherIDVector.size(); i++)
      {
        // grab the next key from the vector
        String pubID = (String)publisherIDVector.elementAt(i);

        if (pubID.equalsIgnoreCase(publisherID))
          throw new RegistryException("Invalid Operation, A publisher " +
            "cannot delete it's own publisher account.");

        // TModel exists and we control it so let's delete it.
        dataStore.deletePublisher(pubID);
      }

      // delete the TModels
      for (int i=0; i<publisherIDVector.size(); i++)
      {
        String tModelKey = (String)publisherIDVector.elementAt(i);
        dataStore.deleteTModel(tModelKey);

        log.info("Publisher '"+publisherID+"' deleted TModel with key: "+tModelKey);
      }

      dataStore.commit();
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