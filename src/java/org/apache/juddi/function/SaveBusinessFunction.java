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
import org.apache.juddi.datatype.DiscoveryURL;
import org.apache.juddi.datatype.DiscoveryURLs;
import org.apache.juddi.datatype.Email;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.Phone;
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
        "an UploadRegistry is not supported.");

    // aquire a jUDDI datastore instance
    DataStoreFactory dataFactory = DataStoreFactory.getFactory();
    DataStore dataStore = dataFactory.acquireDataStore();

    try
    {
      dataStore.beginTrans();

      // validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);

      String publisherID = publisher.getPublisherID();
      String authorizedName = publisher.getName();

      // validate request parameters & execute
      for (int i=0; i<businessVector.size(); i++)
      {
        // move the BusinessEntity into a form we can work with easily
        BusinessEntity business = (BusinessEntity)businessVector.elementAt(i);

        String businessKey = business.getBusinessKey();

        // If a BusinessKey was specified then make sure it's a valid one.
        if ((businessKey != null) && (businessKey.length() > 0) && (!dataStore.isValidBusinessKey(businessKey)))
          throw new InvalidKeyPassedException("BusinessKey: "+businessKey);

        // If a BusinessKey was specified then make sure 'publisherID' controls it.
        if ((businessKey != null) && (businessKey.length() > 0) && (!dataStore.isBusinessPublisher(businessKey,publisherID)))
          throw new UserMismatchException("BusinessKey: "+businessKey);
      }

      for (int i=0; i<businessVector.size(); i++)
      {
        // move the BusinessEntity into a form we can work with easily
        BusinessEntity business = (BusinessEntity)businessVector.elementAt(i);

        String businessKey = business.getBusinessKey();

        // If the new BusinessEntity has a BusinessKey then it must already
        // exists so delete the old one. It a BusinessKey isn't specified then
        // this is a new BusinessEntity so create a new BusinessKey for it.
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

        addBusinessEntityDiscoveryURL(business);

        // Everything checks out so let's save it. First
        // store 'authorizedName' and 'operator' values
        // in each BusinessEntity.

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

          String workPhone = publisher.getWorkPhone();
          if (workPhone != null)
            contact.addPhone(new Phone(workPhone,"business"));

          String mobile = publisher.getMobilePhone();
          if (mobile != null)
            contact.addPhone(new Phone(mobile,"mobile"));

          String pager = publisher.getPager();
          if (pager != null)
            contact.addPhone(new Phone(pager,"pager"));

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
      dataFactory.releaseDataStore(dataStore);
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
      siteURL.append(Config.getOperatorSiteURL());
      siteURL.append("/discovery?businessKey=");
      siteURL.append(business.getBusinessKey());
      // add to the business entity
      business.addDiscoveryURL(new DiscoveryURL(businessEntityUseType,
                  siteURL.toString()));
    }

  }
  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String[] args)
  {
    // initialize the registry
    org.apache.juddi.registry.RegistryEngine reg = org.apache.juddi.registry.RegistryEngine.getInstance();
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

      // generate a BusinessEntity
      BusinessEntity businessEntity = new BusinessEntity();
      businessEntity.setBusinessKey(null);
      businessEntity.setNameVector(nameVector);

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
