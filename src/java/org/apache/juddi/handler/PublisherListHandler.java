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

import org.apache.juddi.IRegistry;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.PublisherInfo;
import org.apache.juddi.datatype.response.PublisherInfos;
import org.apache.juddi.datatype.response.PublisherList;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of PublisherList objects.
 * Returns PublisherList."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class PublisherListHandler extends AbstractHandler
{
  public static final String TAG_NAME = "publisherList";

  private HandlerMaker maker = null;

  protected PublisherListHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    PublisherList obj = new PublisherList();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // We could use the generic attribute value to
    // determine which version of UDDI was used to
    // format the request XML. - Steve

    // Attributes
    obj.setGeneric(element.getAttribute("generic"));
    obj.setOperator(element.getAttribute("operator"));

    // We can ignore the xmlns attribute since we
    // can always determine it's value using the
    // "generic" attribute. - Steve

    String truncValue = element.getAttribute("truncated");
    if (truncValue != null)
      obj.setTruncated(truncValue.equalsIgnoreCase("true"));

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,PublisherInfosHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(PublisherInfosHandler.TAG_NAME);
      obj.setPublisherInfos((PublisherInfos)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    PublisherList list = (PublisherList)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    String generic = list.getGeneric();
    if ((generic != null) && (generic.trim().length() > 0))
    {
      element.setAttribute("generic",generic);
      element.setAttribute("xmlns",IRegistry.JUDDI_V1_NAMESPACE);
    }
    else // Default to UDDI v2 and jUDDI v1 values
    {
      element.setAttribute("generic",IRegistry.UDDI_V2_GENERIC);
      element.setAttribute("xmlns",IRegistry.JUDDI_V1_NAMESPACE);
    }

    String operator = list.getOperator();
    if (operator != null)
      element.setAttribute("operator",operator);
    else
      element.setAttribute("operator","");

    boolean truncated = list.isTruncated();
    if (truncated)
      element.setAttribute("truncated","true");

    PublisherInfos infos = list.getPublisherInfos();
    if (infos != null)
    {
      handler = maker.lookup(PublisherInfosHandler.TAG_NAME);
      handler.marshal(infos,element);
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
    AbstractHandler handler = maker.lookup(PublisherListHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    PublisherList list = new PublisherList();
    list.setGeneric("1.0");
    list.setOperator("jUDDI.org");
    list.setTruncated(false);
    list.addPublisherInfo(new PublisherInfo("sviens","Steve Viens"));
    list.addPublisherInfo(new PublisherInfo("jdoe","John Doe"));

    System.out.println();

    RegistryObject regObject = list;
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