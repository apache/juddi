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

import org.apache.juddi.datatype.Address;
import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of Address objects.
 * Returns Address."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class AddressHandler extends AbstractHandler
{
  public static final String TAG_NAME = "address";

  private HandlerMaker maker = null;

  protected AddressHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    Address obj = new Address();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    String useType = element.getAttribute("useType");
    if ((useType != null) && (useType.trim().length() > 0))
      obj.setUseType(useType);

    String sortCode = element.getAttribute("sortCode");
    if ((sortCode != null) && (sortCode.trim().length() > 0))
      obj.setSortCode(sortCode);

    String tModelKey = element.getAttribute("tModelKey");
    if ((tModelKey != null) && (tModelKey.trim().length() > 0))
      obj.setTModelKey(tModelKey);

    // Text Node Value
    // {none}

    // Child Elements
    handler = maker.lookup(AddressLineHandler.TAG_NAME);
    nodeList = XMLUtils.getChildElementsByTagName(element,AddressLineHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      obj.addAddressLine((AddressLine)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    Address address = (Address)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);

    String useType = address.getUseType();
    if ((useType != null) && (useType.trim().length() > 0))
      element.setAttribute("useType",useType);

    String sortCode = address.getSortCode();
    if ((sortCode != null) && (sortCode.trim().length() > 0))
      element.setAttribute("sortCode",sortCode);

    String tModelKey = address.getTModelKey();
    if ((tModelKey != null) && (tModelKey.trim().length() > 0))
      element.setAttribute("tModelKey",tModelKey);

    Vector vector = address.getAddressLineVector();
    if ((vector!=null) && (vector.size() > 0))
    {
      AbstractHandler handler = maker.lookup(AddressLineHandler.TAG_NAME);
      for (int i=0; i < vector.size(); i++)
      {
        AddressLine addressLine = (AddressLine)vector.elementAt(i);
        handler.marshal(addressLine,element);
      }
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
    AbstractHandler handler = maker.lookup(AddressHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    Address object = new Address();
    object.setUseType("myAddressUseType");
    object.setSortCode("sortThis");
    object.setTModelKey(null);
    object.addAddressLine(new AddressLine("AddressLine1","keyNameAttr","keyValueAttr"));
    object.addAddressLine(new AddressLine("AddressLine2"));

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