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
import org.apache.juddi.datatype.binding.AccessPoint;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of AccessPoint objects.
 * Returns AccessPoint."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class AccessPointHandler extends AbstractHandler
{
  public static final String TAG_NAME = "accessPoint";

  private HandlerMaker maker = null;

  protected AccessPointHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    AccessPoint obj = new AccessPoint();

    // Attributes
    obj.setURLType(element.getAttribute("URLType"));

    // Text Node Value
    obj.setURL(XMLUtils.getText(element));

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    AccessPoint accessPoint = (AccessPoint)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);

    // Attributes
    String urlType = accessPoint.getURLType();
    if (urlType != null)
      element.setAttribute("URLType",urlType);

    // Text Node Value
    String urlValue = accessPoint.getURL();
    if (urlValue != null)
      element.appendChild(parent.getOwnerDocument().createTextNode(urlValue));

    // Child Elements
    // {none}

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AccessPointHandler handler = new AccessPointHandler(maker);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    AccessPoint object = new AccessPoint();
    object.setURLType(AccessPoint.HTTP);
    object.setURL("http://www.sviens.com/service.cgi");

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