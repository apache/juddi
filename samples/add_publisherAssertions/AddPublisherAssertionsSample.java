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

import org.apache.juddi.client.*;
import org.apache.juddi.datatype.*;
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.datatype.business.*;
import org.apache.juddi.datatype.request.*;
import org.apache.juddi.datatype.response.*;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.registry.*;

import java.util.Vector;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class AddPublisherAssertionsSample
{
  public static void main(String[] args)
  {
    // initialize the registry
    RegistryProxy reg = new RegistryProxy();

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
      GetAuthToken authTokenRequest2 = new GetAuthToken("sviens","password");
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
  }
}