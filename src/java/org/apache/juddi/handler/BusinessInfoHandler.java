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

import org.apache.juddi.datatype.Description;
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
      obj.addName((org.apache.juddi.datatype.Name)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,DescriptionHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      obj.addDescription((Description)handler.unmarshal((Element)nodeList.elementAt(i)));
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
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
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