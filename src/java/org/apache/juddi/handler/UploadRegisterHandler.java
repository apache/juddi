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

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.UploadRegister;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of Name objects.
 * Returns Name."
 *
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class UploadRegisterHandler extends AbstractHandler
{
  public static final String TAG_NAME = "uploadRegister";

  protected UploadRegisterHandler(HandlerMaker maker)
  {
  }

  public RegistryObject unmarshal(Element element)
  {
    UploadRegister obj = new UploadRegister();

    // Text Node Value
    obj.setValue(XMLUtils.getText(element));

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    UploadRegister uploadRegister = (UploadRegister)object;
    String generic = getGeneric(null);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);

    String uploadRegisterValue = uploadRegister.getValue();
    if (uploadRegisterValue != null)
      element.appendChild(parent.getOwnerDocument().createTextNode(uploadRegisterValue));

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    UploadRegisterHandler handler = new UploadRegisterHandler(maker);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    UploadRegister object = new UploadRegister();
    object.setValue("http://www.juddi.org/businessEntity.xml");

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
