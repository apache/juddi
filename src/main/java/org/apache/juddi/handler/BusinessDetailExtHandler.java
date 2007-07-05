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
import org.apache.juddi.datatype.business.BusinessEntityExt;
import org.apache.juddi.datatype.response.BusinessDetailExt;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * BusinessDetailExtHandler
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class BusinessDetailExtHandler extends AbstractHandler
{
  public static final String TAG_NAME = "businessDetailExt";

  private HandlerMaker maker = null;

  protected BusinessDetailExtHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    BusinessDetailExt obj = new BusinessDetailExt();
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
    nodeList = XMLUtils.getChildElementsByTagName(element,BusinessEntityExtHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(BusinessEntityExtHandler.TAG_NAME);
      obj.addBusinessEntityExt((BusinessEntityExt)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    BusinessDetailExt detail = (BusinessDetailExt)object;
    String generic = detail.getGeneric();
    generic = getGeneric(generic);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);
    AbstractHandler handler = null;

    element.setAttribute("generic",generic);

    String operator = detail.getOperator();
    if (operator != null)
      element.setAttribute("operator",operator);
    else
      element.setAttribute("operator","");

    boolean truncated = detail.isTruncated();
    if (truncated)
      element.setAttribute("truncated","true");

    Vector vector = detail.getBusinessEntityExtVector();
    if ((vector!=null) && (vector.size() > 0))
    {
      handler = maker.lookup(BusinessEntityExtHandler.TAG_NAME);
      for (int i=0; i < vector.size(); i++)
        handler.marshal((BusinessEntityExt)vector.elementAt(i),element);
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