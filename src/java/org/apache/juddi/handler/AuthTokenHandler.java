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
import org.apache.juddi.datatype.request.AuthInfo;
import org.apache.juddi.datatype.response.AuthToken;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class AuthTokenHandler extends AbstractHandler
{
  public static final String TAG_NAME = "authToken";

  private HandlerMaker maker = null;

  public AuthTokenHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    AuthToken obj = new AuthToken();
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
    nodeList = XMLUtils.getChildElementsByTagName(element,AuthInfoHandler.TAG_NAME);
    if (nodeList.size()> 0)
    {
      handler = maker.lookup(AuthInfoHandler.TAG_NAME);
      obj.setAuthInfo((AuthInfo)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    AuthToken authToken = (AuthToken)object;
    Element element = parent.getOwnerDocument().createElement(AuthTokenHandler.TAG_NAME);
    AbstractHandler handler = null;

    String generic = authToken.getGeneric();
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

    String operator = authToken.getOperator();
    if (operator != null)
      element.setAttribute("operator",operator);

    AuthInfo authInfo = authToken.getAuthInfo();
    if (authInfo != null)
    {
      handler = maker.lookup(AuthInfoHandler.TAG_NAME);
      handler.marshal(authInfo,element);
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
    AbstractHandler handler = maker.lookup(AuthTokenHandler.TAG_NAME);
    Element parent = XMLUtils.newRootElement();
    Element child = null;
    
    AuthToken object = new AuthToken();
    object.setAuthInfo(new AuthInfo("authToken:c9613c3c-fe55-4f34-a3da-b3167afbca4a"));
    object.setGeneric(IRegistry.UDDI_V2_GENERIC);
    object.setOperator("jUDDI.org");
    
    System.out.println();
    
    RegistryObject regObject = object;
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