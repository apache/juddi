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

import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of AddressLine objects.
 * Returns AddressLine."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class AddressLineHandler extends AbstractHandler
{
  public static final String TAG_NAME = "addressLine";

  protected AddressLineHandler(HandlerMaker maker)
  {
  }

  public RegistryObject unmarshal(Element element)
  {
    AddressLine obj = new AddressLine();

    // Attributes
    String keyName = element.getAttribute("keyName");
    if ((keyName != null) && (keyName.trim().length() > 0))
      obj.setKeyName(keyName);

    String keyValue = element.getAttribute("keyValue");
    if ((keyValue != null) && (keyValue.trim().length() > 0))
      obj.setKeyValue(keyValue);

    // Text Node Value
    obj.setLineValue(XMLUtils.getText(element));

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    AddressLine line = (AddressLine)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);

    String keyName = line.getKeyName();
    if ((keyName != null) && (keyName.trim().length() > 0))
      element.setAttribute("keyName",keyName);

    String keyValue = line.getKeyValue();
    if ((keyValue != null) && (keyValue.trim().length() > 0))
      element.setAttribute("keyValue",keyValue);

    String lineValue = line.getLineValue();
    if (lineValue != null)
      element.appendChild(parent.getOwnerDocument().createTextNode(lineValue));

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
      HandlerMaker maker = HandlerMaker.getInstance();
      AbstractHandler handler = maker.lookup(AddressLineHandler.TAG_NAME);
      Element parent = XMLUtils.newRootElement();
      Element child = null;

      AddressLine object = new AddressLine("AddressLine1","keyNameAttr","keyValueAttr");

      System.out.println();

      RegistryObject regObject = object;
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

      System.out.println();
  }
}