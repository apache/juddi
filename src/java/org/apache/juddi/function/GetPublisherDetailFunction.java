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
import org.apache.juddi.datatype.request.GetPublisherDetail;
import org.apache.juddi.datatype.response.PublisherDetail;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnknownUserException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class GetPublisherDetailFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(GetPublisherDetailFunction.class);

  /**
   *
   */
  public GetPublisherDetailFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    GetPublisherDetail request = (GetPublisherDetail)regObject;
    String generic = request.getGeneric();
    Vector idVector = request.getPublisherIDVector();

    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      for (int i=0; i<idVector.size(); i++)
      {
        String publisherID = (String)idVector.elementAt(i);

        // If the a Publisher doesn't exist hrow an UnknownUserException.
        if ((publisherID == null) || (publisherID.length() == 0) ||
            (dataStore.getPublisher(publisherID) == null))
          throw new UnknownUserException("get_publisher: "+
              "userID="+publisherID);
      }

      Vector publisherVector = new Vector();

      for (int i=0; i<idVector.size(); i++)
      {
        String publisherID = (String)idVector.elementAt(i);
        publisherVector.add(dataStore.getPublisher(publisherID));
      }

      dataStore.commit();

      // create a new PublisherDetail and stuff the new tModelVector into it.
      PublisherDetail detail = new PublisherDetail();
      detail.setGeneric(generic);
      detail.setPublisherVector(publisherVector);
      detail.setOperator(Config.getOperator());
      return detail;
    }
    catch(UnknownUserException ukuex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(ukuex.getMessage());
      throw (RegistryException)ukuex;
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