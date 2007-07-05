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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datastore.DataStore;
import org.apache.juddi.datastore.DataStoreFactory;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.request.SaveSubscription;
import org.apache.juddi.datatype.response.Subscriptions;
import org.apache.juddi.error.AccountLimitExceededException;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.RequestDeniedException;
import org.apache.juddi.error.ResultSetTooLargeException;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.error.UserMismatchException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class SaveSubscriptionFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(SaveSubscriptionFunction.class);

  /**
   *
   */
  public SaveSubscriptionFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    SaveSubscription request = (SaveSubscription)regObject;
    String generic = request.getGeneric();

    // aquire a datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // TODO (UDDI v3) Implement save_subscription business logic.
      
      dataStore.commit();

      Subscriptions subs = new Subscriptions();
      subs.setGeneric(generic);
      subs.setOperator(Config.getOperator());
      return subs;
    }
    catch(InvalidKeyPassedException ex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(ex);
      throw (RegistryException)ex;
    }
    catch(UnsupportedException ex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(ex);
      throw (RegistryException)ex;
    }
    catch(ResultSetTooLargeException ex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(ex);
      throw (RegistryException)ex;
    }
    catch(AccountLimitExceededException ex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(ex);
      throw (RegistryException)ex;
    }
    catch(UserMismatchException ex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(ex);
      throw (RegistryException)ex;
    }
    catch(RequestDeniedException ex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(ex);
      throw (RegistryException)ex;
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
