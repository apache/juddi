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
import org.apache.juddi.datatype.response.RelatedBusinessInfo;
import org.apache.juddi.datatype.response.RelatedBusinessInfos;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class RelatedBusinessInfosHandler extends AbstractHandler
{
  public static final String TAG_NAME = "relatedBusinessInfos";

  private HandlerMaker maker = null;

  protected RelatedBusinessInfosHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    RelatedBusinessInfos obj = new RelatedBusinessInfos();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    // {none}

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,RelatedBusinessInfoHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(RelatedBusinessInfoHandler.TAG_NAME);
      obj.addRelatedBusinessInfo((RelatedBusinessInfo)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    RelatedBusinessInfos infos = (RelatedBusinessInfos)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    Vector vector = infos.getRelatedBusinessInfoVector();
    if ((vector!=null) && (vector.size() > 0))
    {
      handler = maker.lookup(RelatedBusinessInfoHandler.TAG_NAME);
      for (int i=0; i < vector.size(); i++)
        handler.marshal((RelatedBusinessInfo)vector.elementAt(i),element);
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
    AbstractHandler handler = maker.lookup(RelatedBusinessInfosHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    RelatedBusinessInfos infos = new RelatedBusinessInfos();
    infos.addRelatedBusinessInfo(new RelatedBusinessInfo());
    infos.addRelatedBusinessInfo(new RelatedBusinessInfo());

    System.out.println();

    RegistryObject regObject = infos;
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