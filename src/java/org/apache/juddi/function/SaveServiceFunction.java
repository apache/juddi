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
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.GetAuthToken;
import org.apache.juddi.datatype.request.SaveBusiness;
import org.apache.juddi.datatype.request.SaveService;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.ServiceDetail;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.service.BusinessServices;
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
public class SaveServiceFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(SaveServiceFunction.class);

  /**
   *
   */
  public SaveServiceFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    SaveService request = (SaveService)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();
    Vector serviceVector = request.getBusinessServiceVector();
    UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);
      String publisherID = publisher.getPublisherID();

      // Validate request parameters & execute
      for (int i=0; i<serviceVector.size(); i++)
      {
        // Move the BusinessService data into a form we can work with easily
        BusinessService service = (BusinessService)serviceVector.elementAt(i);
        String businessKey = service.getBusinessKey();
        String serviceKey = service.getServiceKey();

        // If a BusinessKey wasn't included or it is an invalid BusinessKey then
        // throw an InvalidKeyPassedException
        if ((businessKey == null) || (businessKey.length() == 0) || (!dataStore.isValidBusinessKey(businessKey)))
          throw new InvalidKeyPassedException("businessKey="+businessKey);

        // Confirm that 'publisherID' controls the BusinessEntity that this
        // BusinessService belongs to.  If not then throw a UserMismatchException.
        if (!dataStore.isBusinessPublisher(businessKey,publisherID))
          throw new UserMismatchException("businessKey="+serviceKey);

        // If a ServiceKey was specified then make sure it's a valid one.
        if (((serviceKey != null) && (serviceKey.length() > 0)) && (!dataStore.isValidServiceKey(serviceKey)))
          throw new InvalidKeyPassedException("serviceKey="+serviceKey);

        // Normally, a valid tModelKey MUST be specified for the keyedReference 
        // to be valid. However, in the case of a keyedReference that is used in 
        // a categoryBag, the tModelKey may be omitted or specified as a 
        // zero-length string to indicate that the taxonomy being used is
        // uddi-org:general_keywords. When it is omitted in this manner, the UDDI 
        // registry will insert the proper key during the save_xx operation.
        // - UDDI Programmers API v2.04 Section 4.3.5.1 Specifying keyedReferences
        //
        CategoryBag categoryBag = service.getCategoryBag();
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

      for (int i=0; i<serviceVector.size(); i++)
      {
        // move the BusinessService data into a form we can work with easily
        BusinessService service = (BusinessService)serviceVector.elementAt(i);
        String serviceKey = service.getServiceKey();

        // If the new BusinessService has a ServiceKey then it must already
        // exists so delete the old one. It a ServiceKey isn't specified then
        // this is a new BusinessService so create a new ServiceKey for it.
        if ((serviceKey != null) && (serviceKey.length() > 0))
          dataStore.deleteService(serviceKey);
        else
          service.setServiceKey(uuidgen.uuidgen());

        // everything checks out so let's save it.
        dataStore.saveService(service);
      }

      dataStore.commit();

      // create a new ServiceDetail and stuff the
      // original but 'updated' serviceVector in.
      ServiceDetail detail = new ServiceDetail();
      detail.setGeneric(generic);
      detail.setOperator(Config.getOperator());
      detail.setTruncated(false);
      detail.setBusinessServiceVector(serviceVector);
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
      // create & execute the GetAuthToken request
      GetAuthToken authTokenRequest = new GetAuthToken("sviens","password");
      AuthToken authToken = (AuthToken)reg.execute(authTokenRequest);
      AuthInfo authInfo = authToken.getAuthInfo();

      // generate a Name Vector
      Vector nameVector = new Vector();
      nameVector.add(new Name("Dow Chemical"));

      // generate a BusinessService
      BusinessService service = new BusinessService();
      service.addName(new Name("Reaction Finder"));
      service.addDescription(new Description("Finds side effects when combining chemicals"));

      // generate a BusinessServices
      BusinessServices services = new BusinessServices();
      services.addBusinessService(service);

      // generate a BusinessEntity
      BusinessEntity businessEntity = new BusinessEntity();
      businessEntity.setBusinessKey(null);
      businessEntity.setNameVector(nameVector);
      businessEntity.setBusinessServices(services);

      // generate a BusinessEntity Vector
      Vector businessEntityVector = new Vector();
      businessEntityVector.add(businessEntity);

      // create & execute the SaveBusiness request
      SaveBusiness request = new SaveBusiness();
      request.setAuthInfo(authInfo);
      request.setBusinessEntityVector(businessEntityVector);
      BusinessDetail detail = (BusinessDetail)reg.execute(request);
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