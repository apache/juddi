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
package org.apache.juddi.handler;

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of Publisher objects.
 * Returns Publisher."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class PublisherHandler extends AbstractHandler
{
  public static final String TAG_NAME = "publisher";

  private HandlerMaker maker = null;

  protected PublisherHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    Publisher obj = new Publisher();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes (required)
    obj.setPublisherID(element.getAttribute("publisherID"));
    obj.setName(element.getAttribute("publisherName"));

    String admin = element.getAttribute("admin");
    if ((admin != null) && (admin.length() > 0))
      obj.setAdminValue(admin);
    else
      obj.setAdmin(false);

    String enabled = element.getAttribute("enabled");
    if ((enabled != null) && (enabled.length() > 0))
      obj.setEnabledValue(enabled);
    else
      obj.setAdmin(false);

    // Attributes (optional)
    String firstName = element.getAttribute("firstName");
    if ((firstName != null) && (firstName.length() > 0))
      obj.setFirstName(firstName);

    String lastName = element.getAttribute("lastName");
    if ((lastName != null) && (lastName.length() > 0))
      obj.setLastName(lastName);

    String middleInit = element.getAttribute("middleInit");
    if ((middleInit != null) && (middleInit.length() > 0))
      obj.setMiddleInit(middleInit);

    String workPhone = element.getAttribute("workPhone");
    if ((workPhone != null) && (workPhone.length() > 0))
      obj.setWorkPhone(workPhone);

    String mobilePhone = element.getAttribute("mobilePhone");
    if ((mobilePhone != null) && (mobilePhone.length() > 0))
      obj.setMobilePhone(mobilePhone);

    String pager = element.getAttribute("pager");
    if ((pager != null) && (pager.length() > 0))
      obj.setPager(pager);

    String emailAddress = element.getAttribute("emailAddress");
    if ((emailAddress != null) && (emailAddress.length() > 0))
      obj.setEmailAddress(emailAddress);

    // Text Node Value
    // {none}

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    Publisher publisher = (Publisher)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    // Attributes (required)
    String publisherID = publisher.getPublisherID();
    if ((publisherID != null) && (publisherID.length() > 0))
      element.setAttribute("publisherID",publisherID);
    else
      element.setAttribute("publisherID","");

    String publisherName = publisher.getName();
    if ((publisherName != null) && (publisherName.length() > 0))
      element.setAttribute("publisherName",publisherName);
    else
      element.setAttribute("publisherName","");

    element.setAttribute("admin",String.valueOf(publisher.isAdmin()));
    element.setAttribute("enabled",String.valueOf(publisher.isEnabled()));

    // Attributes (optional)
    String firstName = publisher.getFirstName();
    if ((firstName != null) && (firstName.length() > 0))
      element.setAttribute("firstName",firstName);

    String lastName = publisher.getLastName();
    if ((lastName != null) && (lastName.length() > 0))
      element.setAttribute("lastName",lastName);

    String middleInit = publisher.getMiddleInit();
    if ((middleInit != null) && (middleInit.length() > 0))
      element.setAttribute("middleInit",middleInit);

    String workPhone = publisher.getWorkPhone();
    if ((workPhone != null) && (workPhone.length() > 0))
      element.setAttribute("workPhone",workPhone);

    String mobilePhone = publisher.getMobilePhone();
    if ((mobilePhone != null) && (mobilePhone.length() > 0))
      element.setAttribute("mobilePhone",mobilePhone);

    String pager = publisher.getPager();
    if ((pager != null) && (pager.length() > 0))
      element.setAttribute("pager",pager);

    String emailAddress = publisher.getEmailAddress();
    if ((emailAddress != null) && (emailAddress.length() > 0))
      element.setAttribute("emailAddress",emailAddress);

    // Text Node Value
    // {none}

    // Child Elements
    // {none}

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(PublisherHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    Publisher publisher = new Publisher();
    publisher.setPublisherID("bcrosby");
    publisher.setName("Bing Crosby");
    publisher.setLastName("Crosby");
    publisher.setFirstName("Bing");
    publisher.setWorkPhone("978.123-4567");
    publisher.setMobilePhone("617-765-9876");
    publisher.setPager("800-123-4655 ID: 501");
    publisher.setEmailAddress("bcrosby@juddi.org");
    publisher.setAdmin(true);
    publisher.setEnabled(true);

    System.out.println();

    RegistryObject regObject = publisher;
    handler.marshal(regObject,parent);
    child = (Element)parent.getFirstChild();
    parent.removeChild(child);
    XMLUtils.writeXML(child,System.out);

    System.out.println();

    regObject = handler.unmarshal(child);
    handler.marshal(regObject,parent);
    child = (Element)parent.getFirstChild();
    parent.removeChild(child);
    XMLUtils.writeXML(child,System.out);
  }
}