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
import org.apache.juddi.datatype.business.BusinessEntity;
import org.apache.juddi.datatype.request.ValidateValues;
import org.apache.juddi.datatype.service.BusinessService;
import org.apache.juddi.datatype.tmodel.TModel;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class ValidateValuesHandler extends AbstractHandler
{
  public static final String TAG_NAME = "validate_values";

  private HandlerMaker maker = null;

  protected ValidateValuesHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    ValidateValues obj = new ValidateValues();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    String generic = element.getAttribute("generic");
    if ((generic != null && (generic.trim().length() > 0)))
      obj.setGeneric(generic);

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,BusinessEntityHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(BusinessEntityHandler.TAG_NAME);
      obj.addBusinessEntity((BusinessEntity)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,BusinessServiceHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(BusinessServiceHandler.TAG_NAME);
      obj.addBusinessService((BusinessService)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,TModelHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(TModelHandler.TAG_NAME);
      obj.addTModel((TModel)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    ValidateValues request = (ValidateValues)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    String generic = request.getGeneric();
    if (generic != null)
      element.setAttribute("generic",generic);

    Vector businessVector = request.getBusinessEntityVector();
    if ((businessVector!=null) && (businessVector.size() > 0))
    {
      handler = maker.lookup(BusinessEntityHandler.TAG_NAME);
      for (int i=0; i < businessVector.size(); i++)
        handler.marshal((BusinessEntity)businessVector.elementAt(i),element);
    }

    Vector serviceVector = request.getBusinessServiceVector();
    if ((serviceVector!=null) && (serviceVector.size() > 0))
    {
      handler = maker.lookup(BusinessServiceHandler.TAG_NAME);
      for (int i=0; i < serviceVector.size(); i++)
        handler.marshal((BusinessService)serviceVector.elementAt(i),element);
    }

    Vector tModelVector = request.getTModelVector();
    if ((tModelVector!=null) && (tModelVector.size() > 0))
    {
      handler = maker.lookup(TModelHandler.TAG_NAME);
      for (int i=0; i < tModelVector.size(); i++)
        handler.marshal((TModel)tModelVector.elementAt(i),element);
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
  AbstractHandler handler = maker.lookup(ValidateValuesHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    BusinessEntity business = new BusinessEntity();
    BusinessService service = new BusinessService();
    TModel tModel = new TModel();

    ValidateValues request = new ValidateValues();
    request.addBusinessEntity(business);
    request.addBusinessService(service);
    request.addTModel(tModel);

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

    System.out.println();
  }
}