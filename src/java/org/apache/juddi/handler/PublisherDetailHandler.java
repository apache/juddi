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
import org.apache.juddi.datatype.publisher.Publisher;
import org.apache.juddi.datatype.response.PublisherDetail;
import org.apache.juddi.registry.IRegistry;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of ServiceDetail objects.
 * Returns ServiceDetail."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class PublisherDetailHandler extends AbstractHandler
{
  public static final String TAG_NAME = "publisherDetail";

  private HandlerMaker maker = null;

  protected PublisherDetailHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    PublisherDetail obj = new PublisherDetail();
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

    String truncValue = element.getAttribute("truncated");
    if (truncValue != null)
      obj.setTruncated(truncValue.equalsIgnoreCase("true"));

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,PublisherHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(PublisherHandler.TAG_NAME);
      obj.addPublisher((Publisher)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    PublisherDetail detail = (PublisherDetail)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    String generic = detail.getGeneric();
    if (generic != null)
    {
      element.setAttribute("generic",generic);
      element.setAttribute("xmlns",IRegistry.JUDDI_V1_NAMESPACE);
    }

    String operator = detail.getOperator();
    if (operator != null)
      element.setAttribute("operator",operator);

    boolean truncated = detail.isTruncated();
    if (truncated)
      element.setAttribute("truncated","true");

    Vector vector = detail.getPublisherVector();
    if ((vector!=null) && (vector.size() > 0))
    {
      handler = maker.lookup(PublisherHandler.TAG_NAME);
      for (int i=0; i < vector.size(); i++)
        handler.marshal((Publisher)vector.elementAt(i),element);
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
    AbstractHandler handler = maker.lookup(PublisherDetailHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    Publisher publisher = new Publisher();
    publisher.setPublisherID("bcrosby");
    publisher.setName("Bing Crosby");
    publisher.setLastName("Crosby");
    publisher.setFirstName("Bing");
    publisher.setWorkPhone("978.123-4567");
    publisher.setMobilePhone("617-765-9876");
    publisher.setPager("800-123-4655 ID: 501");
    publisher.setEmailAddress("bcrosby@juddi.org");
    publisher.setAdmin(true);

    PublisherDetail detail = new PublisherDetail();
    detail.setGeneric("1.0");
    detail.setOperator("jUDDI.org");
    detail.setTruncated(false);
    detail.addPublisher(publisher);
    detail.addPublisher(publisher);

    System.out.println();

    RegistryObject regObject = detail;
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