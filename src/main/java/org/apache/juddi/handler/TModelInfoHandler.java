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

import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.TModelInfo;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * TModelInfosHandler
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelInfoHandler extends AbstractHandler
{
  public static final String TAG_NAME = "tModelInfo";

  private HandlerMaker maker = null;

  protected TModelInfoHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    TModelInfo obj = new TModelInfo();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    obj.setTModelKey(element.getAttribute("tModelKey"));

    // Text Node Value
    // [none]

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,NameHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(NameHandler.TAG_NAME);
      Name name = (Name )handler.unmarshal((Element)nodeList.elementAt(0));
      if (name != null)
        obj.setName(name);    
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    TModelInfo info = (TModelInfo)object;
    String generic = getGeneric(null);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);
    AbstractHandler handler = null;

    String tModelKey = info.getTModelKey();
    if (tModelKey != null)
      element.setAttribute("tModelKey",tModelKey);

    Name name = info.getName();
    if (name != null)
    {
      handler = maker.lookup(NameHandler.TAG_NAME);
      handler.marshal(name,element);
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