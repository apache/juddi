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

import org.apache.juddi.datatype.Address;
import org.apache.juddi.datatype.AddressLine;
import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.Email;
import org.apache.juddi.datatype.Phone;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.business.Contact;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.error.RegistryException;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Knows about the creation and populating of RegistryException
 * objects.
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class RegistryExceptionHandler extends AbstractHandler
{
  public static final String TAG_NAME = "fault";

  private HandlerMaker maker = null;

  protected RegistryExceptionHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    RegistryException obj = new RegistryException();
    Vector nodeList = null;
    AbstractHandler handler = null;

    nodeList = XMLUtils.getChildElementsByTagName(element,"faultcode");
    if (nodeList.size() > 0)
      obj.setFaultCode(XMLUtils.getText((Element)nodeList.elementAt(0)));

    nodeList = XMLUtils.getChildElementsByTagName(element,"faultstring");
    if (nodeList.size() > 0)
      obj.setFaultString(XMLUtils.getText((Element)nodeList.elementAt(0)));

    nodeList = XMLUtils.getChildElementsByTagName(element,"faultactor");
    if (nodeList.size() > 0)
      obj.setFaultActor(XMLUtils.getText((Element)nodeList.elementAt(0)));

    nodeList = XMLUtils.getChildElementsByTagName(element,"detail");
    if (nodeList.size() > 0)
    {
      nodeList = XMLUtils.getChildElementsByTagName((Element)nodeList.elementAt(0),DispositionReportHandler.TAG_NAME);
      if (nodeList.size() > 0)
      {
        handler = maker.lookup(DispositionReportHandler.TAG_NAME);
        obj.setDispositionReport((DispositionReport)handler.unmarshal((Element)nodeList.elementAt(0)));
      }
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    RegistryException regEx = (RegistryException)object;
    Document document = parent.getOwnerDocument();
    Element fault = document.createElementNS(null,TAG_NAME);

    String fCode = regEx.getFaultCode();
    if (fCode != null)
    {
      Element fCodeElement = document.createElement("faultcode");
      fCodeElement.appendChild(document.createTextNode(fCode));
      fault.appendChild(fCodeElement);
    }

    String fString = regEx.getFaultString();
    if (fString == null)
      fString = "";

    Element fStringElement = document.createElement("faultstring");
    fStringElement.appendChild(document.createTextNode(fString));
    fault.appendChild(fStringElement);

    String fActor = regEx.getFaultActor();
    if (fActor != null)
    {
      Element fActorElement = document.createElement("faultactor");
      fActorElement.appendChild(document.createTextNode(fActor));
      fault.appendChild(fActorElement);
    }

    // check for a DispositionReport in the exception and if one exists,
    // grab it, marshal it into xml and stuff it into a SOAP fault
    // detail element.

    DispositionReport dispRpt = regEx.getDispositionReport();
    if (dispRpt != null)
    {
      Element fDetailElement = document.createElement("detail");
      IHandler handler = maker.lookup(DispositionReportHandler.TAG_NAME);
      handler.marshal(dispRpt,fDetailElement);
      fault.appendChild(fDetailElement);
    }

    parent.appendChild(fault);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(RegistryExceptionHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    Address address = new Address();
    address.setUseType("myAddressUseType");
    address.setSortCode("sortThis");
    address.setTModelKey(null);
    address.addAddressLine(new AddressLine("AddressLine1","keyNameAttr","keyValueAttr"));
    address.addAddressLine(new AddressLine("AddressLine2"));

    Contact contact = new Contact();
    //contact.setUseType("myAddressUseType");
    contact.setPersonNameValue("Bob Whatever");
    contact.addDescription(new Description("Bob is a big fat jerk"));
    contact.addDescription(new Description("obBay sIay a igBay atFay erkJay","es"));
    contact.addEmail(new Email("bob@whatever.com"));
    contact.addPhone(new Phone("(603)559-1901"));
    contact.addAddress(address);

    System.out.println();

    RegistryObject regObject = contact;
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