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
import org.apache.juddi.datatype.request.DeletePublisherAssertions;
import org.apache.juddi.datatype.request.GetAuthToken;
import org.apache.juddi.datatype.request.SaveBusiness;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.datatype.response.BusinessDetail;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.error.RegistryException;
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
  public DeletePublisherAssertionsFunction()
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
    DeletePublisherAssertions request = (DeletePublisherAssertions)regObject;
    String generic = request.getGeneric();
    AuthInfo authInfo = request.getAuthInfo();
    Vector assertionVector = request.getPublisherAssertionVector();

    // aquire a jUDDI datastore instance
    DataStoreFactory factory = DataStoreFactory.getFactory();
    DataStore dataStore = factory.acquireDataStore();

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
      // generate an AuthToken
      GetAuthToken authTokenRequest = new GetAuthToken("sviens","password");
      GetAuthTokenFunction authTokenService = new GetAuthTokenFunction();
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
      BusinessDetail detail = (BusinessDetail)(new SaveBusinessFunction().execute(sbReq));
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
      DispositionReport dspRpt1 = (DispositionReport)(new AddPublisherAssertionsFunction().execute(apaReq));
      System.out.println("errno: "+dspRpt1.toString());
      DispositionReport dspRpt2 = (DispositionReport)(new AddPublisherAssertionsFunction().execute(apaReq));
      System.out.println("errno: "+dspRpt2.toString());

      // create an DeletePublisherAssertions request & invoke the server
      DeletePublisherAssertions dpaReq = new DeletePublisherAssertions();
      dpaReq.setAuthInfo(new AuthInfo(authInfo));
      dpaReq.setPublisherAssertionVector(assertionVector);
      DispositionReport response = (DispositionReport)(new DeletePublisherAssertionsFunction().execute(dpaReq));
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