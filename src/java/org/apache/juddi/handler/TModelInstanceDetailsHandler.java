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
import org.apache.juddi.datatype.binding.TModelInstanceDetails;
import org.apache.juddi.datatype.binding.TModelInstanceInfo;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of TModelInstanceDetails objects.
 * Returns TModelInstanceDetails."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelInstanceDetailsHandler extends AbstractHandler
{
  public static final String TAG_NAME = "tModelInstanceDetails";

  private HandlerMaker maker = null;

  protected TModelInstanceDetailsHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    TModelInstanceDetails obj = new TModelInstanceDetails();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    // {none}

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,TModelInstanceInfoHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(TModelInstanceInfoHandler.TAG_NAME);
      obj.addTModelInstanceInfo((TModelInstanceInfo)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    TModelInstanceDetails details = (TModelInstanceDetails)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    Vector infoVector = details.getTModelInstanceInfoVector();
    if ((infoVector!=null) && (infoVector.size() > 0))
    {
      handler = maker.lookup(TModelInstanceInfoHandler.TAG_NAME);
      for (int i=0; i < infoVector.size(); i++)
        handler.marshal((TModelInstanceInfo)infoVector.elementAt(i),element);
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