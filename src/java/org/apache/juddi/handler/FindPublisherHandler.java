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

import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.request.FindPublisher;
import org.apache.juddi.datatype.request.FindQualifier;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * Knows about the creation and populating of FindPublisher objects.
 * Returns FindPublisher.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class FindPublisherHandler extends AbstractHandler
{
  public static final String TAG_NAME = "find_publisher";

  private HandlerMaker maker = null;

  protected FindPublisherHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    FindPublisher obj = new FindPublisher();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    String generic = element.getAttribute("generic");
    if ((generic != null && (generic.trim().length() > 0)))
      obj.setGeneric(generic);

    String maxRows = element.getAttribute("maxRows");
    if ((maxRows != null) && (maxRows.length() > 0))
      obj.setMaxRows(maxRows);

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,NameHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(NameHandler.TAG_NAME);
      Name name = (Name )handler.unmarshal((Element)nodeList.elementAt(0));
      if (name != null)
        obj.setName(name);    
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,FindQualifiersHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(FindQualifiersHandler.TAG_NAME);
      obj.setFindQualifiers((FindQualifiers)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    FindPublisher request = (FindPublisher)object;
    String generic = request.getGeneric();
    generic = getGeneric(generic);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);
    AbstractHandler handler = null;

    element.setAttribute("generic",generic);

    int maxRows = request.getMaxRows();
    if (maxRows > 0)
      element.setAttribute("maxRows",String.valueOf(maxRows));

    FindQualifiers qualifiers = request.getFindQualifiers();
    if ((qualifiers != null) && (qualifiers.size() > 0))
    {
      handler = maker.lookup(FindQualifiersHandler.TAG_NAME);
      handler.marshal(qualifiers,element);
    }

    Name name = request.getName();
    if (name != null)
    {
      handler = maker.lookup(NameHandler.TAG_NAME);
      handler.marshal(name,element);
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
    AbstractHandler handler = maker.lookup(FindPublisherHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    FindPublisher request = new FindPublisher();
    request.setName(new Name("s","en"));
    request.addFindQualifier(new FindQualifier(FindQualifier.SORT_BY_NAME_ASC));
    request.setMaxRows(50);

    System.out.println();

    RegistryObject regObject = request;
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