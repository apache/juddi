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

import java.util.Properties;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.juddi.datastore.DataStore;
import org.apache.juddi.datastore.DataStoreFactory;
import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.DiscoveryURL;
import org.apache.juddi.datatype.DiscoveryURLs;
import org.apache.juddi.datatype.Email;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.datatype.business.Contacts;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.GetAuthToken;
import org.apache.juddi.datatype.request.SaveBusiness;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.service.BusinessServices;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UnsupportedException;
import org.apache.juddi.error.UserMismatchException;
import org.apache.juddi.handler.BusinessEntityHandler;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;
import org.apache.juddi.uuidgen.UUIDGen;
import org.apache.juddi.uuidgen.UUIDGenFactory;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class SaveBusinessFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(SaveBusinessFunction.class);

  /**
   *
   */
  public SaveBusinessFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    SaveBusiness request = (SaveBusiness)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();
    Vector businessVector = request.getBusinessEntityVector();
    Vector uploadRegVector = request.getUploadRegisterVector();
    UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

    // UploadRegistry functionality is not currently supported.
    if ((uploadRegVector != null) && (uploadRegVector.size() > 0))
      throw new UnsupportedException("Saving BusinessEntities via " +
        "UploadRegistry is not supported.");

    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // Validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);

      String publisherID = publisher.getPublisherID();
      String authorizedName = publisher.getName();

      // Validate request parameters & execute
      for (int i=0; i<businessVector.size(); i++)
      {
        // Move the BusinessEntity into a form we can work with easily
        BusinessEntity business = (BusinessEntity)businessVector.elementAt(i);

        String businessKey = business.getBusinessKey();

        // If a BusinessKey was specified then make sure it's a valid one.
        if ((businessKey != null) && (businessKey.length() > 0) && (!dataStore.isValidBusinessKey(businessKey)))
          throw new InvalidKeyPassedException("businessKey="+businessKey);

        // If a BusinessKey was specified then make sure 'publisherID' controls it.
        if ((businessKey != null) && (businessKey.length() > 0) && (!dataStore.isBusinessPublisher(businessKey,publisherID)))
          throw new UserMismatchException("businessKey="+businessKey);

        // Normally, a valid tModelKey MUST be specified for the keyedReference 
        // to be valid. However, in the case of a keyedReference that is used in 
        // a categoryBag, the tModelKey may be omitted or specified as a 
        // zero-length string to indicate that the taxonomy being used is
        // uddi-org:general_keywords. When it is omitted in this manner, the UDDI 
        // registry will insert the proper key during the save_xx operation.
        // - UDDI Programmers API v2.04 Section 4.3.5.1 Specifying keyedReferences
        //
        CategoryBag categoryBag = business.getCategoryBag();
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

      for (int i=0; i<businessVector.size(); i++)
      {
        // move the BusinessEntity into a form we can work with easily
        BusinessEntity business = (BusinessEntity)businessVector.elementAt(i);

        String businessKey = business.getBusinessKey();

        // If the new BusinessEntity has a BusinessKey then it must already
        // exists so delete the old one. It a BusinessKey isn't specified then
        // this is a new BusinessEntity so create a new BusinessKey for it.
        //
        if ((businessKey != null) && (businessKey.length() > 0))
        {
          dataStore.deleteBusiness(businessKey);
        }
        else
        {
          business.setBusinessKey(uuidgen.uuidgen());
        }

        // check if the business has DiscoveryURL with
        // useType as 'businessEntity' if not create one
        // and add it to the business object.
        //
        addBusinessEntityDiscoveryURL(business);

        // Everything checks out so let's save it. First
        // store 'authorizedName' and 'operator' values
        // in each BusinessEntity.
        //
        business.setAuthorizedName(authorizedName);
        business.setOperator(Config.getOperator());

        // If no contacts were specified with the Business
        // Entity then add a new contact of type 'publisher'
        // using the publishers information.

        Contacts contacts = business.getContacts();
        if ((contacts == null) ||
            (contacts.getContactVector() == null) ||
            (contacts.getContactVector().isEmpty()))
        {
          Contact contact = new Contact();
          contact.setPersonNameValue(publisher.getName());
          contact.setUseType("publisher");

          String email = publisher.getEmailAddress();
          if (email != null)
            contact.addEmail(new Email(email,"email"));

          business.addContact(contact);
        }

        dataStore.saveBusiness(business,publisherID);
      }

      dataStore.commit();

      BusinessDetail detail = new BusinessDetail();
      detail.setGeneric(generic);
      detail.setOperator(Config.getOperator());
      detail.setTruncated(false);
      detail.setBusinessEntityVector(businessVector);
      return detail;
    }
    catch(UnsupportedException suppex)
    {
      try { dataStore.rollback(); } catch(Exception e) { }
      log.info(suppex);
      throw (RegistryException)suppex;
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

  private void addBusinessEntityDiscoveryURL(BusinessEntity business)
  {
    // get the discovery URLs from the business entity
    DiscoveryURLs discoveryURLs = business.getDiscoveryURLs();

    boolean businessEntityURLExists = false;
    String businessEntityUseType = BusinessEntityHandler.TAG_NAME;

    if(discoveryURLs != null)
    {
      Vector discoveryURLVector = discoveryURLs.getDiscoveryURLVector();
      if(discoveryURLVector != null)
      {

        String useType  = null;

        for (int j=0; j<discoveryURLVector.size(); j++)
        {
          DiscoveryURL discoveryURL = (DiscoveryURL)discoveryURLVector.get(j);
          if(discoveryURL != null)
          {
            useType = discoveryURL.getUseType();
            // check if the business has DiscoveryURL with useType as 'businessEntity'
            if(useType.equals(businessEntityUseType))
            {
              businessEntityURLExists = true;
              j = discoveryURLVector.size();
            }
          }
        }
      }
    }
    else
    {
      // create create a discoveryURLs and add it to business entity
      business.setDiscoveryURLs(new DiscoveryURLs());
    }

    // add the businessEntity if businessEntity URL doesn't exist
    if(!businessEntityURLExists)
    {
      // get the DiscoveryURL from the juddi properties file
      StringBuffer siteURL = new StringBuffer();
      siteURL.append(Config.getOperatorURL());
      siteURL.append("/uddiget.jsp?businesskey=");
      siteURL.append(business.getBusinessKey());
      
      // add to the business entity
      business.addDiscoveryURL(new DiscoveryURL(businessEntityUseType,siteURL.toString()));
    }
  }
  
  
  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    // be sure to use the jUDDI-manged pool
    Properties props = new Properties();
    props.put("juddi.useConnectionPool","true");

    // initialize the registry
    RegistryEngine reg = new RegistryEngine(props);
    reg.init();

    try
    {
      // create & execute the GetAuthToken request
      GetAuthToken authTokenRequest = new GetAuthToken("sviens","password");
      AuthToken authToken = (AuthToken)reg.execute(authTokenRequest);
      AuthInfo authInfo = authToken.getAuthInfo();

      // generate a Name Vector
      Vector nameVector = new Vector();
      nameVector.add(new Name("IBM"));
      nameVector.add(new Name("Microsoft"));

      // generate a new BusinessService
      BusinessService service = new BusinessService();
      service.setNameVector(nameVector);
      
      // generate a BusinessService Vector
      Vector serviceVector = new Vector();
      serviceVector.add(service);
      
      // generate a BusinessServices instance
      BusinessServices services = new BusinessServices();
      services.setBusinessServiceVector(serviceVector);
      
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
      
      System.out.println(detail);
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
