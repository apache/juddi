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

import org.apache.juddi.datatype.BusinessKey;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.RelatedBusinessInfo;
import org.apache.juddi.datatype.response.RelatedBusinessInfos;
import org.apache.juddi.datatype.response.RelatedBusinessesList;
import org.apache.juddi.registry.IRegistry;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class RelatedBusinessesListHandler extends AbstractHandler
{
  public static final String TAG_NAME = "relatedBusinessList";

  private HandlerMaker maker = null;

  protected RelatedBusinessesListHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    RelatedBusinessesList obj = new RelatedBusinessesList();
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
    nodeList = XMLUtils.getChildElementsByTagName(element,BusinessKeyHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(BusinessKeyHandler.TAG_NAME);
      obj.setBusinessKey((BusinessKey)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,RelatedBusinessInfosHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(RelatedBusinessInfosHandler.TAG_NAME);
      obj.setRelatedBusinessInfos((RelatedBusinessInfos)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    RelatedBusinessesList list = (RelatedBusinessesList)object;
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

    BusinessKey key = list.getBusinessKey();
    if (key != null)
    {
      handler = maker.lookup(BusinessKeyHandler.TAG_NAME);
      handler.marshal(key,element);
    }

    RelatedBusinessInfos infos = list.getRelatedBusinessInfos();
    if (infos != null)
    {
      handler = maker.lookup(RelatedBusinessInfosHandler.TAG_NAME);
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
    AbstractHandler handler = maker.lookup(RelatedBusinessesListHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    RelatedBusinessesList list = new RelatedBusinessesList();
    list.setGeneric("2.0");
    list.setOperator("jUDDI.org");
    list.setTruncated(true);
    list.setBusinessKey(new BusinessKey("f9f0c35f-06ab-4bec-9c7d-b1469e73f0eb"));
    list.addRelatedBusinessInfo(new RelatedBusinessInfo("abc"));
    list.addRelatedBusinessInfo(new RelatedBusinessInfo("xyz"));

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
  }
}