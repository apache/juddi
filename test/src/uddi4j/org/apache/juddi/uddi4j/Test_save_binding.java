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