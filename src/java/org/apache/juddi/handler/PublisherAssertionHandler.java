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
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
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