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
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.request.FindRelatedBusinesses;
import org.apache.juddi.datatype.response.RelatedBusinessInfos;
import org.apache.juddi.datatype.response.RelatedBusinessesList;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class FindRelatedBusinessesFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(FindRelatedBusinessesFunction.class);

  /**
   *
   */
  public FindRelatedBusinessesFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    FindRelatedBusinesses request = (FindRelatedBusinesses)regObject;
    String generic = request.getGeneric();
    String businessKey = request.getBusinessKey();
    KeyedReference keyedRef = request.getKeyedReference();
    FindQualifiers qualifiers = request.getFindQualifiers();
    int maxRows = request.getMaxRows();

    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      Vector infoVector = null;
      boolean truncatedResults = false;

      // validate request parameters & execute
      // nothing that requires validation has been identified

      // perform the search for matching business entities (return only keys in requested order)
      infoVector = dataStore.findRelatedBusinesses(businessKey,keyedRef,qualifiers);
      if ((infoVector != null) && (infoVector.size() > 0))
      {
        // if the number of keys returned is greater than maxRows then truncate the results.
        int rowCount = infoVector.size();
        if ((maxRows > 0) && (maxRows < rowCount))
        {
          rowCount = maxRows;
          truncatedResults = true;
        }
      }

      // create a new BusinessInfos instance and stuff
      // the new Vector of BusinessInfos into it.
      RelatedBusinessInfos infos = new RelatedBusinessInfos();
      infos.setRelatedBusinessInfoVector(infoVector);

      dataStore.commit();

      // create a new RelatedBusinessesList instance and
      // stuff the new relatedBusinessInfoVector into it.
      RelatedBusinessesList list = new RelatedBusinessesList();
      list.setGeneric(generic);
      list.setOperator(Config.getOperator());
      list.setTruncated(truncatedResults);
      list.setRelatedBusinessInfos(infos);
      return list;
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