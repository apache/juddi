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
import org.apache.juddi.datatype.response.AssertionStatusItem;
import org.apache.juddi.datatype.response.CompletionStatus;
import org.apache.juddi.datatype.response.KeysOwned;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class AssertionStatusItemHandler extends AbstractHandler
{
  public static final String TAG_NAME = "assertionStatusItem";
  private static final String FROM_KEY_TAG_NAME = "fromKey";
  private static final String TO_KEY_TAG_NAME = "toKey";

  private HandlerMaker maker = null;

  protected AssertionStatusItemHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    AssertionStatusItem obj = new AssertionStatusItem();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    obj.setCompletionStatus(element.getAttribute("completionStatus"));

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,FROM_KEY_TAG_NAME);
    if (nodeList.size() > 0)
    {
      Element keyElement = (Element)nodeList.elementAt(0);
      obj.setFromKey(XMLUtils.getText(keyElement));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,TO_KEY_TAG_NAME);
    if (nodeList.size() > 0)
    {
      Element keyElement = (Element)nodeList.elementAt(0);
      obj.setToKey(XMLUtils.getText(keyElement));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,KeyedReferenceHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(KeyedReferenceHandler.TAG_NAME);
      obj.setKeyedReference((KeyedReference)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,KeysOwnedHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(KeysOwnedHandler.TAG_NAME);
      obj.setKeysOwned((KeysOwned)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    AssertionStatusItem item = (AssertionStatusItem)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    CompletionStatus status = item.getCompletionStatus();
    if ((status != null) && (status.getValue() != null))
      element.setAttribute("completionStatus",status.getValue());

    String fromKey = item.getFromKey();
    if (fromKey != null)
    {
      Element keyElement = parent.getOwnerDocument().createElement(FROM_KEY_TAG_NAME);
      keyElement.appendChild(parent.getOwnerDocument().createTextNode(fromKey));
      element.appendChild(keyElement);
    }

    String toKey = item.getToKey();
    if (toKey != null)
    {
      Element keyElement = parent.getOwnerDocument().createElement(TO_KEY_TAG_NAME);
      keyElement.appendChild(parent.getOwnerDocument().createTextNode(toKey));
      element.appendChild(keyElement);
    }

    KeyedReference keyedRef = item.getKeyedReference();
    if (keyedRef != null)
    {
      handler = maker.lookup(KeyedReferenceHandler.TAG_NAME);
      handler.marshal(keyedRef,element);
    }

    KeysOwned keysOwned = item.getKeysOwned();
    if (keysOwned != null)
    {
      handler = maker.lookup(KeysOwnedHandler.TAG_NAME);
      handler.marshal(keysOwned,element);
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
    AbstractHandler handler = maker.lookup(AssertionStatusItemHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    KeysOwned keysOwned = new KeysOwned();
    keysOwned.setToKey("dfddb58c-4853-4a71-865c-f84509017cc7");
    keysOwned.setFromKey("fe8af00d-3a2c-4e05-b7ca-95a1259aad4f");

    AssertionStatusItem item = new AssertionStatusItem();
    item.setCompletionStatus(new CompletionStatus(CompletionStatus.COMPLETE));
    item.setFromKey("986d9a16-5d4d-46cf-9fac-6bb80a7091f6");
    item.setToKey("dd45a24c-74fc-4e82-80c2-f99cdc76d681");
    item.setKeyedReference(new KeyedReference("uuid:8ff45356-acde-4a4c-86bf-f953611d20c6","Subsidiary","1"));
    item.setKeysOwned(keysOwned);

    System.out.println();

    RegistryObject regObject = item;
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
