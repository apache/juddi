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
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.DispositionReport;
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of DispositionReport objects.
 * Returns DispositionReport."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class DispositionReportHandler extends AbstractHandler
{
  public static final String TAG_NAME = "dispositionReport";

  private HandlerMaker maker = null;

  protected DispositionReportHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    DispositionReport obj = new DispositionReport();
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
    nodeList = XMLUtils.getChildElementsByTagName(element,ResultHandler.TAG_NAME);
    for (int i=0; i<nodeList.size(); i++)
    {
      handler = maker.lookup(ResultHandler.TAG_NAME);
      obj.addResult((Result)handler.unmarshal((Element)nodeList.elementAt(i)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    DispositionReport report = (DispositionReport)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    // We could use the generic value to determine which
    // version of UDDI we should follow to format the
    // response XML. For now we'll simply send back a
    // UDDI 2.0 formatted response.  - Steve

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

    Vector vector = report.getResultVector();
    if (vector!=null)
    {
      handler = maker.lookup(ResultHandler.TAG_NAME);
      for (int i=0; i < vector.size(); i++)
      {
        Result result = (Result)vector.elementAt(i);
        handler.marshal(result,element);
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
    AbstractHandler handler = maker.lookup(DispositionReportHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    ErrInfo errInfo = new ErrInfo();
    errInfo.setErrCode("E_accountLimitExceeded");
    errInfo.setErrMsg("Authentication token information has timed out.");

    Result result = new Result();
    result.setErrno(10160);
    result.setErrInfo(errInfo);

    ErrInfo errInfo2 = new ErrInfo();
    errInfo2.setErrCode("E_success");
    errInfo2.setErrMsg(null);

    Result result2 = new Result();
    result2.setErrno(Result.E_SUCCESS);
    result2.setErrInfo(errInfo2);

    DispositionReport report = new DispositionReport();
    report.setGeneric(IRegistry.UDDI_V2_GENERIC);
    report.setOperator("jUDDI.org");
    report.addResult(result);
    report.addResult(result2);

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

    System.out.println();
  }
}