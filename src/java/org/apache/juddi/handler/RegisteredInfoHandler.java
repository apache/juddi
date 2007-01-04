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

import org.apache.juddi.datatype.Description;
import org.apache.juddi.datatype.Name;
import org.apache.juddi.datatype.RegistryObject;
import org.apache.juddi.datatype.response.BusinessInfo;
import org.apache.juddi.datatype.response.BusinessInfos;
import org.apache.juddi.datatype.response.RegisteredInfo;
import org.apache.juddi.datatype.response.ServiceInfo;
import org.apache.juddi.datatype.response.TModelInfo;
import org.apache.juddi.datatype.response.TModelInfos;
import org.apache.juddi.util.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * RegisteredInfoHandler
 *
 * @author Steve Viens (sviens@apache.org)
 * @author Anou Mana (anou_mana@users.sourceforge.net)
 */
public class RegisteredInfoHandler extends AbstractHandler
{
  public static final String TAG_NAME = "registeredInfo";

  private HandlerMaker maker = null;

  protected RegisteredInfoHandler(HandlerMaker maker)
  {
    this.maker = maker;
  }

  public RegistryObject unmarshal(Element element)
  {
    RegisteredInfo obj = new RegisteredInfo();
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
    nodeList = XMLUtils.getChildElementsByTagName(element,BusinessInfosHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(BusinessInfosHandler.TAG_NAME);
      obj.setBusinessInfos((BusinessInfos)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    nodeList = XMLUtils.getChildElementsByTagName(element,TModelInfosHandler.TAG_NAME);
    if (nodeList.size() > 0)
    {
      handler = maker.lookup(TModelInfosHandler.TAG_NAME);
      obj.setTModelInfos((TModelInfos)handler.unmarshal((Element)nodeList.elementAt(0)));
    }

    return obj;
  }

  public void marshal(RegistryObject object,Element parent)
  {
    RegisteredInfo info = (RegisteredInfo)object;
    String generic = info.getGeneric();
    generic = getGeneric(generic);
    String namespace = getUDDINamespace(generic);
    Element element = parent.getOwnerDocument().createElementNS(namespace,TAG_NAME);
    AbstractHandler handler = null;

    element.setAttribute("generic",generic);

    String operator = info.getOperator();
    if (operator != null)
      element.setAttribute("operator",operator);
    else
      element.setAttribute("operator","");

    BusinessInfos bInfos = info.getBusinessInfos();
    if (bInfos!=null)
    {
      handler = maker.lookup(BusinessInfosHandler.TAG_NAME);
      handler.marshal(bInfos,element);
    }

    TModelInfos tInfos = info.getTModelInfos();
    if (tInfos!=null)
    {
      handler = maker.lookup(TModelInfosHandler.TAG_NAME);
      handler.marshal(tInfos,element);
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
    AbstractHandler handler = maker.lookup(RegisteredInfoHandler.TAG_NAME);

    Element parent = XMLUtils.newRootElement();
    Element child = null;

    ServiceInfo sInfo = new ServiceInfo();
    sInfo.addName(new Name("regInfoServNm"));
    sInfo.addName(new Name("regInfoServNm2","en"));
    sInfo.setServiceKey("826e6443-e3c5-442b-9bf8-943071ca83f4");
    sInfo.setBusinessKey("56644a68-8779-46c7-93ce-90eeb7569f3f");

    BusinessInfo bInfo = new BusinessInfo();
    bInfo.setBusinessKey("56644a68-8779-46c7-93ce-90eeb7569f3f");
    bInfo.addName(new Name("regInfoBizNm"));
    bInfo.addName(new Name("regInfoBizNm2","en"));
    bInfo.addDescription(new Description("regInfoBiz whatever"));
    bInfo.addDescription(new Description("regInfoBiz whatever too","fr"));
    bInfo.addServiceInfo(sInfo);

    BusinessInfos bInfos = new BusinessInfos();
    bInfos.addBusinessInfo(bInfo);
    bInfos.addBusinessInfo(bInfo);

    TModelInfo tInfo = new TModelInfo();
    tInfo.setTModelKey("uuid:e86bd2a9-03f6-44c4-b619-400ef2cd7e47");
    tInfo.setName(new Name("RegInfoTestDriver"));

    TModelInfos tInfos = new TModelInfos();
    tInfos.addTModelInfo(tInfo);
    tInfos.addTModelInfo(tInfo);

    RegisteredInfo info = new RegisteredInfo();
    info.setGeneric("2.0");
    info.setOperator("jUDDI.org");
    info.setBusinessInfos(bInfos);
    info.setTModelInfos(tInfos);

    System.out.println();

    RegistryObject regObject = info;
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