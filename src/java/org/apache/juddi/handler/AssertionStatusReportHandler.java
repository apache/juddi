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
import org.apache.juddi.datatype.response.AssertionStatusItem;
import org.apache.juddi.datatype.response.AssertionStatusReport;
import org.apache.juddi.datatype.response.CompletionStatus;
import org.apache.juddi.datatype.response.KeysOwned;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class AssertionStatusReportHandler extends AbstractHandler
{
  public static final String TAG_NAME = "assertionStatusReport";

  private HandlerMaker maker = null;

  protected AssertionStatusReportHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    AssertionStatusReport obj = new AssertionStatusReport();
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
    nodeList = XMLUtils.getChildElementsByTagName(element,AssertionStatusItemHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(AssertionStatusItemHandler.TAG_NAME);
      obj.addAssertionStatusItem((AssertionStatusItem)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    AssertionStatusReport report = (AssertionStatusReport)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    String generic = report.getGeneric();
    if (generic != null)
    {
      element.setAttribute("generic",generic);

      if (generic.equals(IRegistry.UDDI_V1_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V1_NAMESPACE);
      else if (generic.equals(IRegistry.UDDI_V2_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V2_NAMESPACE);
      else if (generic.equals(IRegistry.UDDI_V3_GENERIC))
        element.setAttribute("xmlns",IRegistry.UDDI_V3_NAMESPACE);
    }

    String operator = report.getOperator();
    if (operator != null)
      element.setAttribute("operator",operator);

    Vector vector = report.getAssertionStatusItemVector();
    if ((vector!=null) && (vector.size() > 0))
    {
      handler = maker.lookup(AssertionStatusItemHandler.TAG_NAME);
      for (int i=0; i<vector.size(); i++)
        handler.marshal((AssertionStatusItem)vector.elementAt(i),element);
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
    AbstractHandler handler = maker.lookup(AssertionStatusReportHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;

    KeysOwned keysOwned = new KeysOwned();
    keysOwned.setFromKey("fe8af00d-3a2c-4e05-b7ca-95a1259aad4f");
    keysOwned.setToKey("dfddb58c-4853-4a71-865c-f84509017cc7");

    AssertionStatusItem item = new AssertionStatusItem();
    item.setFromKey("986d9a16-5d4d-46cf-9fac-6bb80a7091f6");
    item.setToKey("dd45a24c-74fc-4e82-80c2-f99cdc76d681");
    item.setKeyedReference(new KeyedReference("uuid:8ff45356-acde-4a4c-86bf-f953611d20c6","Subsidiary","1"));
    item.setCompletionStatus(new CompletionStatus(CompletionStatus.COMPLETE));
    item.setKeysOwned(keysOwned);

    AssertionStatusReport report = new AssertionStatusReport();
    report.setGeneric("2.0");
    report.setOperator("jUDDI.org");
    report.addAssertionStatusItem(item);
    report.addAssertionStatusItem(item);

    System.out.println();

    RegistryObject regObject = report;
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