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

import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class PublisherAssertionHandler extends AbstractHandler
{
  public static final String TAG_NAME = "publisherAssertion";

  private HandlerMaker maker = null;

  protected PublisherAssertionHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    PublisherAssertion obj = new PublisherAssertion();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,"fromKey");
    if (nodeList.size() > 0)
    {
      String fromKey = XMLUtils.getText((Element)nodeList.elementAt(0));
      obj.setFromKey(fromKey);
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,"toKey");
    if (nodeList.size() > 0)
    {
      String toKey = XMLUtils.getText((Element)nodeList.elementAt(0));
      obj.setToKey(toKey);
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,KeyedReferenceHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(KeyedReferenceHandler.TAG_NAME);
      obj.setKeyedReference((KeyedReference)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    PublisherAssertion assertion = (PublisherAssertion)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    String fromKey = assertion.getFromKey();
    if (fromKey != null)
    {
      Element fkElement = parent.getOwnerDocument().createElement("fromKey");
      fkElement.appendChild(parent.getOwnerDocument().createTextNode(fromKey));
      element.appendChild(fkElement);
    }

    String toKey = assertion.getToKey();
    if (toKey != null)
    {
      Element tkElement = parent.getOwnerDocument().createElement("toKey");
      tkElement.appendChild(parent.getOwnerDocument().createTextNode(toKey));
      element.appendChild(tkElement);
    }

    KeyedReference keyedRef = assertion.getKeyedReference();
    if (keyedRef != null)
    {
      handler = maker.lookup(KeyedReferenceHandler.TAG_NAME);
      handler.marshal(keyedRef,element);
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
    AbstractHandler handler = maker.lookup(PublisherAssertionHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    PublisherAssertion assertion = new PublisherAssertion();
    assertion.setFromKey("b2f072e7-6013-4385-93b4-9c1c2ece1c8f");
    assertion.setToKey("115be72d-0c04-4b5f-a729-79a522629c19");
    assertion.setKeyedReference(new KeyedReference("uuid:8ff45356-acde-4a4c-86bf-f953611d20c6","catBagKeyName2","catBagKeyValue2"));

    System.out.println();

    RegistryObject regObject = assertion;
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