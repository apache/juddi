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
package org.apache.juddi.uddi4j;

import java.util.Vector;

import org.uddi4j.UDDIException;
import org.uddi4j.datatype.Name;
import org.uddi4j.datatype.binding.BindingTemplate;
import org.uddi4j.datatype.business.BusinessEntity;
import org.uddi4j.datatype.service.BusinessService;
import org.uddi4j.response.AuthToken;
import org.uddi4j.response.BusinessDetail;
import org.uddi4j.response.ServiceDetail;
import org.uddi4j.transport.TransportException;


public class Test_save_binding extends UDDITestBase {
  protected boolean inited = false;
  protected BusinessService _businessService = null;

  public Test_save_binding() {
  }

  public void testCases() {
    if (! inited) {
      fail("initialization failed");
      return;
    }
    _simpleBinding();
  }

  public void setUp() {
    /**
     * create business && service
     */

    if (! PublisherManager.createPublisher("saveBindingPublisher","saveBindingPublisher")) {
      fail("Unable to create publisher");
      return;
    }

    AuthToken _bindingToken = null;
    try {
      _bindingToken = proxy.get_authToken("saveBindingPublisher", "password");
    }
    catch (TransportException ex1) {
      fail(ex1.toString());
    }
    catch (UDDIException ex1) {
      fail(ex1.toString());
    }

    BusinessEntity bEntity = new BusinessEntity();

    bEntity.setDefaultName(new Name("saveBindingBusiness"));
    Vector busVector = new Vector();
    busVector.add(bEntity);

    Vector victor = null;
    try {
      BusinessDetail busDetail = proxy.save_business(token.getAuthInfoString(),
          busVector);
      victor = busDetail.getBusinessEntityVector();
      assertEquals(victor.size(), 1);
    }
    catch (TransportException ex) {
      fail(ex.toString());
      return;
    }
    catch (UDDIException ex) {
      fail(ex.toString());
      return;
    }

    BusinessEntity returnedBusinessEntity = (BusinessEntity)victor.elementAt(0);

    BusinessService service = new BusinessService();
    service.setBusinessKey(returnedBusinessEntity.getBusinessKey());
    Name name = new Name("saveBindingService");
    service.setDefaultName(name);

    java.util.Vector servicesVector = new Vector();
    servicesVector.add(service);
    try {
      proxy.save_service(_bindingToken.getAuthInfoString(), servicesVector);
      ServiceDetail detail = proxy.save_service(token.getAuthInfoString(), servicesVector);
      servicesVector = detail.getBusinessServiceVector();
      assertEquals(servicesVector.size(), 1);
      _businessService = (BusinessService)servicesVector.elementAt(0);
      assertNotNull(_businessService);
    }
    catch (TransportException ex2) {
      fail(ex2.toString());
      return;
    }
    catch (UDDIException ex2) {
      fail(ex2.toString());
      return;
    }

    inited = true;
  }

  public void tearDown() {

  }

  protected void _simpleBinding() {
    BindingTemplate bindingTemplate = new BindingTemplate();
    bindingTemplate.setDefaultDescriptionString("SOAP Binding");
    bindingTemplate.setServiceKey(_businessService.getServiceKey());
    try {

      AuthToken _bindingToken = proxy.get_authToken("saveBindingPublisher", "password");
      Vector bindingVector = new Vector();
      bindingVector.addElement(bindingTemplate);
      proxy.save_binding(_bindingToken.getAuthInfoString(), bindingVector);
    }
    catch (TransportException ex) {
      fail(ex.toString());
    }
    catch (UDDIException ex) {
      fail(ex.toString());
    }
  }
}