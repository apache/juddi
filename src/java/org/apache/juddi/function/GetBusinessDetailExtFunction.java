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
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.business.BusinessEntityExt;
import org.apache.juddi.datatype.request.GetBusinessDetailExt;
import org.apache.juddi.datatype.response.BusinessDetailExt;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class GetBusinessDetailExtFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(GetBusinessDetailExtFunction.class);

  /**
   *
   */
  public GetBusinessDetailExtFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    GetBusinessDetailExt request = (GetBusinessDetailExt)regObject;
    String generic = request.getGeneric();
    Vector businessKeyVector = request.getBusinessKeyVector();

    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // verify
      for (int i=0; i<businessKeyVector.size(); i++)
      {
        String businessKey = (String)businessKeyVector.elementAt(i);

        // If the a BusinessEntity doesn't exist hrow an InvalidKeyPassedException.
        if ((businessKey == null) || (businessKey.length() == 0) ||
            (!dataStore.isValidBusinessKey(businessKey)))
          throw new InvalidKeyPassedException(businessKey);
      }

      Vector businessEntityExtVector = new Vector();

      for (int i=0; i<businessKeyVector.size(); i++)
      {
        String businessKey = (String)businessKeyVector.elementAt(i);
        BusinessEntity business = dataStore.fetchBusiness(businessKey);
        BusinessEntityExt businessExt = new BusinessEntityExt(business);
        businessEntityExtVector.addElement(businessExt);
      }

      dataStore.commit();

      // create a new BusinessDetailExt and stuff the Vector of
      // new BusinessEntityExt instances into it.
      BusinessDetailExt detailExt = new BusinessDetailExt();
      detailExt.setGeneric(generic);
      detailExt.setOperator(Config.getOperator());
      detailExt.setBusinessEntityExtVector(businessEntityExtVector);
      return detailExt;
    }
    catch(InvalidKeyPassedException keyex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(keyex.getMessage());
      throw (RegistryException)keyex;
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