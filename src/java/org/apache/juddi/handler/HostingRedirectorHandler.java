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

import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.binding.HostingRedirector;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of HostingRedirector objects.
 * Returns HostingRedirector."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class HostingRedirectorHandler extends AbstractHandler
{
  public static final String TAG_NAME = "hostingRedirector";

  private HandlerMaker maker = null;

  protected HostingRedirectorHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    HostingRedirector obj = new HostingRedirector();

    // Attributes
    obj.setBindingKey(element.getAttribute("bindingKey"));

    // Text Node Value
    // {none}

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    HostingRedirector redirector = (HostingRedirector)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);

    String bindingKey = redirector.getBindingKey();
    if (bindingKey != null)
      element.setAttribute("bindingKey",bindingKey);

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(HostingRedirectorHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    HostingRedirector redirector =  new HostingRedirector();
    redirector.setBindingKey("92658289-0bd7-443c-8948-0bb4460b44c0");

    System.out.println();

    RegistryObject regObject = redirector;
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

    System.out.println();
  }
}