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
import org.apache.juddi.datatype.response.ServiceInfos;
import org.apache.juddi.datatype.response.ServiceList;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of ServiceList objects.
 * Returns ServiceList."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class ServiceListHandler extends AbstractHandler
{
  public static final String TAG_NAME = "serviceList";

  private HandlerMaker maker = null;

  protected ServiceListHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    ServiceList obj = new ServiceList();
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
    nodeList = XMLUtils.getChildElementsByTagName(element,ServiceInfosHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(ServiceInfosHandler.TAG_NAME);
      obj.setServiceInfos((ServiceInfos)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    ServiceList list = (ServiceList)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    String generic = list.getGeneric();
    if (generic != null)
    {
      element.setAttribute("generic",generic);

      if (generic.equals(IRegistry.UDDI_V1_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V1_NAMESPACE);
      else if (generic.equals(IRegistry.UDDI_V2_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V2_NAMESPACE);
      else if (generic.equals(IRegistry.UDDI_V3_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V3_NAMESPACE);
    }

    String operator = list.getOperator();
    if (operator != null)
      element.setAttribute("operator",operator);

    boolean truncated = list.isTruncated();
    if (truncated)
      element.setAttribute("truncated","true");

    ServiceInfos infos = list.getServiceInfos();
    if (infos != null)
    {
      handler = maker.lookup(ServiceInfosHandler.TAG_NAME);
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
  }
}