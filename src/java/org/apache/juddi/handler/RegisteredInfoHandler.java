/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "jUDDI" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
import org.apache.juddi.registry.Registry;
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
    Element element = parent.getOwnerDocument().createElement(TAG_NAME);
    AbstractHandler handler = null;

    String generic = info.getGeneric();
    if (generic != null)
    {
      element.setAttribute("generic",generic);

      if (generic.equals(Registry.UDDI_V1_GENERIC))
        element.setAttribute("xmlns",Registry.UDDI_V1_NAMESPACE);
      else if (generic.equals(Registry.UDDI_V2_GENERIC))
        element.setAttribute("xmlns",Registry.UDDI_V2_NAMESPACE);
      else if (generic.equals(Registry.UDDI_V3_GENERIC))
        element.setAttribute("xmlns",Registry.UDDI_V3_NAMESPACE);
    }

    String operator = info.getOperator();
    if (operator != null)
      element.setAttribute("operator",operator);

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