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
import org.apache.juddi.datatype.TModelBag;
import org.apache.juddi.datatype.TModelKey;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of TModelBag objects.
 * Returns TModelBag."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class TModelBagHandler extends AbstractHandler
{
  public static final String TAG_NAME = "tModelBag";

  private HandlerMaker maker = null;

  protected TModelBagHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    TModelBag obj = new TModelBag();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    // {none}

   // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,TModelKeyHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(TModelKeyHandler.TAG_NAME);
      obj.addTModelKey((TModelKey)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    TModelBag bag = (TModelBag)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    Vector keyVector = bag.getTModelKeyVector();
    if ((keyVector!=null) && (keyVector.size() > 0))
    {
      handler = maker.lookup(TModelKeyHandler.TAG_NAME);
      for (int i=0; i < keyVector.size(); i++)
        handler.marshal(new TModelKey((String)keyVector.elementAt(i)),element);
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
    AbstractHandler handler = maker.lookup(TModelBagHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    TModelBag bag = new TModelBag();
    bag.addTModelKey("uuid:35d9793b-9911-4b85-9f0e-5d4c65b4f253");
    bag.addTModelKey(new TModelKey("uuid:c5ab113f-0d6b-4247-b3c4-8c012726acd8"));

    System.out.println();

    RegistryObject regObject = bag;
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