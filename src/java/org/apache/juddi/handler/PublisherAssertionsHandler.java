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

import org.apache.juddi.IRegistry;
import org.apache.juddi.datatype.KeyedReference;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.assertion.PublisherAssertion;
import org.apache.juddi.datatype.response.PublisherAssertions;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class PublisherAssertionsHandler extends AbstractHandler
{
  public static final String TAG_NAME = "publisherAssertions";

  private HandlerMaker maker = null;

  protected PublisherAssertionsHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    PublisherAssertions obj = new PublisherAssertions();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // We could use the generic attribute value to
    // determine which version of UDDI was used to
    // format the request XML. - Steve

    // Attributes
    obj.setGeneric(element.getAttribute("generic"));
    obj.setOperator(element.getAttribute("operator"));

    // We can ignore the xmlns attribute since we
    // can always determine it's value using the
    // "generic" attribute. - Steve

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,PublisherAssertionHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(PublisherAssertionHandler.TAG_NAME);
      obj.addPublisherAssertion((PublisherAssertion)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    PublisherAssertions assertions = (PublisherAssertions)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    String generic = assertions.getGeneric();
    if ((generic != null) && (generic.trim().length() > 0))
    {
      element.setAttribute("generic",generic);

      if (generic.equals(IRegistry.UDDI_V1_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V1_NAMESPACE);
      else if (generic.equals(IRegistry.UDDI_V2_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V2_NAMESPACE);
      else if (generic.equals(IRegistry.UDDI_V3_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V3_NAMESPACE);
    }
    else // Default to UDDI v2 values
    {
      element.setAttribute("generic",IRegistry.UDDI_V2_GENERIC);
      element.setAttribute("xmlns",IRegistry.UDDI_V2_NAMESPACE);
    }

    String operator = assertions.getOperator();
    if (operator != null)
      element.setAttribute("operator",operator);
    else
      element.setAttribute("operator","");

    Vector vector = assertions.getPublisherAssertionVector();
    if ((vector!=null) && (vector.size() > 0))
    {
      handler = maker.lookup(PublisherAssertionHandler.TAG_NAME);
      for (int i=0; i < vector.size(); i++)
        handler.marshal((PublisherAssertion)vector.elementAt(i),element);
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
    AbstractHandler handler = maker.lookup(PublisherAssertionsHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    PublisherAssertion assertion = new PublisherAssertion();
    assertion.setFromKey("b2f072e7-6013-4385-93b4-9c1c2ece1c8f");
    assertion.setToKey("115be72d-0c04-4b5f-a729-79a522629c19");
    assertion.setKeyedReference(new KeyedReference("uuid:8ff45356-acde-4a4c-86bf-f953611d20c6","catBagKeyName2","catBagKeyValue2"));

    PublisherAssertions assertions = new PublisherAssertions();
    assertions.setGeneric("2.0");
    assertions.setOperator("jUDDI.org");
    assertions.addPublisherAssertion(assertion);
    assertions.addPublisherAssertion(assertion);

    System.out.println();

    RegistryObject regObject = assertions;
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