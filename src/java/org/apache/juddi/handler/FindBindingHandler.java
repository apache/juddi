/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    String key = request.getServiceKey();
    if (key != null)
      element.setAttribute("serviceKey",key);

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