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
import org.apache.juddi.datatype.request.FindQualifier;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;


/**
 * FindQualifierHandler
 *
 * "Knows about the creation and populating of FindQualifier objects.
 * Returns FindQualifier."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class FindQualifierHandler extends AbstractHandler
{
  public static final String TAG_NAME = "findQualifier";

  private HandlerMaker maker = null;

  protected FindQualifierHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    FindQualifier obj = new FindQualifier();

    // Attributes
    // {none}

    // Text Node Value
    obj.setValue(XMLUtils.getText(element));

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    FindQualifier qualifier = (FindQualifier)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);

    String qValue = qualifier.getValue();
    if (qValue != null)
      element.appendChild(parent.getOwnerDocument().createTextNode(qValue));

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(FindQualifierHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    FindQualifier qualifier = new FindQualifier(FindQualifier.SORT_BY_NAME_ASC);

    System.out.println();

    RegistryObject regObject = qualifier;
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