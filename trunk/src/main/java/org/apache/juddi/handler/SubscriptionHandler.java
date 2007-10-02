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

import java.util.Date;

import org.apache.juddi.datatype.BindingKey;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.subscription.Subscription;
import org.apache.juddi.datatype.subscription.SubscriptionFilter;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * SubscriptionHandler
 *
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class SubscriptionHandler extends AbstractHandler
{
  public static final String TAG_NAME = "subscription";

  protected SubscriptionHandler(HandlerMaker maker)
  {
  }

  public RegistryObject unmarshal(Element element)
  {
    // TODO (UDDI v3) Fill out SubscriptoinHandler.unmarshal()
      
    Subscription obj = new Subscription();

    // Attributes
    obj.setSubscriptionKey(element.getAttribute("subscriptionKey"));

    // Text Node Value
    // [none]

    // Child Elements

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    Subscription subscription = (Subscription)object;
    String generic = getGeneric(null);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);

    String subscriptionKey = subscription.getSubscriptionKey();
    if (subscriptionKey != null)
      element.setAttribute("subscriptionKey",subscriptionKey);

    // TODO (UDDI v3) Fill out SubscriptoinHandler.marshal()

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(SubscriptionHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    Subscription subscription = new Subscription();
    subscription.setSubscriptionKey("uuid:269855db-62eb-4862-8e5a-1b06f2753038");
    subscription.setBindingKey(new BindingKey(""));
    subscription.setBrief(true);
    subscription.setExpiresAfter(new Date());
    subscription.setMaxEntities(100);
    subscription.setNotificationInterval("");
    subscription.setSubscriptionFilter(new SubscriptionFilter());

    System.out.println();

    RegistryObject regObject = subscription;
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