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
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.request.FindQualifier;
import org.apache.juddi.datatype.request.FindQualifiers;
import org.apache.juddi.datatype.request.FindRelatedBusinesses;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class FindRelatedBusinessesHandler extends AbstractHandler
{
  public static final String TAG_NAME = "find_relatedBusinesses";

  private HandlerMaker maker = null;

  protected FindRelatedBusinessesHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    FindRelatedBusinesses obj = new FindRelatedBusinesses();
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
    nodeList = XMLUtils.getChildElementsByTagName(element,BusinessKeyHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(BusinessKeyHandler.TAG_NAME);
      obj.setBusinessKey((BusinessKey)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,FindQualifiersHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(FindQualifiersHandler.TAG_NAME);
      obj.setFindQualifiers((FindQualifiers)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,KeyedReferenceHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(KeyedReferenceHandler.TAG_NAME);
      obj.setKeyedReference((KeyedReference)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    FindRelatedBusinesses request = (FindRelatedBusinesses)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    String generic = request.getGeneric();
    if (generic != null)
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

  BusinessKey businessKey = new BusinessKey(request.getBusinessKey());
  if (businessKey != null)
  {
    handler = maker.lookup(BusinessKeyHandler.TAG_NAME);
    handler.marshal(businessKey,element);
  }

    KeyedReference keyedRef = request.getKeyedReference();
    if (keyedRef != null)
    {
      handler = maker.lookup(KeyedReferenceHandler.TAG_NAME);
      handler.marshal(keyedRef,element);
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
    AbstractHandler handler = maker.lookup(FindRelatedBusinessesHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    FindRelatedBusinesses request = new FindRelatedBusinesses();
    request.setBusinessKey("10ad2903-932f-49fe-aaed-bf80d0ed50f0");
    request.addFindQualifier(new FindQualifier(FindQualifier.SORT_BY_DATE_ASC));
    request.addFindQualifier(new FindQualifier(FindQualifier.AND_ALL_KEYS));
    request.setMaxRows(43);
    request.setKeyedReference(new KeyedReference("uuid:8ff45356-acde-4a4c-86bf-f953611d20c6","catBagKeyName2","catBagKeyValue2"));

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