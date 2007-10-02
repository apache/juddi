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

import org.apache.juddi.datatype.CategoryBag;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.TModelKey;
import org.apache.juddi.datatype.request.FindBinding;
import org.apache.juddi.datatype.request.FindQualifier;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;


/**
 * FindBindingHandler
 *
 * "Knows about the creation and populating of FindBinding objects.
 * Returns FindBinding."
 *
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class FindBindingHandler extends AbstractHandler
{
  public static final String TAG_NAME = "find_binding";

  private HandlerMaker maker = null;

  protected FindBindingHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    FindBinding obj = new FindBinding();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    String serviceKey = element.getAttribute("serviceKey");
    if ((serviceKey != null && (serviceKey.trim().length() > 0)))
      obj.setServiceKey(serviceKey);

    String generic = element.getAttribute("generic");
    if ((generic != null && (generic.trim().length() > 0)))
      obj.setGeneric(generic);

    String maxRows = element.getAttribute("maxRows");
    if ((maxRows != null) && (maxRows.trim().length() > 0))
      obj.setMaxRows(maxRows);

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,FindQualifiersHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(FindQualifiersHandler.TAG_NAME);
      obj.setFindQualifiers((FindQualifiers)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,TModelBagHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(TModelBagHandler.TAG_NAME);
      obj.setTModelBag((TModelBag)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,CategoryBagHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(CategoryBagHandler.TAG_NAME);
      obj.setCategoryBag((CategoryBag)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    FindBinding request = (FindBinding)object;
    String generic = request.getGeneric();
    generic = getGeneric(generic);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);
    AbstractHandler handler = null;

    String key = request.getServiceKey();
    if (key != null)
      element.setAttribute("serviceKey",key);

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

    CategoryBag catBag = request.getCategoryBag();
    if (catBag != null)
    {
      handler = maker.lookup(CategoryBagHandler.TAG_NAME);
      handler.marshal(catBag,element);
    }

    TModelBag bag = request.getTModelBag();
    if (bag != null)
    {
      handler = maker.lookup(TModelBagHandler.TAG_NAME);
      handler.marshal(bag,element);
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
    AbstractHandler handler = maker.lookup(FindBindingHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    CategoryBag catBag = new CategoryBag();
    catBag.addKeyedReference(new KeyedReference("catBagKeyName","catBagKeyValue"));
    catBag.addKeyedReference(new KeyedReference("uuid:8ff45356-acde-4a4c-86bf-f953611d20c6","catBagKeyName2","catBagKeyValue2"));

    TModelBag bag = new TModelBag();
    bag.addTModelKey("uuid:35d9793b-9911-4b85-9f0e-5d4c65b4f253");
    bag.addTModelKey(new TModelKey("uuid:c5ab113f-0d6b-4247-b3c4-8c012726acd8"));

    FindBinding request = new FindBinding();
    request.addFindQualifier(new FindQualifier(FindQualifier.SORT_BY_DATE_ASC));
    request.addFindQualifier(new FindQualifier(FindQualifier.AND_ALL_KEYS));
    request.setMaxRows(50);
    request.setServiceKey("fd36dbce-bc3e-468b-8346-5374975a0843");
    request.setTModelBag(bag);
    request.setCategoryBag(catBag);

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