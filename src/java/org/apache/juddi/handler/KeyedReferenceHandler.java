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

import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of KeyedReference objects.
 * Returns KeyedReference."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class KeyedReferenceHandler extends AbstractHandler
{
  public static final String TAG_NAME = "keyedReference";

  private HandlerMaker maker = null;

  protected KeyedReferenceHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    KeyedReference obj = new KeyedReference();

    // Attributes
    obj.setTModelKey(element.getAttribute("tModelKey"));
    obj.setKeyName(element.getAttribute("keyName"));
    obj.setKeyValue(element.getAttribute("keyValue"));

    // Text Node Value
    // {none}

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    KeyedReference keyedRef = (KeyedReference)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);

    String tModelKey = keyedRef.getTModelKey();
    if (tModelKey != null)
      element.setAttribute("tModelKey",tModelKey);

    String keyName = keyedRef.getKeyName();
    if (keyName != null)
      element.setAttribute("keyName",keyName);

    String keyValue = keyedRef.getKeyValue();
    if (keyValue != null)
      element.setAttribute("keyValue",keyValue);

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
  AbstractHandler handler = maker.lookup(KeyedReferenceHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    KeyedReference keyedRef = new KeyedReference();
    keyedRef.setTModelKey("uuid:3860b975-9e0c-4cec-bad6-87dfe00e3864");
    keyedRef.setKeyName("idBagKeyName2");
    keyedRef.setKeyValue("idBagKeyValue2");

    System.out.println();

    RegistryObject regObject = keyedRef;
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