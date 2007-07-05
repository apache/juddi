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

import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.BusinessInfo;
import org.apache.juddi.datatype.response.ServiceInfos;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * BusinessInfoHandler
 *
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class BusinessInfoHandler extends AbstractHandler
{
  public static final String TAG_NAME = "businessInfo";

  private HandlerMaker maker = null;

  protected BusinessInfoHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    BusinessInfo obj = new BusinessInfo();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    obj.setBusinessKey(element.getAttribute("businessKey"));

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,NameHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(NameHandler.TAG_NAME);
      Name name = (Name )handler.unmarshal((Element)nodeList.elementAt(i));
      if (name != null)
        obj.addName(name);
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,DescriptionHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      Description descr = (Description)handler.unmarshal((Element)nodeList.elementAt(i));
      if (descr != null)
        obj.addDescription(descr);
    }

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
    BusinessInfo info = (BusinessInfo)object;
    String generic = getGeneric(null);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);
    AbstractHandler handler = null;

    String businessKey = info.getBusinessKey();
    if (businessKey != null)
      element.setAttribute("businessKey",businessKey);

    Vector nameVector = info.getNameVector();
    if ((nameVector!=null) && (nameVector.size() > 0))
    {
      handler = maker.lookup(NameHandler.TAG_NAME);
      for (int i=0; i < nameVector.size(); i++)
        handler.marshal((org.apache.juddi.datatype.Name)nameVector.elementAt(i),element);
    }

    Vector descVector = info.getDescriptionVector();
    if ((descVector!=null) && (descVector.size() > 0))
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      for (int i=0; i < descVector.size(); i++)
        handler.marshal((Description)descVector.elementAt(i),element);
    }

    ServiceInfos infos = info.getServiceInfos();
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