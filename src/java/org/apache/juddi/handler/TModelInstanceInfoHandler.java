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
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.binding.InstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceInfo;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class TModelInstanceInfoHandler extends AbstractHandler
{
  public static final String TAG_NAME = "tModelInstanceInfo";

  private HandlerMaker maker = null;

  protected TModelInstanceInfoHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    TModelInstanceInfo obj = new TModelInstanceInfo();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    obj.setTModelKey(element.getAttribute("tModelKey"));

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,DescriptionHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      obj.addDescription((Description)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,InstanceDetailsHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(InstanceDetailsHandler.TAG_NAME);
      obj.setInstanceDetails((InstanceDetails)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    TModelInstanceInfo tModInstInfo = (TModelInstanceInfo)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    String tModelKey = tModInstInfo.getTModelKey();
    if (tModelKey != null)
      element.setAttribute("tModelKey",tModelKey);

  Vector descrVector = tModInstInfo.getDescriptionVector();
  if ((descrVector!=null) && (descrVector.size() > 0))
  {
    handler = maker.lookup(DescriptionHandler.TAG_NAME);
    for (int i=0; i < descrVector.size(); i++)
    handler.marshal((Description)descrVector.elementAt(i),element);
  }

   InstanceDetails instDet = tModInstInfo.getInstanceDetails();
    if (instDet != null)
    {
      handler = maker.lookup(InstanceDetailsHandler.TAG_NAME);
      handler.marshal(instDet,element);
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