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
import org.apache.juddi.datatype.OverviewDoc;
import org.apache.juddi.datatype.OverviewURL;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of OverviewDoc objects.
 * Returns OverviewDoc."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class OverviewDocHandler extends AbstractHandler
{
  public static final String TAG_NAME = "overviewDoc";

  private HandlerMaker maker = null;

  protected OverviewDocHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    OverviewDoc obj = new OverviewDoc();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    // {none}

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,DescriptionHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      Description descr = (Description)handler.unmarshal((Element)nodeList.elementAt(i));
      if (descr != null)
        obj.addDescription(descr);
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,OverviewURLHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(OverviewURLHandler.TAG_NAME);
      obj.setOverviewURL((OverviewURL)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    OverviewDoc overDoc = (OverviewDoc)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    Vector descrVector = overDoc.getDescriptionVector();
    if ((descrVector!=null) && (descrVector.size() > 0))
    {
      handler = maker.lookup(DescriptionHandler.TAG_NAME);
      for (int i=0; i < descrVector.size(); i++)
        handler.marshal((Description)descrVector.elementAt(i),element);
    }

    OverviewURL overURL = overDoc.getOverviewURL();
    if (overURL != null)
    {
      handler = maker.lookup(OverviewURLHandler.TAG_NAME);
      handler.marshal(overURL,element);
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