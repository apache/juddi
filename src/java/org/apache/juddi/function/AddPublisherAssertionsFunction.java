/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
import org.apache.juddi.datatype.request.GetAuthToken;
import org.apache.juddi.datatype.request.SaveBusiness;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.InvalidKeyPassedException;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.error.UserMismatchException;
import org.apache.juddi.util.Config;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class AddPublisherAssertionsFunction extends AbstractFunction
{
  // private reference to jUDDI Logger
  private static Log log = LogFactory.getLog(AddPublisherAssertionsFunction.class);

  /**
   *
   */
  public AddPublisherAssertionsFunction()
  {
    super();
  }

  /**
   *
   */
  public RegistryObject execute(RegistryObject regObject)
    throws RegistryException
  {
    // extract individual parameters
    AddPublisherAssertions request = (AddPublisherAssertions)regObject;
    AuthInfo authInfo = request.getAuthInfo();
    Vector assertionVector = request.getPublisherAssertionVector();
    String generic = request.getGeneric();

    // aquire a jUDDI datastore instance
    DataStoreFactory factory = DataStoreFactory.getFactory();
    DataStore dataStore = factory.acquireDataStore();

    try
    {
      dataStore.beginTrans();

      // validate authentication parameters
      Publisher publisher = getPublisher(authInfo,dataStore);
      String publisherID = publisher.getPublisherID();

      // validate request parameters & execute
      for (int i=0; i<assertionVector.size(); i++)
      {
        // transform each PublisherAssertion data into a form we can work with easily
        PublisherAssertion assertion = (PublisherAssertion)assertionVector.elementAt(i);

        // make sure we've got a 'fromKey'
        String fromKey = assertion.getFromKey();
        if ((fromKey == null) || (fromKey.length() == 0))
          throw new InvalidKeyPassedException("FromKey: "+fromKey);

        // make sure we've got a 'toKey'
        String toKey = assertion.getToKey();
        if ((toKey == null) || (toKey.length() == 0))
          throw new InvalidKeyPassedException("ToKey: "+toKey);

        // make sure we've got a 'KeyedRefernce'
        KeyedReference keyedRef = assertion.getKeyedReference();
        if (keyedRef == null)
          throw new InvalidKeyPassedException("KeyedRef: "+keyedRef);

        // make sure the 'KeyedRefernce' contains a 'TModelKey'
        String tModelKey = keyedRef.getTModelKey();
        if ((tModelKey == null) || (tModelKey.length() == 0))
          throw new InvalidKeyPassedException("TModelKey: "+keyedRef);

        // verify that the BusinessEntities or tModel identified by the 'fromKey'
        // really exists. If not then throw an InvalidKeyPassedException.
        if ((!dataStore.isValidBusinessKey(fromKey)) && (!dataStore.isValidTModelKey(fromKey)))
          throw new InvalidKeyPassedException("FromKey: "+fromKey);

        // verify that the BusinessEntitys or tModel identified by the 'fromKey'
        // really exists. If not then throw an InvalidKeyPassedException.
        if ((!dataStore.isValidBusinessKey(toKey)) && (!dataStore.isValidTModelKey(toKey)))
          throw new InvalidKeyPassedException("ToKey: "+toKey);

        // verify that the 'publisherID' controls at least one of the
        // BusinessEntities or TModels that are identified in this
        // assertion. If not then throw a UserMismatchException.
        if ((!dataStore.isBusinessPublisher(fromKey,publisherID)) &&
            (!dataStore.isBusinessPublisher(toKey,publisherID))   &&
            (!dataStore.isTModelPublisher(fromKey,publisherID))   &&
            (!dataStore.isTModelPublisher(toKey,publisherID)))
          throw new UserMismatchException("fromKey: "+fromKey+" or toKey: "+toKey);
      }

      dataStore.saveAssertions(publisherID,assertionVector);
      dataStore.commit();
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

    // didn't encounter an exception so let's create
    // and return a successfull DispositionReport
    DispositionReport dispRpt = new DispositionReport();
    dispRpt.setGeneric(generic);
    dispRpt.setOperator(Config.getOperator());
    dispRpt.addResult(new Result(Result.E_SUCCESS));
    return dispRpt;
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
      // create & execute the GetAuthToken1 request
      GetAuthToken authTokenRequest1 = new GetAuthToken("sviens","password");
      AuthToken authToken1 = (AuthToken)reg.execute(authTokenRequest1);
      AuthInfo authInfo1 = authToken1.getAuthInfo();

      // create a couple of business entities
      BusinessEntity business1 = new BusinessEntity();
      business1.addName(new Name("Blockbuster","en"));

      // BusinessEntity Vector
      Vector businessVector1 = new Vector(1);
      businessVector1.addElement(business1);

      // create & execute the SaveBusiness request
      SaveBusiness sbReq1 = new SaveBusiness();
      sbReq1.setAuthInfo(authInfo1);
      sbReq1.setBusinessEntityVector(businessVector1);
      BusinessDetail detail1 = (BusinessDetail)reg.execute(sbReq1);
      Vector detailVector1 = detail1.getBusinessEntityVector();
      BusinessEntity b1 = (BusinessEntity)detailVector1.elementAt(0);


      // create & execute the GetAuthToken2 request
      GetAuthToken authTokenRequest2 = new GetAuthToken("steveviens","password");
      AuthToken authToken2 = (AuthToken)reg.execute(authTokenRequest2);
      AuthInfo authInfo2 = authToken2.getAuthInfo();

      // create a BusinessEntity
      BusinessEntity business2 = new BusinessEntity();
      business2.addName(new Name("PopSecret","en"));

      // create a BusinessEntity Vector
      Vector businessVector2 = new Vector(1);
      businessVector2.addElement(business2);

      // create & execute the SaveBusiness request
      SaveBusiness sbReq2 = new SaveBusiness();
      sbReq2.setAuthInfo(authInfo2);
      sbReq2.setBusinessEntityVector(businessVector2);
      BusinessDetail detail2 = (BusinessDetail)reg.execute(sbReq2);
      Vector detailVector2 = detail2.getBusinessEntityVector();
      BusinessEntity b2 = (BusinessEntity)detailVector2.elementAt(0);


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
      apaReq.setAuthInfo(authInfo1);
      apaReq.setPublisherAssertionVector(assertionVector);
      DispositionReport dspRpt1 = (DispositionReport)reg.execute(apaReq);
      System.out.println("errno: "+dspRpt1.toString());
      System.out.println();
      DispositionReport dspRpt2 = (DispositionReport)reg.execute(apaReq);
      System.out.println("errno: "+dspRpt2.toString());
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