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

import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;


/**
 * "Knows about the creation and populating of Name objects.
 * Returns Name."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class NameHandler extends AbstractHandler
{
  public static final String TAG_NAME = "name";

  private HandlerMaker maker = null;

  protected NameHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {   
    // Attributes
    String langCode = element.getAttribute("xml:lang");

    // Text Node Value
    String nameValue = XMLUtils.getText(element);

    // Child Elements
    // {none}

    // Only create Name instance if nameValue not null and not zero-length
    Name obj = null;
    if ((nameValue != null) && (nameValue.trim().length() > 0)) 
      obj = new Name(nameValue,langCode);
    
    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    Name name = (Name)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);

    String langCode = name.getLanguageCode();
    if ((langCode != null) && (langCode.trim().length() > 0))
      element.setAttribute("xml:lang",langCode);

    String nameValue = name.getValue();
    if (nameValue != null)
      element.appendChild(parent.getOwnerDocument().createTextNode(nameValue));

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
  }
}