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
import org.apache.juddi.datatype.publisher.PublisherID;
import org.apache.juddi.datatype.request.GetPublisherDetail;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class GetPublisherDetailHandler extends AbstractHandler
{
  public static final String TAG_NAME = "get_publisherDetail";

  private HandlerMaker maker = null;

  protected GetPublisherDetailHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    GetPublisherDetail obj = new GetPublisherDetail();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    String generic = element.getAttribute("generic");
    if ((generic != null && (generic.trim().length() > 0)))
      obj.setGeneric(generic);

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,PublisherIDHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(PublisherIDHandler.TAG_NAME);
      obj.addPublisherID((PublisherID)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    GetPublisherDetail request = (GetPublisherDetail)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    String generic = request.getGeneric();
    if (generic != null)
      element.setAttribute("generic",generic);

    Vector idVector = request.getPublisherIDVector();
    if ((idVector!=null) && (idVector.size() > 0))
    {
      handler = maker.lookup(PublisherIDHandler.TAG_NAME);
      for (int i=0; i<idVector.size(); i++)
        handler.marshal(new PublisherID((String)idVector.elementAt(i)),element);
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
    AbstractHandler handler = maker.lookup(GetPublisherDetailHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    GetPublisherDetail service = new GetPublisherDetail();
    service.addPublisherID("sviens");
    service.addPublisherID(new PublisherID("jdoe"));

    System.out.println();

    RegistryObject regObject = service;
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