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
import org.apache.juddi.datatype.request.GetRegisteredInfo;
import org.apache.juddi.datatype.response.BusinessInfos;
import org.apache.juddi.datatype.response.RegisteredInfo;
import org.apache.juddi.datatype.response.TModelInfos;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class GetRegisteredInfoFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(GetRegisteredInfoFunction.class);

  /**
   *
   */
  public GetRegisteredInfoFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    GetRegisteredInfo request = (GetRegisteredInfo)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();

    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);
      String publisherID = publisher.getPublisherID();

      // validate request parameters
      // nothing that requires validation has been identified

      // declare some 'work' variables
      BusinessInfos businessInfos = new BusinessInfos();
      TModelInfos tModelInfos = new TModelInfos();
      Vector keyVector = null;
      Vector infoVector = null;

      // perform the search for BusinessEntities registered to publisherID
      keyVector = dataStore.findRegisteredBusinesses(publisherID);
      if ((keyVector != null) && (keyVector.size() > 0))
      {
        int rowCount = keyVector.size();

        // iterate through the business entity keys fetching each associated BusinessInfo.
        infoVector = new Vector(rowCount);
        for (int i=0; i<rowCount; i++)
          infoVector.addElement(dataStore.fetchBusinessInfo((String)keyVector.elementAt(i)));

        businessInfos.setBusinessInfoVector(infoVector);
      }

      // perform the search for TModels registered to publisherID
      keyVector = dataStore.findRegisteredTModels(publisherID);
      if ((keyVector != null) && (keyVector.size() > 0))
      {
        int rowCount = keyVector.size();

        // iterate through the tModel keys fetching each associated TModelInfo.
        infoVector = new Vector(rowCount);
        for (int i=0; i<rowCount; i++)
          infoVector.addElement(dataStore.fetchTModelInfo((String)keyVector.elementAt(i)));

        tModelInfos.setTModelInfoVector(infoVector);
      }

      dataStore.commit();

      // create a new BusinessInfos instance and stuff
      // the new Vector of BusinessInfos into it.
      RegisteredInfo info = new RegisteredInfo();
      info.setGeneric(generic);
      info.setOperator(Config.getOperator());
      info.setBusinessInfos(businessInfos);
      info.setTModelInfos(tModelInfos);
      return info;
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