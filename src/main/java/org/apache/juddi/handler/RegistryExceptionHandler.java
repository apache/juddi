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
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    // {none}

    // Text Node Value
    // {none}

    // Child Elements
    String fCode = null;
    nodeList = XMLUtils.getChildElementsByTagName(element,"faultcode");
    if (nodeList.size() > 0)
      fCode = XMLUtils.getText((Element)nodeList.elementAt(0));

    String fString = null;
    nodeList = XMLUtils.getChildElementsByTagName(element,"faultstring");
    if (nodeList.size() > 0)
      fString = XMLUtils.getText((Element)nodeList.elementAt(0));

    String fActor = null;
    nodeList = XMLUtils.getChildElementsByTagName(element,"faultactor");
    if (nodeList.size() > 0)
      fActor = XMLUtils.getText((Element)nodeList.elementAt(0));

    DispositionReport dispRpt = null;
    nodeList = XMLUtils.getChildElementsByTagName(element,"detail");
    if (nodeList.size() > 0)
    {
      nodeList = XMLUtils.getChildElementsByTagName((Element)nodeList.elementAt(0),DispositionReportHandler.TAG_NAME);
      if (nodeList.size() > 0)
      {
        handler = maker.lookup(DispositionReportHandler.TAG_NAME);
        dispRpt = ((DispositionReport)handler.unmarshal((Element)nodeList.elementAt(0)));
      }
    }

    // Create RegistryException instance and return
    RegistryException obj = new RegistryException(fCode,fString,fActor,dispRpt); 
    
    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    RegistryException regEx = (RegistryException)object;
    Document document = parent.getOwnerDocument();
    String generic = getGeneric(null);
    String namespace = getUDDINamespace(generic);
    Element fault = document.createElementNS(namespace,TAG_NAME);

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

    RegistryException regex = 
        new org.apache.juddi.error.AuthTokenRequiredException("Test Exception");

    System.out.println();

    RegistryObject regObject = regex;
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