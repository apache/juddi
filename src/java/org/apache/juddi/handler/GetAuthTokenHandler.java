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
import org.apache.juddi.datatype.request.GetAuthToken;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * @author Steve Viens (sviens@apache.org)
 */
public class GetAuthTokenHandler extends AbstractHandler
{
  public static final String TAG_NAME = "get_authToken";

  private HandlerMaker maker = null;

  protected GetAuthTokenHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    GetAuthToken obj = new GetAuthToken();

    // Attributes
    String userID = element.getAttribute("userID");
    if ((userID != null && (userID.trim().length() > 0)))
      obj.setUserID(userID);

    String credential = element.getAttribute("cred");
    if ((credential != null && (credential.trim().length() > 0)))
      obj.setCredential(credential);

    String generic = element.getAttribute("generic");
    if ((generic != null && (generic.trim().length() > 0)))
      obj.setGeneric(generic);

    // Text Node Value
    // {none}

    // Child Elements
    // {none}

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    GetAuthToken request = (GetAuthToken)object;
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);

    String cred = request.getCredential();
    if ((cred != null && (cred.length() > 0)))
      element.setAttribute("cred",cred);

    String userID = request.getUserID();
    if ((userID != null && (userID.length() > 0)))
      element.setAttribute("userID",userID);

    String generic = request.getGeneric();
    if (generic != null)
      element.setAttribute("generic",generic);

    parent.appendChild(element);
  }


  /***************************************************************************/
  /***************************** TEST DRIVER *********************************/
  /***************************************************************************/


  public static void main(String args[])
    throws Exception
  {
    HandlerMaker maker = HandlerMaker.getInstance();
    AbstractHandler handler = maker.lookup(GetAuthTokenHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    GetAuthToken request = new GetAuthToken();
    request.setUserID("sviens");
    request.setCredential("password");

    System.out.println();

    RegistryObject regObject = request;
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