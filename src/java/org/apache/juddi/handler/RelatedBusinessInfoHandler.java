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
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.SharedRelationships;
import org.apache.juddi.datatype.response.RelatedBusinessInfo;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class RelatedBusinessInfoHandler extends AbstractHandler
{
  public static final String TAG_NAME = "relatedBusinessInfo";

  private HandlerMaker maker = null;

  protected RelatedBusinessInfoHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    RelatedBusinessInfo obj = new RelatedBusinessInfo();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    // {none}

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,BusinessKeyHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(BusinessKeyHandler.TAG_NAME);
      obj.setBusinessKey((BusinessKey)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,NameHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(NameHandler.TAG_NAME);
      obj.addName((org.apache.juddi.datatype.Name)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,DescriptionHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      obj.addDescription((Description)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,SharedRelationshipsHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(SharedRelationshipsHandler.TAG_NAME);
      obj.setSharedRelationships((SharedRelationships)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  /**
   *
   */
  public void marshal(RegistryObject object,Element parent)
  {
    RelatedBusinessInfo info = (RelatedBusinessInfo)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    String businessKey = info.getBusinessKey();
    if (businessKey != null)
    {
      handler = maker.lookup(BusinessKeyHandler.TAG_NAME);
      handler.marshal(new BusinessKey(businessKey),element);
    }

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

    SharedRelationships relationships = info.getSharedRelationships();
    if (relationships != null)
    {
      handler = maker.lookup(SharedRelationshipsHandler.TAG_NAME);
      handler.marshal(relationships,element);
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
    AbstractHandler handler = maker.lookup(RelatedBusinessInfoHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    RelatedBusinessInfo info = new RelatedBusinessInfo();
    info.setBusinessKey("");
    info.setBusinessKey("6c0ac186-d36b-4b81-bd27-066a5fe0fc1f");
    info.addName(new Name("businessNm"));
    info.addName(new Name("businessNm2","en"));
    info.addDescription(new Description("business whatever"));
    info.addDescription(new Description("business whatever too","fr"));
    info.addKeyedReference(new KeyedReference("idBagKeyName","idBagkeyValue"));
    info.addKeyedReference(new KeyedReference("uuid:f78a135a-4769-4e79-8604-54d440314bc0","idBagKeyName2","idBagkeyValue2"));

    System.out.println();

    RegistryObject regObject = info;
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