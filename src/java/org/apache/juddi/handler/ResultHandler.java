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
import org.apache.juddi.datatype.response.ErrInfo;
import org.apache.juddi.datatype.response.Result;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * "Knows about the creation and populating of Result objects.
 * Returns Result."
 *
 * @author Steve Viens (sviens@apache.org)
 */
public class ResultHandler extends AbstractHandler
{
  public static final String TAG_NAME = "result";

  private HandlerMaker maker = null;

  protected ResultHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    Result obj = new Result();
    Vector nodeList = null;
    AbstractHandler handler = null;

    // Attributes
    obj.setErrno(element.getAttribute("errno"));

    // Text Node Value
    // {none}

    // Child Elements
    nodeList = XMLUtils.getChildElementsByTagName(element,ErrInfoHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(ErrInfoHandler.TAG_NAME);
      obj.setErrInfo((ErrInfo)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    Result result = (Result)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);
    AbstractHandler handler = null;

    element.setAttribute("errno",String.valueOf(result.getErrno()));

    ErrInfo errInfo = result.getErrInfo();
    if (errInfo!=null)
    {
      handler = maker.lookup(ErrInfoHandler.TAG_NAME);
      handler.marshal(errInfo,element);
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
    AbstractHandler handler = maker.lookup(ResultHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    ErrInfo errInfo = new ErrInfo();
    errInfo.setErrCode(Result.E_ACCOUNT_LIMIT_EXCEEDED_CODE);
    errInfo.setErrMsg(Result.E_ACCOUNT_LIMIT_EXCEEDED_MSG);

    Result result = new Result();
    result.setErrno(Result.E_ACCOUNT_LIMIT_EXCEEDED);
    result.setErrInfo(errInfo);

    System.out.println();

    RegistryObject regObject = result;
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