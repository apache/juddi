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
import org.apache.juddi.datatype.response.CompletionStatus;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class CompletionStatusHandler extends AbstractHandler
{
  public static final String TAG_NAME = "completionStatus";

  private HandlerMaker maker = null;

  protected CompletionStatusHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    CompletionStatus obj = new CompletionStatus();

    // Attributes
    // {none}

    // Text Node Value
    obj.setValue(XMLUtils.getText(element));

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    CompletionStatus status = (CompletionStatus)object;
    Element element = parent.getOwnerDocument().createElementNS(null,TAG_NAME);

    String statusValue = status.getValue();
    if (statusValue != null)
      element.appendChild(parent.getOwnerDocument().createTextNode(statusValue));

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(CompletionStatusHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    CompletionStatus status = new CompletionStatus();
    status.setValue(CompletionStatus.FROMKEY_INCOMPLETE);

    System.out.println();

    RegistryObject regObject = status;
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