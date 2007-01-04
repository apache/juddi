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

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.request.FindQualifier;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of FindQualifiers objects.
 * Returns FindQualifiers."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class FindQualifiersHandler extends AbstractHandler
{
  public static final String TAG_NAME = "findQualifiers";

  private HandlerMaker maker = null;

  protected FindQualifiersHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    FindQualifiers obj = new FindQualifiers();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    // {none}

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,FindQualifierHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(FindQualifierHandler.TAG_NAME);
      obj.addFindQualifier((FindQualifier)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    FindQualifiers qualifiers = (FindQualifiers)object;
    String generic = getGeneric(null);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);
    AbstractHandler handler = null;

    Vector qVector = qualifiers.getFindQualifierVector();
    if ((qVector!=null) && (qVector.size() > 0))
    {
      handler = maker.lookup(FindQualifierHandler.TAG_NAME);
      for (int i=0; i < qVector.size(); i++)
        handler.marshal((FindQualifier)qVector.elementAt(i),element);
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
    AbstractHandler handler = maker.lookup(FindQualifiersHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    FindQualifiers qualifiers = new FindQualifiers();
    qualifiers.addFindQualifier(new FindQualifier(FindQualifier.SORT_BY_DATE_ASC));
    qualifiers.addFindQualifier(new FindQualifier(FindQualifier.AND_ALL_KEYS));

    System.out.println();

    RegistryObject regObject = qualifiers;
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