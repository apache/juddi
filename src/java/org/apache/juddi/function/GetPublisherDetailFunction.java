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
  public GetPublisherDetailFunction()
  {
    super();
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
    DataStoreFactory factory = DataStoreFactory.getFactory();
    DataStore dataStore = factory.acquireDataStore();

    try
    {
      dataStore.beginTrans();

      for (int i=0; i<idVector.size(); i++)
      {
        String publisherID = (String)idVector.elementAt(i);

        // If the a Publisher doesn't exist hrow an UnknownUserException.
        if ((publisherID == null) || (publisherID.length() == 0) ||
            (dataStore.getPublisher(publisherID) == null))
          throw new UnknownUserException("PublisherID: "+publisherID);
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
    finally
    {
      factory.releaseDataStore(dataStore);
    }

  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
  }
}