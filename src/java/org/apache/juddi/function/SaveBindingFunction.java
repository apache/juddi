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
import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.binding.BindingTemplate;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.SaveBinding;
import org.apache.juddi.datatype.response.BindingDetail;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UserMismatchException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class SaveBindingFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(SaveBindingFunction.class);

  /**
   *
   */
  public SaveBindingFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    SaveBinding request = (SaveBinding)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();
    Vector bindingVector = request.getBindingTemplateVector();
    UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

    // Aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // Validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);
      String publisherID = publisher.getPublisherID();

      // Validate request parameters
      for (int i=0; i<bindingVector.size(); i++)
      {
        // Move the BindingTemplate into a form we can work with easily
        BindingTemplate binding = (BindingTemplate)bindingVector.elementAt(i);
        String serviceKey = binding.getServiceKey();
        String bindingKey = binding.getBindingKey();

        // Confirm that the 'BusinessService' that this binding belongs to
        // really exists. If not then throw an InvalidKeyPassedException.
       if ((serviceKey == null) || (serviceKey.length() == 0) || (!dataStore.isValidServiceKey(serviceKey)))
          throw new InvalidKeyPassedException("ServiceKey: "+serviceKey);

        // Confirm that 'publisherID' controls the BusinessService that this
        // binding template belongs to.  If not then throw a UserMismatchException.
        if (!dataStore.isServicePublisher(serviceKey,publisherID))
          throw new UserMismatchException("ServiceKey: "+serviceKey);

        // If a BindingKey was specified then make sure it's a valid one.
        if ((bindingKey != null) && (bindingKey.length() > 0) && (!dataStore.isValidBindingKey(bindingKey)))
          throw new InvalidKeyPassedException("BindingKey: "+bindingKey);

        // Normally, a valid tModelKey MUST be specified for the keyedReference 
        // to be valid. However, in the case of a keyedReference that is used in 
        // a categoryBag, the tModelKey may be omitted or specified as a 
        // zero-length string to indicate that the taxonomy being used is
        // uddi-org:general_keywords. When it is omitted in this manner, the UDDI 
        // registry will insert the proper key during the save_xx operation.
        // - UDDI Programmers API v2.04 Section 4.3.5.1 Specifying keyedReferences
        //
        CategoryBag categoryBag = binding.getCategoryBag();
        if (categoryBag != null)
        {
          Vector keyedRefVector = categoryBag.getKeyedReferenceVector();
          if (keyedRefVector != null)
          {
            int vectorSize = keyedRefVector.size();
            if (vectorSize > 0)
            {
              for (int j=0; j<vectorSize; j++)
              {
                KeyedReference keyedRef = (KeyedReference)keyedRefVector.elementAt(j);
                String key = keyedRef.getTModelKey();
                
                // A null or zero-length tModelKey is treated as 
                // though the tModelKey for uddiorg:general_keywords 
                // had been specified.
                //
                if ((key == null) || (key.trim().length() == 0))
                  keyedRef.setTModelKey(TModel.GENERAL_KEYWORDS_TMODEL_KEY);
              }
            }
          }
        }
      }

      for (int i=0; i<bindingVector.size(); i++)
      {
        // move the BindingTemplate data into a form we can work with easily
        BindingTemplate binding = (BindingTemplate)bindingVector.elementAt(i);
        String bindingKey = binding.getBindingKey();

        // If the new BindingTemplate has a BindingKey then it must already
        // exists so delete the old one. It a BindingKey isn't specified then
        // this is a new BindingTemplate so create a new BindingKey for it.
        if ((bindingKey != null) && (bindingKey.length() > 0))
          dataStore.deleteBinding(bindingKey);
        else
          binding.setBindingKey(uuidgen.uuidgen());

        // everything checks out so let's save it.
        dataStore.saveBinding(binding);
      }

      dataStore.commit();

      BindingDetail detail = new BindingDetail();
      detail.setGeneric(generic);
      detail.setOperator(Config.getOperator());
      detail.setTruncated(false);
      detail.setBindingTemplateVector(bindingVector);
      return detail;
    }
    catch(InvalidKeyPassedException ikpex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(ikpex);
      throw (RegistryException)ikpex;
    }
    catch(UserMismatchException umex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(umex);
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

