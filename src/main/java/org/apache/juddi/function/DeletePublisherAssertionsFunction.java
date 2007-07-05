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
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.request.AddPublisherAssertions;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.request.DeletePublisherAssertions;
import org.apache.juddi.datatype.request.GetAuthToken;
import org.apache.juddi.datatype.request.SaveBusiness;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.registry.RegistryEngine;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class DeletePublisherAssertionsFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(DeletePublisherAssertionsFunction.class);

  /**
   *
   */
  public DeletePublisherAssertionsFunction(RegistryEngine registry)
  {
    super(registry);
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    // extract individual parameters
    DeletePublisherAssertions request = (DeletePublisherAssertions)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();
    Vector assertionVector = request.getPublisherAssertionVector();

    // aquire a jUDDI datastore instance
    DataStore dataStore = DataStoreFactory.getDataStore();

    try
    {
      dataStore.beginTrans();

      // validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);
      String publisherID = publisher.getPublisherID();

      // validate request parameters
      for (int i=0; i<assertionVector.size(); i++)
      {
        // nothing that requires validation has been identified
      }

      // delete the PublisherAssertions
      dataStore.deleteAssertions(publisherID,assertionVector);
      dataStore.commit();
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

    // We didn't encounter any problems so let's create an
    // E_SUCCESS Result, embed it in a DispositionReport
    // and return it.
    Result result = new Result(Result.E_SUCCESS);
    result.setErrCode(Result.lookupErrCode(Result.E_SUCCESS));    
    DispositionReport dispRpt = new DispositionReport();
    dispRpt.setGeneric(generic);
    dispRpt.setOperator(Config.getOperator());
    dispRpt.addResult(result);
    
    return dispRpt;
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
      // generate an AuthToken
      GetAuthToken authTokenRequest = new GetAuthToken("sviens","password");
      GetAuthTokenFunction authTokenService = new GetAuthTokenFunction(reg);
      AuthToken authToken = (AuthToken)authTokenService.execute(authTokenRequest);
      String authInfo = authToken.getAuthInfo().getValue();

      // create a couple of BusinessEntities
      BusinessEntity business1 = new BusinessEntity();
      business1.addName(new Name("Blockbuster","en"));

      BusinessEntity business2 = new BusinessEntity();
      business2.addName(new Name("Moonlighting","en"));

      Vector businessVector = new Vector(2);
      businessVector.addElement(business1);
      businessVector.addElement(business2);

      // create a SaveBusiness request & invoke the server
      SaveBusiness sbReq = new SaveBusiness();
      sbReq.setAuthInfo(new AuthInfo(authInfo));
      sbReq.setBusinessEntityVector(businessVector);
      BusinessDetail detail = (BusinessDetail)(new SaveBusinessFunction(reg).execute(sbReq));
      Vector detailVector = detail.getBusinessEntityVector();
      BusinessEntity b1 = (BusinessEntity)detailVector.elementAt(0);
      BusinessEntity b2 = (BusinessEntity)detailVector.elementAt(1);

      // create a new PublisherAssertion
      String fromKey = b1.getBusinessKey();
      String toKey = b2.getBusinessKey();
      KeyedReference keyedReference = new KeyedReference ("Partner Company","peer-peer");
      keyedReference.setTModelKey(TModel.RELATIONSHIPS_TMODEL_KEY);
      PublisherAssertion assertion = new PublisherAssertion(fromKey,toKey,keyedReference);

      // create a PublisherAssertion Vector
      Vector assertionVector = new Vector();
      assertionVector.addElement(assertion);

      // create an AddPublisherAssertions request & invoke the server
      AddPublisherAssertions apaReq = new AddPublisherAssertions();
      apaReq.setAuthInfo(new AuthInfo(authInfo));
      apaReq.setPublisherAssertionVector(assertionVector);
      DispositionReport dspRpt1 = (DispositionReport)(new AddPublisherAssertionsFunction(reg).execute(apaReq));
      System.out.println("errno: "+dspRpt1.toString());
      DispositionReport dspRpt2 = (DispositionReport)(new AddPublisherAssertionsFunction(reg).execute(apaReq));
      System.out.println("errno: "+dspRpt2.toString());

      // create an DeletePublisherAssertions request & invoke the server
      DeletePublisherAssertions dpaReq = new DeletePublisherAssertions();
      dpaReq.setAuthInfo(new AuthInfo(authInfo));
      dpaReq.setPublisherAssertionVector(assertionVector);
      DispositionReport response = (DispositionReport)(new DeletePublisherAssertionsFunction(reg).execute(dpaReq));
      System.out.println("errno: "+response.toString());
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