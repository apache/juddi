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
package org.apache.juddi.handler;

import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.datatype.request.AddPublisherAssertions;
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class AddPublisherAssertionsHandler extends AbstractHandler
{
  public static final String TAG_NAME = "add_publisherAssertions";

  private HandlerMaker maker = null;

  protected AddPublisherAssertionsHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    AddPublisherAssertions obj = new AddPublisherAssertions();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    String generic = element.getAttribute("generic");
    if ((generic != null && (generic.trim().length() > 0)))
      obj.setGeneric(generic);

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,AuthInfoHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(AuthInfoHandler.TAG_NAME);
      obj.setAuthInfo((AuthInfo)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,PublisherAssertionHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(PublisherAssertionHandler.TAG_NAME);
      obj.addPublisherAssertion((PublisherAssertion)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    AddPublisherAssertions request = (AddPublisherAssertions)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);

    String generic = request.getGeneric();
    if (generic != null)
      element.setAttribute("generic",generic);

    AuthInfo authInfo = request.getAuthInfo();
    if (authInfo != null)
    {
      AbstractHandler handler = maker.lookup(AuthInfoHandler.TAG_NAME);
      handler.marshal(authInfo,element);
    }

    Vector vector = request.getPublisherAssertionVector();
    if ((vector != null) && (vector.size() > 0))
    {
      AbstractHandler handler = maker.lookup(PublisherAssertionHandler.TAG_NAME);
      for (int i=0; i<vector.size(); i++)
        handler.marshal(((PublisherAssertion)vector.elementAt(i)),element);
    }

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(AddPublisherAssertionsHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    AuthInfo authInfo = new AuthInfo();
    authInfo.setValue("6f157513-844e-4a95-a856-d257e6ba9726");

    PublisherAssertion assertion = new PublisherAssertion();
    assertion.setFromKey("3379ec11-a509-4668-9fee-19b134d0d09b");
    assertion.setToKey("3379ec11-a509-4668-9fee-19b134d0d09b");
    assertion.setKeyName("paKeyName");
    assertion.setKeyValue("paKeyValue");
    assertion.setTModelKey("uuid:3379ec11-a509-4668-9fee-19b134d0d09b");

    AddPublisherAssertions service = new AddPublisherAssertions();
    service.setAuthInfo(authInfo);
    service.addPublisherAssertion(assertion);
    service.addPublisherAssertion(assertion);

    System.out.println();

    RegistryObject regObject = service;
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