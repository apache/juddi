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
import org.apache.juddi.datatype.request.GetSubscriptions;
import org.apache.juddi.datatype.subscription.SubscriptionKey;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of GetSubscriptions objects.
 *  Returns GetSubscriptions."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class GetSubscriptionsHandler extends AbstractHandler
{
  public static final String TAG_NAME = "get_subscriptions";

  private HandlerMaker maker = null;

  protected GetSubscriptionsHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    GetSubscriptions obj = new GetSubscriptions();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    String generic = element.getAttribute("generic");
    if ((generic != null && (generic.trim().length() > 0)))
      obj.setGeneric(generic);

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,SubscriptionKeyHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(SubscriptionKeyHandler.TAG_NAME);
      obj.addSubscriptionKey((SubscriptionKey)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    GetSubscriptions request = (GetSubscriptions)object;
    String generic = request.getGeneric();
    generic = getGeneric(generic);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);
    AbstractHandler handler = null;

    element.setAttribute("generic",generic);

    Vector keyVector = request.getSubscriptionKeyVector();
    if ((keyVector!=null) && (keyVector.size() > 0))
    {
      handler = maker.lookup(SubscriptionKeyHandler.TAG_NAME);
      for (int i=0; i<keyVector.size(); i++)
        handler.marshal(new SubscriptionKey((String)keyVector.elementAt(i)),element);
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
    AbstractHandler handler = maker.lookup(GetSubscriptionsHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    GetSubscriptions service = new GetSubscriptions();
    service.addSubscriptionKey("1bd50f65-9671-41ae-8d13-b3b5a5afcda0");
    service.addSubscriptionKey(new SubscriptionKey("1fbe67e6-f8b5-4743-a23f-9c13e4273d9f"));

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