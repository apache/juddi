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
import org.apache.juddi.datatype.response.Property;
import org.w3c.dom.Element;

/**
 * Knows about the creation and populating of Property objects.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class PropertyHandler extends AbstractHandler
{
  public static final String TAG_NAME = "property";

  private HandlerMaker maker = null;

  protected PropertyHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    Property obj = new Property();

    // Attributes
    String name = element.getAttribute("name");
    if ((name != null) && (name.trim().length() > 0))
      obj.setName(name);

    String value = element.getAttribute("value");
    if ((value != null) && (value.trim().length() > 0))
      obj.setValue(value);

    // Text Node Value
    // {none}

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
  	Property property = (Property)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);

    String name = property.getName();
    if ((name != null) && (name.trim().length() > 0))
      element.setAttribute("name",name);

    String value = property.getValue();
    if ((value != null) && (value.trim().length() > 0))
      element.setAttribute("value",value);

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    // create the source object
    Property inprop = new Property("nameAttr","valueAttr");

    // unmarshal & marshal (object->xml->object)
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(PropertyHandler.TAG_NAME);
    Element element = null;
    handler.marshal(inprop,element);
    AddressLine outprop = (AddressLine)handler.unmarshal(element);

    // compare unmarshaled with marshaled obj
    if (outprop.equals(inprop))
      System.out.println("Input and output are the same.");
    else
      System.out.println("Input and output are NOT the same!");
  }
}