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

import java.util.Iterator;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.Property;
import org.apache.juddi.datatype.response.RegistryInfo;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryInfoHandler extends AbstractHandler
{
  public static final String TAG_NAME = "registryInfo";

  private HandlerMaker maker = null;

  protected RegistryInfoHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    RegistryInfo obj = new RegistryInfo();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    // {none}

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,PropertyHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(PropertyHandler.TAG_NAME);
      obj.addProperty((Property)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    RegistryInfo info = (RegistryInfo)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    Properties props = info.getProperties();
    if ((props!=null) && (props.size() > 0))
    {
      handler = maker.lookup(PropertyHandler.TAG_NAME);

      SortedSet sortedProps = new TreeSet(props.keySet());
      for (Iterator keys = sortedProps.iterator(); keys.hasNext() ; )
      {
        String name = (String)keys.next();
        String value = (String)props.getProperty(name);
        handler.marshal(new Property(name,value),element);
      }
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
    AbstractHandler handler = maker.lookup(RegistryInfoHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    RegistryInfo regInfo = new RegistryInfo();
    regInfo.addProperty(new Property("first_name","Steve"));
    regInfo.addProperty(new Property("last_name","Viens"));
    regInfo.addProperty(new Property("version","1.0b1"));

    System.out.println();

    RegistryObject regObject = regInfo;
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