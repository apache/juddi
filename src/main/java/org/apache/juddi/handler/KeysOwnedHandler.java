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

//import javax.xml.soap.SOAPElement;
//import javax.xml.soap.SOAPException;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.KeysOwned;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class KeysOwnedHandler extends AbstractHandler
{
  public static final String TAG_NAME = "keysOwned";
  private static final String FROM_KEY_TAG_NAME = "fromKey";
  private static final String TO_KEY_TAG_NAME = "toKey";

  protected KeysOwnedHandler(HandlerMaker maker)
  {
  }

  public RegistryObject unmarshal(Element element)
  {
    KeysOwned obj = new KeysOwned();
    Vector nodeList = null;

    // Attributes
    // {none}

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

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    KeysOwned keysOwned = (KeysOwned)object;
    String generic = getGeneric(null);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);

    String fromKey = keysOwned.getFromKey();
    if ((fromKey != null) && (fromKey.length() > 0))
    {
      Element keyElement = parent.getOwnerDocument().createElement(FROM_KEY_TAG_NAME);
      keyElement.appendChild(parent.getOwnerDocument().createTextNode(fromKey));
      element.appendChild(keyElement);
    }

    String toKey = keysOwned.getToKey();
    if ((toKey != null) && (toKey.length() > 0))
    {
      Element keyElement = parent.getOwnerDocument().createElement(TO_KEY_TAG_NAME);
      keyElement.appendChild(parent.getOwnerDocument().createTextNode(toKey));
      element.appendChild(keyElement);
    }

    parent.appendChild(element);
  }

/*  
  public RegistryObject unmarshal(SOAPElement element)
  {
    return null;
  }

  public void marshal(RegistryObject object,SOAPElement parent)
    throws SOAPException
  {
  }
*/

  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
  }
}
